package com.dolphin.adminbackend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Service;

import com.dolphin.adminbackend.creator.MetricCreator;
import com.dolphin.adminbackend.enums.MetricEventEnum;
import com.dolphin.adminbackend.enums.OrderStatus;
import com.dolphin.adminbackend.enums.TimeframeEnum;
import com.dolphin.adminbackend.event.AvgQuantityPerOrderEvent;
import com.dolphin.adminbackend.event.AvgRevenueMetricEvent;
import com.dolphin.adminbackend.event.RealTimeTrendsEvent;
import com.dolphin.adminbackend.event.TotalOrdersByDemographyEvent;
import com.dolphin.adminbackend.event.TotalOrdersMetricEvent;
import com.dolphin.adminbackend.event.TotalRevenueMetricEvent;
import com.dolphin.adminbackend.eventpublisher.DolphinEventPublisher;
import com.dolphin.adminbackend.model.dto.pojo.Timeframe;
import com.dolphin.adminbackend.model.dto.queryresult.RevenueByCategoryOverTime;
import com.dolphin.adminbackend.model.dto.queryresult.TotalOrdersByDemography;
import com.dolphin.adminbackend.model.dto.request.OrderReq;
import com.dolphin.adminbackend.model.dto.supplier.Line;
import com.dolphin.adminbackend.model.dto.supplier.TimeframedAmount;
import com.dolphin.adminbackend.model.jpa.Category;
import com.dolphin.adminbackend.model.jpa.Customer;
import com.dolphin.adminbackend.model.jpa.Order;
import com.dolphin.adminbackend.model.jpa.OrderItem;
import com.dolphin.adminbackend.model.jpa.Payment;
import com.dolphin.adminbackend.model.jpa.Product;
import com.dolphin.adminbackend.repository.CustomerRepo;
import com.dolphin.adminbackend.repository.OrderRepo;
import com.dolphin.adminbackend.repository.PaymentRepo;
import com.dolphin.adminbackend.repository.ProductRepo;
import com.dolphin.adminbackend.utility.ColorUtility;
import com.dolphin.adminbackend.utility.DateUtility;
import com.dolphin.adminbackend.utility.EnumUtility;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private CustomerRepo custRepo;

    @Autowired
    private ProductRepo prodRepo;

    @Autowired
    private DolphinEventPublisher metricEventPublisher;

    @Autowired
    private EnumUtility enumUtility;

    /*
     * org.springframework.beans.factory.UnsatisfiedDependencyException:
     * Requested bean is currently in creation: Is there an unresolvable circular
     * reference or an asynchronous
     * initialization dependency?
     * 
     * This error indicates a circular dependency issue in the Spring application,
     * where two or more beans depend on each other in a way that Spring cannot
     * resolve. Specifically, the stack trace shows that:
     * orderController → depends on orderService
     * orderService → depends on webSocketController
     * webSocketController → depends on totalRevenueStatItem
     * totalRevenueStatItem → depends on orderService
     * This circular dependency causes Spring to enter an infinite loop during bean
     * creation, resulting in the error.
     * 
     * @Autowired
     * private WebSocketController webSocketController;
     * 
     */

    /**
     * Create a new order.
     *
     * @param order the order to create
     * @return the created order
     */
    public Order createOrder(OrderReq orderRequest) {

        Customer customer = null;
        Date orderDate = orderRequest.getOrderDate();
        Long customerId = orderRequest.getCustomerId();
        Optional<Customer> optionalCustomer = custRepo.findById(customerId);
        if (optionalCustomer.isPresent()) {
            customer = optionalCustomer.get();
        } else {
            throw new EntityNotFoundException("customerId " + customerId + " not found");
        }

        List<OrderItem> orderItems = orderRequest.getItems().stream()
                .map(itemRequest -> {
                    Long productId = itemRequest.getProductId();
                    Optional<Product> optProduct = prodRepo.findById(productId);
                    if (!optProduct.isPresent()) {
                        throw new EntityNotFoundException("productId " + productId + " not found");
                    }
                    return new OrderItem(
                            null, // ID is auto-generated
                            optProduct.get(), // in OrderItem, product is eargerly fetched so it won't be empty
                            itemRequest.getQuantity(),
                            itemRequest.getPricePerUnit(),
                            null // Order will be set later
                    );
                }).collect(Collectors.toList());

        // total order amount for all items
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem item : orderItems) {
            BigDecimal itemTotal = item.getPricePerUnit().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(orderDate);

        // Set the order reference for all items
        orderItems.forEach(item -> item.setOrder(order));

        Order savedOrder = orderRepo.save(order);
        TotalRevenueMetricEvent totalRevenueEvent = new TotalRevenueMetricEvent(this);
        TotalOrdersMetricEvent totalOrdersEvent = new TotalOrdersMetricEvent(this);
        AvgRevenueMetricEvent avgRevenueEvent = new AvgRevenueMetricEvent(this);
        AvgQuantityPerOrderEvent avgQuantityEvent = new AvgQuantityPerOrderEvent(this);
        RealTimeTrendsEvent revenuePerSecondEvent = new RealTimeTrendsEvent(this, orderDate);
        TotalOrdersByDemographyEvent ordersByDemographyEvent = new TotalOrdersByDemographyEvent(this);

        List<ApplicationEvent> events = new ArrayList<ApplicationEvent>();
        events.add(totalRevenueEvent);
        events.add(totalOrdersEvent);
        events.add(avgRevenueEvent);
        events.add(avgQuantityEvent);
        events.add(revenuePerSecondEvent);
        events.add(ordersByDemographyEvent);
        metricEventPublisher.publishMultiple(events);

        return savedOrder;
    }

    public List<TimeframedAmount> getTimeframedSingleAmounts(MetricEventEnum event) {
        List<TimeframedAmount> timeframedAmounts = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (TimeframeEnum timeframeEnum : TimeframeEnum.values()) {
            Timeframe timeframe = DateUtility.getStartOfTimeFrame(timeframeEnum, now);
            LocalDateTime currentStartLocalDate = timeframe.getCurrentStartDate();
            LocalDateTime prevStartLocalDate = timeframe.getPrevStartDate();
            LocalDateTime prevLocalLastSecond = timeframe.getPrevLastSecond();
            Date currentStartDate = DateUtility.convertToDate(currentStartLocalDate);
            Date previousStartDate = DateUtility.convertToDate(prevStartLocalDate);
            Date prevLastSecond = DateUtility.convertToDate(prevLocalLastSecond);
            Date currentDate = DateUtility.convertToDate(now);
            BigDecimal amount = null, previousAmount = null, rateOfChange = null;
            String startMessage = timeframe.getMessage();

            switch (event) {
                case TOTAL_ORDERS:
                    amount = getOrdersCountBetween(currentStartDate, currentDate).setScale(2, RoundingMode.HALF_UP);
                    previousAmount = getOrdersCountBetween(previousStartDate, prevLastSecond);
                    break;
                case TOTAL_REVENUE:
                    amount = getSumOfTotalAmountBetween(currentStartDate, currentDate).setScale(2,
                            RoundingMode.HALF_UP);
                    previousAmount = getSumOfTotalAmountBetween(previousStartDate, prevLastSecond);
                    break;
                case AVERAGE_QUANTITY:
                    amount = getAvgQuantityPerOrderBetween(currentStartDate, currentDate).setScale(2,
                            RoundingMode.HALF_UP);
                    previousAmount = getAvgQuantityPerOrderBetween(previousStartDate, prevLastSecond);
                    break;
                case AVERAGE_REVENUE:
                    amount = getAvgTotalAmountBetween(currentStartDate, currentDate).setScale(2, RoundingMode.HALF_UP);
                    previousAmount = getAvgTotalAmountBetween(previousStartDate, prevLastSecond);
                    break;
                default:
                    break;
            }

            if (previousAmount.compareTo(BigDecimal.ZERO) != 0) {
                rateOfChange = amount.divide(previousAmount, 10, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(0, RoundingMode.HALF_UP);
            }

            // log.info("timeframe: " + timeframeEnum.toString());
            // log.info("currentStartDate: " + currentStartDate);
            // log.info("previousStartDate: " + previousStartDate);
            // log.info("prevLastSecond: " + prevLastSecond);
            TimeframedAmount tfAmount = new TimeframedAmount(timeframeEnum, currentStartDate, previousStartDate, amount,
                    previousAmount, startMessage, rateOfChange);
            timeframedAmounts.add(tfAmount);
        }
        return timeframedAmounts;
    }

    public List<TimeframedAmount> getTimeframedTotalOrdersByDemography() {
        List<TimeframedAmount> timeframedAmounts = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (TimeframeEnum timeframeEnum : TimeframeEnum.values()) {
            Timeframe startFrame = DateUtility.getStartOfTimeFrame(timeframeEnum, now);
            LocalDateTime local = startFrame.getCurrentStartDate();
            Date startDate = DateUtility.convertToDate(local);
            Date endDate = DateUtility.convertToDate(now);
            List<List<Object>> series = null;
            List<TotalOrdersByDemography> totals = orderRepo.findTotalOrdersByDemographyBetween(startDate, endDate);
            Map<String, Long> demographyAndOrdersMap = new HashMap<>();
            Map<String, String> demographyAndColorMap = new HashMap<>();

            for (TotalOrdersByDemography total : totals) {
                String currentGender = total.getGender();
                String ageRange = DateUtility.getAgeRange(total.getAge());
                String demography = currentGender.concat(ageRange); // "MALE(31-40)"
                String demographyColor = ColorUtility.getDemographicColor(currentGender, ageRange);

                if (demographyAndOrdersMap.containsKey(demography)) {
                    demographyAndOrdersMap.put(demography, demographyAndOrdersMap.get(demography) + total.getCount());
                } else {
                    demographyAndOrdersMap.put(demography, total.getCount());
                    demographyAndColorMap.put(demography, demographyColor);
                }
            }

            // Automatically sorted with TreeSet
            Set<String> demographySet = new TreeSet<>(demographyAndOrdersMap.keySet());
            if (!demographySet.isEmpty()) {
                series = new ArrayList<>();
                for (String demography : demographySet) {
                    List<Object> demographyDataList = new ArrayList<>();
                    demographyDataList.add(demography);
                    demographyDataList.add(demographyAndOrdersMap.get(demography));
                    demographyDataList.add(demographyAndColorMap.get(demography));
                    series.add(demographyDataList);
                }
            }

            TimeframedAmount tfAmount = new TimeframedAmount(timeframeEnum, series);
            timeframedAmounts.add(tfAmount);
        }
        return timeframedAmounts;
    }

    public List<Line> getLastFewMinutesTrends(Date timeOccured) {
        // Get (x,y) coordinate of the line chart at this instant.
        List<Line> lines = new ArrayList<>();
        List<MetricEventEnum> eventEnums = enumUtility.getSingleAmountEventEnums();

        for (MetricEventEnum eventEnum : eventEnums) {
            Line line = null;
            boolean flagged = false;
            String lineColor = "#0A84FF", lineType = "areaspline";
            String yTitle = null, xTitle = "Time";
            String lineName = null, subMetricName = null, subMetricPrefix = null;
            BigDecimal metricAmount = null, subMetricAmount = null;
            switch (eventEnum) {
                case TOTAL_REVENUE:
                    metricAmount = orderRepo.findRevenueCurrentSecond(timeOccured);
                    subMetricAmount = orderRepo.findAvgRevenueCurrentMinute(timeOccured);
                    lineName = "Revenue Trend (Last Few Minutes)";
                    subMetricName = "avg/min";
                    subMetricPrefix = "RM";
                    yTitle = "RM";
                    xTitle = "Time";
                    flagged = true;
                    break;
                case TOTAL_ORDERS:
                    metricAmount = orderRepo.findOrdersCountCurrentSecond(timeOccured);
                    subMetricAmount = orderRepo.findAvgOrdersCountCurrentMinute(timeOccured).setScale(2,
                            RoundingMode.HALF_UP);
                    lineName = "Orders Trend (Last Few Minutes)";
                    subMetricName = "avg/min";
                    yTitle = "Orders";
                    xTitle = "Time";
                    flagged = true;
                    break;
                default:
                    break;
            }
            if (flagged) {
                line = createLine(
                        timeOccured,
                        metricAmount,
                        subMetricAmount,
                        lineType,
                        lineName,
                        eventEnum,
                        lineColor,
                        subMetricName,
                        subMetricPrefix,
                        yTitle,
                        xTitle);
                lines.add(line);
            }
        }
        return lines;
    }

    public Line createLine(Date currentDate, BigDecimal metricAmount, BigDecimal subMetricAmount, String lineType,
            String lineName, MetricEventEnum eventEnum, String lineColor, String subMetricName, String subMetricPrefix,
            String yTitle,
            String xTitle) {

        // Add data to line
        List<Object> point = new ArrayList<>();
        point.add(currentDate);
        if (metricAmount == null) {
            point.add(BigDecimal.ZERO);
        } else {
            point.add(metricAmount);
        }
        List<List<Object>> points = new ArrayList<>();
        points.add(point);

        // Add additional data to this line chart, but it's not part of the line.
        Map<String, Object> subMetric = new HashMap<>();
        if (subMetricAmount == null) {
            subMetricAmount = BigDecimal.ZERO;
        }
        subMetric.put("name", subMetricName);
        subMetric.put("data", subMetricAmount);
        subMetric.put("prefix", subMetricPrefix);
        Line line = new Line(lineType, lineName, eventEnum, lineColor, points, subMetric, yTitle, xTitle);
        return line;
    }

    public BigDecimal getOrdersCountBetween(Date startDate, Date endDate) {
        return BigDecimal.valueOf(orderRepo.findCountOfOrdersBetween(startDate, endDate));
    }

    public BigDecimal getSumOfTotalAmountBetween(Date startDate, Date endDate) {
        BigDecimal sum = orderRepo.findSumOfTotalAmountBetween(startDate, endDate);
        if (sum == null) {
            return BigDecimal.ZERO;
        }
        return sum;
    }

    public BigDecimal getAvgQuantityPerOrderBetween(Date startDate, Date endDate) {
        BigDecimal avg = orderRepo.findAvgQuantityPerOrderBetween(startDate, endDate);
        if (avg == null) {
            avg = BigDecimal.ZERO;
        }
        return avg;
    }

    public BigDecimal getAvgTotalAmountBetween(Date startDate, Date endDate) {
        BigDecimal avg = orderRepo.findAvgOfTotalAmountBetween(startDate, endDate);
        if (avg == null) {
            avg = BigDecimal.ZERO;
        }
        return avg;
    }

    // public List<Line> getRevenueByCategoryPerSecond() {
    // List<RevenueByCategoryOverTime> revenues = new ArrayList<>();
    // if (orderRepo.findRevenueByCategoryPerSecond().isEmpty()) {
    // List<Category> categories = orderRepo.findCategoriesInOrderItems();
    // for (Category c : categories) {
    // RevenueByCategoryOverTime revenue = new RevenueByCategoryOverTime(c.getId(),
    // c.getName(), new Date(), BigDecimal.ZERO, c.getLineColor());
    // revenues.add(revenue);
    // }
    // } else {
    // revenues = orderRepo.findRevenueByCategoryPerSecond();
    // }
    // Map<String, String> categoryNameAndColor = new HashMap<>();
    // for (RevenueByCategoryOverTime revenue : revenues) {
    // categoryNameAndColor.put(revenue.getCategoryName(), revenue.getLineColor());
    // }
    // List<Line> lines = new ArrayList<>();
    // for (String distinctCategory : categoryNameAndColor.keySet()) {
    // List<List<Object>> points = new ArrayList<>();
    // for (RevenueByCategoryOverTime revenue : revenues) {
    // String categoryName = revenue.getCategoryName();
    // if (!categoryName.equals(distinctCategory))
    // continue;
    // Date orderDate = revenue.getOrderDate();
    // BigDecimal amount = revenue.getAmount();
    // List<Object> point = new ArrayList<>();
    // point.add(orderDate);
    // point.add(amount);
    // points.add(point);
    // }
    // Line line = new Line("line", distinctCategory,
    // categoryNameAndColor.get(distinctCategory), points);
    // lines.add(line);
    // }
    // return lines;
    // }

    /**
     * Retrieve an order by its ID.
     *
     * @param orderId the ID of the order
     * @return the order, if found
     */
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepo.findById(orderId);
    }

    /**
     * Retrieve all orders.
     *
     * @return a list of all orders
     */
    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    /**
     * Update an existing order.
     *
     * @param orderId      the ID of the order to update
     * @param updatedOrder the new order details
     * @return the updated order
     */
    public Order updateOrder(Long orderId, Order updatedOrder) {
        return orderRepo.findById(orderId).map(order -> {
            // order.setCustomerName(updatedOrder.getCustomerName());
            order.setTotalAmount(updatedOrder.getTotalAmount());
            order.setPayments(updatedOrder.getPayments());
            return orderRepo.save(order);
        }).orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    }

    /**
     * Delete an order by its ID.
     *
     * @param orderId the ID of the order to delete
     */
    public void deleteOrder(Long orderId) {
        orderRepo.deleteById(orderId);
    }

    /**
     * Add a payment to an order.
     *
     * @param orderId the ID of the order
     * @param payment the payment to add
     * @return the updated order
     */
    public Order addPaymentToOrder(Long orderId, Payment payment) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        payment.setOrder(order); // Associate payment with the order
        paymentRepo.save(payment); // Save the payment

        order.getPayments().add(payment); // Add payment to order's payment list
        return orderRepo.save(order);
    }

    /**
     * Get payments for a specific order.
     *
     * @param orderId the ID of the order
     * @return a list of payments associated with the order
     */
    public List<Payment> getPaymentsForOrder(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        return order.getPayments();
    }
}
