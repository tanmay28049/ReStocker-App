/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.info6250.restocker.dao;

import com.info6250.restocker.models.Notification;
import java.time.LocalDate;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

/**
 *
 * @author tanmay
 */
@Repository
public class NotificationDaoImpl implements NotificationDao {

    private final SessionFactory sessionFactory;

    public NotificationDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void create(Notification notification) {
        Session session = sessionFactory.getCurrentSession();
        
        session.persist(notification);
        
    }

    @Override
    public List<Notification> getUnacknowledged() {
        Session session = sessionFactory.getCurrentSession();
        
        List<Notification> notifications = session.createQuery(
                "FROM Notification n WHERE n.acknowledged = false ORDER BY n.createdDate DESC",
                Notification.class
        ).list();
        
        return notifications;
    }

    @Override
    public void acknowledge(Long id) {
        Session session = sessionFactory.getCurrentSession();
        
        Notification notification = session.get(Notification.class, id);
        if (notification != null) {
            notification.setAcknowledged(true);
            notification.setAcknowledgedDate(LocalDate.now());
            session.merge(notification);
        }
        
    }
}
