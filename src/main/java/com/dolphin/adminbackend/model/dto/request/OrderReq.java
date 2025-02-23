package com.dolphin.adminbackend.model.dto.request;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderReq implements SimulatableReq{

    private Long customerId;
    private List<OrderItemReq> items;
    private Date orderDate;
    private final String SIM_ID = "NEW_ORDER_SIM";

    @Override
    public String getSimID() {
        return SIM_ID;
    }

}
