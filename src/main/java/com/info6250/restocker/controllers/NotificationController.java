/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.info6250.restocker.controllers;

import com.info6250.restocker.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author tanmay
 */
@Controller
@RequestMapping("/notifications")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public String listNotifications(Model model) {
        model.addAttribute("notifications", notificationService.getUnacknowledgedNotifications());
        return "notifications/list";
    }

    @PostMapping("/acknowledge/{id}")
    public String acknowledgeNotification(@PathVariable Long id) {
        notificationService.acknowledgeNotification(id);
        return "redirect:/notifications";
    }
}
