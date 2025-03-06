package com.dolphin.adminbackend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.dolphin.adminbackend.enums.Prompt;
import com.dolphin.adminbackend.model.dto.response.GeminiRes;
import com.dolphin.adminbackend.service.GenerativeSQLService;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class GeminiAPITest {

    @Autowired
    GenerativeSQLService genSQLService;
    
    @Test
    public void test(){
        String userQuery = "Which demography that bought the most 'wearable' product category last month?";
        String systemPrompt = Prompt.DIVER_INSTRUCT_PROMPT.format(userQuery);
        ResponseEntity<GeminiRes> respEntity = genSQLService.askGeminiAPI(systemPrompt);
        assertEquals(respEntity.getStatusCode().value(), HttpStatus.ACCEPTED.value());
        GeminiRes resp = respEntity.getBody();
        String query = resp.getCandidates().get(0).getContent().getParts().get(0).getText();
        assertNotNull(query);
        log.info(query);
    }
}
