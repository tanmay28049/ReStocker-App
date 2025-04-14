/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.info6250.restocker.dao;

import com.info6250.restocker.models.User;


/**
 *
 * @author tanmay
 */
public interface UserDao {
    void save(User user);
    User findByEmail(String email);
    User findById(Long id);
    void update(User user);
    void delete(User user);
}
