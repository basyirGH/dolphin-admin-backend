package com.dolphin.adminbackend.model.dto.queryresult;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// @NoArgsConstructor

@Getter
@Setter
public class TotalOrdersByDemography {
    private int age;
    private String gender;
    private Long count;

    public TotalOrdersByDemography(int age, String gender, Long count){
        this.age = age;
        this.gender = gender;
        this.count = count;
    }
}
