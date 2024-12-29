package com.dolphin.adminbackend.repositories;

import org.springframework.stereotype.Repository;

import com.dolphin.adminbackend.models.User;

@Repository
public class UserRepository {
    public User findUserByEmail(String email){
        User user = new User(email,"123456");
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        return user;
    }
}
