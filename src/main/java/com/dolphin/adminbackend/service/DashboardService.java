package com.dolphin.adminbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dolphin.adminbackend.model.dto.Dashboard;
import com.dolphin.adminbackend.repository.OrderRepo;

@Service
public class DashboardService {

    @Autowired
    private OrderRepo orderRepo;

    public Dashboard getDashboard() {
        Dashboard dashboard = new Dashboard();
        dashboard.setOrdersCount(orderRepo.count());
        return dashboard;
    }

}
