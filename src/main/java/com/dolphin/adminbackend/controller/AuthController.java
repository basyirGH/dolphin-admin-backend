package com.dolphin.adminbackend.controller;

import com.dolphin.adminbackend.auth.JwtUtil;
import com.dolphin.adminbackend.model.jpa.User;
import com.dolphin.adminbackend.model.dto.request.LoginReq;
import com.dolphin.adminbackend.model.dto.response.ErrorRes;
import com.dolphin.adminbackend.model.dto.response.LoginRes;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;

    }

    @ResponseBody
    @RequestMapping(value = "/guest", method = RequestMethod.GET)
    public ResponseEntity requestGuestToken() {

        try {
            User user = new User("guest", "guest");
            String token = jwtUtil.createToken(user);
            LoginRes loginRes = new LoginRes("guest", token);
            return ResponseEntity.ok(loginRes);
        } catch (Exception e) {
            ErrorRes errorResponse = new ErrorRes(HttpStatus.BAD_REQUEST, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
