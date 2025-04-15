/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.info6250.restocker.dao;

import com.info6250.restocker.models.Notification;
import java.util.List;

/**
 *
 * @author tanmay
 */
public interface NotificationDao {
    void create(Notification notification);
    List<Notification> getUnacknowledged();
    void acknowledge(Long id);
    Notification findUnacknowledgedByProductId(Long productId);
}
