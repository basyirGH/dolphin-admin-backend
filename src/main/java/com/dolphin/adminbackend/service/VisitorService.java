package com.dolphin.adminbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dolphin.adminbackend.model.jpa.Visitor;
import com.dolphin.adminbackend.repository.VisitorRepo;
import com.dolphin.adminbackend.utility.IPHasher;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class VisitorService {
    
    @Autowired
    private VisitorRepo visitorRepository;

    @Autowired
    private IPHasher ipHasher;

    public Visitor trackVisitor(String ip) {
        String hashedIp = ipHasher.hashIp(ip);
        Optional<Visitor> existingVisitor = visitorRepository.findByIp(hashedIp);
        
        if (existingVisitor.isPresent()) {
            System.out.println("Returning visitor: " + ip);
            Visitor visitor = existingVisitor.get();
            if (LocalDateTime.now().isBefore(visitor.getCooldownEndsAt())) {
                visitor.setIsCooldownActive(Boolean.TRUE);
            } else {
                LocalDateTime cooldownEndsAt = LocalDateTime.now().plusHours(1);
                visitor.setCooldownEndsAt(cooldownEndsAt);
                visitor.setIsCooldownActive(Boolean.FALSE);
            }
            return visitorRepository.save(visitor);
        } else {
            // New visitor, save to DB
            Visitor newVisitor = new Visitor();
            LocalDateTime cooldownEndsAt = LocalDateTime.now().plusHours(6);
            newVisitor.setIp(hashedIp);
            newVisitor.setCooldownEndsAt(cooldownEndsAt);
            newVisitor.setIsCooldownActive(Boolean.FALSE);
            System.out.println("New visitor recorded: " + ip);
            return visitorRepository.save(newVisitor);
        }
    }
}

