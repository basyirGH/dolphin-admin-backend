package com.dolphin.adminbackend.model.jpa;

import jakarta.persistence.*; 
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Customer extends User {

    private Integer age;
    private String gender;
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @JsonManagedReference // Marks the child side, prevents circular serialization (nested object/array in api response)
    private List<Order> orders;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
    

    
    
}
