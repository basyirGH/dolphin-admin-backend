package com.dolphin.adminbackend.service;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dolphin.adminbackend.enums.Prompt;
import com.dolphin.adminbackend.model.dto.request.Content;
import com.dolphin.adminbackend.model.dto.request.GeminiReq;
import com.dolphin.adminbackend.model.dto.request.Part;
import com.dolphin.adminbackend.model.dto.response.GeminiRes;
import com.dolphin.adminbackend.repository.DynamicQueryRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GenerativeSQLService {

    @Value("${gemini.api.key}")
    private String API_KEY;

    @Autowired
    DynamicQueryRepo dynamicQueryRepo;

    @Autowired
    private RestTemplate restTemplate;

    private final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    public ResponseEntity<GeminiRes> askGeminiAPI(String systemPrompt) {

        Part part = new Part(systemPrompt);
        List<Part> parts = new ArrayList<>();
        parts.add(part);
        Content content = new Content(parts);
        List<Content> contents = new ArrayList<>();
        contents.add(content);
        GeminiReq req = new GeminiReq(contents);

        URI uri = UriComponentsBuilder.fromUriString(BASE_URL)
                .queryParam("key", API_KEY)
                .build()
                .toUri();

        GeminiRes response = restTemplate.postForObject(uri, req, GeminiRes.class);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @Transactional
    public List<Map<String, Object>> runQuery(String query) {
        return dynamicQueryRepo.executeDynamicQuery(query);
    }

    public String cleanSQL(String input) {
        // Regex to match unquoted dates (YYYY-MM-DD) or full datetime (YYYY-MM-DD
        // HH:MM:SS)
        String wrapDateTimeRegex = "(?<!['\\d])(\\d{4}-\\d{2}-\\d{2}(?:\\s\\d{2}:\\d{2}:\\d{2})?)(?!['\\d])";

        // Regex to match standalone YES or NO (case-insensitive) not already inside
        // quotes
        String wrapYesNoRegex = "(?i)(?<!['\\w])(YES|NO)(?!['\\w])";

        return input
                .replaceAll("(?i)sql", "") // Remove occurrences of "sql" (case-insensitive)
                .replaceAll("[`\"]", "") // Remove backticks and double quotes (keep single quotes for valid SQL
                                         // strings)
                .replaceAll("\\s*\\\\n\\s*", "\n") // Normalize line breaks
                .replaceAll(wrapDateTimeRegex, "'$1'") // Wrap unquoted dates/datetimes with single quotes
                .replaceAll(wrapYesNoRegex, "'$1'") // Wrap unquoted YES/NO with single quotes
                .trim(); // Trim leading/trailing spaces
    }

}
