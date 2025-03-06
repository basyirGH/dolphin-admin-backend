package com.dolphin.adminbackend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GeminiRes {
    private List<Candidate> candidates;
}
