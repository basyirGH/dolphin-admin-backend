package com.dolphin.adminbackend.model.dto.pojo;


import java.util.Date;
import java.util.List;

import com.dolphin.adminbackend.model.dto.request.OrderReq;
import com.dolphin.adminbackend.model.dto.request.SimulatableReq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocketDetail {
    private Date timeOccured;
    
}
