package com.dolphin.adminbackend.model.dto.response;

import com.dolphin.adminbackend.model.dto.request.Content;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Candidate {
    private Content content;
}
