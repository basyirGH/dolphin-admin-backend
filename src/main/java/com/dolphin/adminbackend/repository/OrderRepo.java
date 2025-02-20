package com.dolphin.adminbackend.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dolphin.adminbackend.model.dto.queryresult.RevenueByCategoryOverTime;
import com.dolphin.adminbackend.model.dto.queryresult.TotalOrdersByDemography;
import com.dolphin.adminbackend.model.jpa.Category;
import com.dolphin.adminbackend.model.jpa.Order;

public interface OrderRepo extends JpaRepository<Order, Long> {

        @Query("SELECT count(o.id) from Order o where o.orderDate BETWEEN :startDate AND :endDate")
        Long findCountOfOrdersBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

        @Query("SELECT SUM(o.totalAmount) FROM Order o where o.orderDate BETWEEN :startDate AND :endDate")
        BigDecimal findSumOfTotalAmountBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

        @Query("SELECT AVG(oi2.quantity) " +
                        "FROM Order o JOIN " +
                        "(SELECT oi.order.id as orderId, SUM(oi.quantity) as quantity FROM OrderItem oi GROUP BY oi.order.id) oi2 "
                        +
                        "ON o.id = oi2.orderId " +
                        "WHERE o.orderDate " +
                        "BETWEEN :startDate AND :endDate")
        BigDecimal findAvgQuantityPerOrderBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

        @Query("SELECT CAST(AVG(o.totalAmount) AS BigDecimal) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
        BigDecimal findAvgOfTotalAmountBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

        @Query("SELECT SUM(o.totalAmount) " +
                        "FROM Order o " +
                        "WHERE FUNCTION('DATE', o.orderDate) = CURRENT_DATE " +
                        "AND FUNCTION('TIME_FORMAT', o.orderDate, '%H:%i:%s') = FUNCTION('TIME_FORMAT', :timeOccurred, '%H:%i:%s') ")
        BigDecimal findRevenueCurrentSecond(@Param("timeOccurred") Date timeOccurred);

        @Query("SELECT AVG(o.totalAmount) " +
                        "FROM Order o " +
                        "WHERE FUNCTION('DATE', o.orderDate) = CURRENT_DATE " +
                        "AND FUNCTION('TIME_FORMAT', o.orderDate, '%H:%i') = FUNCTION('TIME_FORMAT', CURRENT_TIME, '%H:%i')")
        BigDecimal findAvgRevenueCurrentMinute();

        @Query("SELECT COUNT(o.id) " +
                        "FROM Order o " +
                        "WHERE FUNCTION('DATE', o.orderDate) = CURRENT_DATE " +
                        "AND FUNCTION('TIME_FORMAT', o.orderDate, '%H:%i:%s') = FUNCTION('TIME_FORMAT', :timeOccurred, '%H:%i:%s') ")
        BigDecimal findOrdersCountCurrentSecond(@Param("timeOccurred") Date timeOccurred);

        @Query("SELECT CAST(COUNT(*) AS double) / 60 " +
                        "FROM Order o " +
                        "WHERE FUNCTION('DATE', o.orderDate) = CURRENT_DATE " +
                        "AND FUNCTION('TIME_FORMAT', o.orderDate, '%H:%i') = FUNCTION('TIME_FORMAT', CURRENT_TIME, '%H:%i')")
        BigDecimal findAvgOrdersCountCurrentMinute();

        @Query("select new com.dolphin.adminbackend.model.dto.queryresult.TotalOrdersByDemography(c.age, c.gender, count(o.id)) "
                        +
                        "from Order o " +
                        "join Customer c ON o.customer.id = c.id " +
                        "where o.orderDate between :startDate and :endDate " +
                        "group by c.age, c.gender ")
        List<TotalOrdersByDemography> findTotalOrdersByDemographyBetween(
                        @Param("startDate") Date startDate,
                        @Param("endDate") Date endDate);

        /*
         * When you apply the FUNCTION('DATE', o.orderDate) (or similar date
         * manipulation functions), it extracts only the date portion (year, month, and
         * day) from the orderDate, effectively discarding the time component.
         * As a result, all o.orderDate values being compared in the WHERE clause are
         * stripped of their time component, and the ORDER BY o.orderDate ASC may not
         * sort correctly because it is acting on the unmodified orderDate (which still
         * includes the time).
         */
        @Query("SELECT new com.dolphin.adminbackend.model.dto.queryresult.RevenueByCategoryOverTime(p.category.id, c.name, o.orderDate, "
                        +
                        "(oi.pricePerUnit * oi.quantity) as amount, c.lineColor) " +
                        "FROM Order o " +
                        "JOIN OrderItem oi ON oi.order.id = o.id " +
                        "JOIN Product p ON p.id = oi.product.id " +
                        "JOIN Category c ON c.id = p.category.id " +
                        "WHERE FUNCTION('DATE', o.orderDate) = CURRENT_DATE " +
                        "AND FUNCTION('TIME_FORMAT', o.orderDate, '%H:%i:%s') = FUNCTION('TIME_FORMAT', CURRENT_TIME, '%H:%i:%s') ")
        List<RevenueByCategoryOverTime> findRevenueByCategoryPerSecond();

        @Query("SELECT DISTINCT new Category(p.category.id, c.name, c.lineColor) " +
                        "from Order o " +
                        "join OrderItem oi on oi.order.id = o.id " +
                        "join Product p on oi.product.id = p.id " +
                        "join Category c on p.category.id = c.id")
        List<Category> findCategoriesInOrderItems();

        @Query(value = "select count(id) " +
                        "from order_ o " +
                        "where date(order_date) = current_date() " +
                        "and time_format(order_date, '%H:%i') >= '00:00' " +
                        "and time_format(order_date, '%H:%i') <= time_format(current_time(), '%H:%i')", nativeQuery = true)
        Long findTodaysPartialOrderCount();

        @Query(value = "select count(id) " +
                        "from order_ o " +
                        "where date(order_date) = date_sub(current_date(), interval '1' day) " + // used to subtract a
                                                                                                 // specified time
                                                                                                 // interval from a
                                                                                                 // date.
                        "and time_format(order_date, '%H:%i') >= '00:00' " +
                        "and time_format(order_date, '%H:%i') <= time_format(current_time(), '%H:%i')", nativeQuery = true)
        Long findYesterdaysPartialOrderCount();

}
