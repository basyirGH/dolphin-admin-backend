package com.dolphin.adminbackend.model.dto.request;

import java.util.List;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
public class GeminiReq {
    private List<Content> contents;
}

