package com.info6250.restocker.dao;

import com.info6250.restocker.dao.UserDao;
import com.info6250.restocker.models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author tanmay
 */

@Repository
public class UserDaoImpl implements UserDao {

    private final SessionFactory sessionFactory;

    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void save(User user) {
        Session session = sessionFactory.getCurrentSession();
        try {
            
            session.persist(user);
            
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            throw e;
        }
    }

    @Override
    public User findByEmail(String email) {
        Session session = sessionFactory.getCurrentSession();
        
        User user = session.createQuery(
                "FROM User WHERE email = :email", User.class)
                .setParameter("email", email)
                .uniqueResult();
        
        return user;
    }

    // Additional methods if needed
    @Override
    public User findById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        
        User user = session.get(User.class, id);
        
        return user;
    }

    @Override
    public void update(User user) {
        Session session = sessionFactory.getCurrentSession();
        
        session.merge(user);
        
    }

    @Override
    public void delete(User user) {
        Session session = sessionFactory.getCurrentSession();
        
        session.remove(user);
        
    }
}
