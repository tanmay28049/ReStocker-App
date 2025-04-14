/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.info6250.restocker.dao;

import com.info6250.restocker.models.DonationCenter;
import java.util.List;

/**
 *
 * @author tanmay
 */
public interface DonationCenterDao {
    void save(DonationCenter center);
    List<DonationCenter> findAll();
    DonationCenter findById(Long id);
    void delete(DonationCenter center);
    void update(DonationCenter center);
}
