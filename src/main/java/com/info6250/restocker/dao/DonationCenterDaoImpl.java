/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.info6250.restocker.dao;

import com.info6250.restocker.models.DonationCenter;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
/**
 *
 * @author tanmay
 */
@Repository
public class DonationCenterDaoImpl implements DonationCenterDao {
    
    private final SessionFactory sessionFactory;
    
    public DonationCenterDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void save(DonationCenter center) {
        Session session = sessionFactory.getCurrentSession();
        
        session.persist(center);
        
    }

    @Override
    public List<DonationCenter> findAll() {
        Session session = sessionFactory.getCurrentSession();
        
        List<DonationCenter> centers = session.createQuery("FROM DonationCenter", DonationCenter.class).list();
        
        return centers;
    }

    @Override
    public DonationCenter findById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        
        DonationCenter center = session.get(DonationCenter.class, id);
        
        return center;
    }

    @Override
    public void delete(DonationCenter center) {
        Session session = sessionFactory.getCurrentSession();
        
        session.remove(center);
        
    }

    @Override
    public void update(DonationCenter center) {
        Session session = sessionFactory.getCurrentSession();
        
        session.merge(center);
        
    }
}
