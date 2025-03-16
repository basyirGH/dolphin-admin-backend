package com.dolphin.adminbackend.model.dto.pojo;

import java.util.Date;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimStatusDetail {
    private UUID sessionID;
    private String status;
    private Date date;
}
