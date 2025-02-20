package com.dolphin.adminbackend.model.dto.supplier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LineDataPoint {

    private Date date;
    private BigDecimal value;
    private List<LineDataPoint> point;

    public LineDataPoint(List<LineDataPoint> point) {
        this.point = point;
    }
}
