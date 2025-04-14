/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.info6250.restocker.services;

import com.info6250.restocker.dao.NotificationDao;
import com.info6250.restocker.models.Notification;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 *
 * @author tanmay
 */
@Service
@Transactional
public class NotificationService {
    
    @Autowired
    private NotificationDao notificationDao;
    
    @Autowired
    private ProductService productService;

    public List<Notification> getUnacknowledgedNotifications() {
        return notificationDao.getUnacknowledged();
    }

    public void createNotification(Notification notification) {
        notificationDao.create(notification);
    }

    public void acknowledgeNotification(Long id) {
        notificationDao.acknowledge(id);
    }

    @Scheduled(cron = "0 0 8 * * ?") // Daily at 8 AM
//    @Scheduled(fixedRate = 5000)  // Every 5 Sec
    public void generateExpiryNotifications() {
        LocalDate threshold = LocalDate.now().plusDays(7);
        productService.getExpiringProducts(threshold).forEach(product -> {
            Notification notification = new Notification();
            notification.setMessage("Product '" + product.getName() + "' expires on " 
                + product.getExpiryDate());
            notification.setProduct(product);
            notificationDao.create(notification);
        });
    }
}
