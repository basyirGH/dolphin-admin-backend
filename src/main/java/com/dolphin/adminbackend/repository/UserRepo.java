package com.dolphin.adminbackend.repository;

import org.springframework.stereotype.Repository;

import com.dolphin.adminbackend.model.jpa.User;

@Repository
public class UserRepo {
    public User findUserByEmail(String email){
        User user = new User(email,"1");
        user.setFullName("Full Name");
        return user;
    }
}
