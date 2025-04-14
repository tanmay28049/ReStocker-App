/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.info6250.restocker.services;

import com.info6250.restocker.dao.DonationCenterDao;
import com.info6250.restocker.models.DonationCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 *
 * @author tanmay
 */
@Service
@Transactional
public class DonationCenterService {
    
    @Autowired
    private DonationCenterDao centerDao;

    public List<DonationCenter> getAllCenters() {
        return centerDao.findAll();
    }
    
    public void saveCenter(DonationCenter center) {
        if (center.getId() == null) {
            centerDao.save(center);
        } else {
            centerDao.update(center);
        }
    }
    
    public DonationCenter getCenterById(Long id) {
        return centerDao.findById(id);
    }
    
    public void deleteCenter(Long id) {
        DonationCenter center = centerDao.findById(id);
        centerDao.delete(center);
    }
}
