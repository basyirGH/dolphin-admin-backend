package com.dolphin.adminbackend.controllers;

import java.util.HashMap;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/rest/home")
public class HomeController {

    @ResponseBody
    @RequestMapping(value = "",method = RequestMethod.GET)
    public HashMap<String, String> hello(){
        HashMap<String, String> response = new HashMap<String, String>();
        response.put("code", "TOKEN_IS_VALID");
        return response;
    }

}
