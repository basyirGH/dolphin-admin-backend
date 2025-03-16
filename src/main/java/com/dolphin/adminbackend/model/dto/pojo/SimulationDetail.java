package com.dolphin.adminbackend.model.dto.pojo;

import java.util.List;
import java.util.UUID;

import com.dolphin.adminbackend.model.dto.request.OrderReq;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulationDetail {
    private UUID simId;
    private List<OrderReq> simulationDataList;
}
