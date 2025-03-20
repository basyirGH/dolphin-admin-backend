package com.dolphin.adminbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import com.dolphin.adminbackend.model.jpa.Visitor;
import com.dolphin.adminbackend.repository.VisitorRepo;
import com.dolphin.adminbackend.utility.IPHasher;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
public class VisitorService {

    @Autowired
    private VisitorRepo visitorRepository;

    @Autowired
    private IPHasher ipHasher;

    private final int MAX_SIM_COUNT = 5;

    public Visitor trackVisitor(String ip, Date date) {
        String hashedIp = ipHasher.hashIp(ip);
        Optional<Visitor> optionalVisitor = visitorRepository.findByIp(hashedIp);
        LocalDateTime current = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    
        if (optionalVisitor.isPresent()) {
            Visitor visitor = optionalVisitor.get();
            LocalDateTime cooldownEndsAt = visitor.getCooldownEndsAt();
            Integer simCount = visitor.getSimCount();
    
            // If cooldown is active and has expired, reset count and deactivate cooldown
            if (cooldownEndsAt != null && current.isAfter(cooldownEndsAt)) {
                visitor.setSimCount(1);
                visitor.setCooldownEndsAt(null);
                visitor.setIsCooldownActive(false);
            } 
            else if (simCount < MAX_SIM_COUNT) {
                visitor.setSimCount(simCount + 1);
            } 
            else {
                visitor.setCooldownEndsAt(current.plusHours(1));
                visitor.setIsCooldownActive(true);
            }
    
            return visitorRepository.save(visitor);
        }
    
        // New visitor registration
        Visitor newVisitor = new Visitor();
        newVisitor.setIp(hashedIp);
        newVisitor.setSimCount(1);
        newVisitor.setIsCooldownActive(false);
    
        return visitorRepository.save(newVisitor);
    }
    
}
