package com.dolphin.adminbackend.model.dto.pojo;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class Timeframe {
    private LocalDateTime currentStartDate;
    private String message;
    private LocalDateTime prevStartDate;
    private LocalDateTime prevLastSecond;
}
