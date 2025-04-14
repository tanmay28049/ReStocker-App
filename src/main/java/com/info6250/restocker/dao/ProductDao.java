/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.info6250.restocker.dao;

import com.info6250.restocker.models.DonationCenter;
import com.info6250.restocker.models.Product;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 *
 * @author tanmay
 */
public interface ProductDao {
    void save(Product product);
    List<Product> findAll();
    Product findById(Long id);
    void delete(Product product);
    void update(Product product);
    List<Product> findExpiringProducts(LocalDate threshold);
    void addDonationSuggestion(Long productId, Long centerId);
    List<Product> findExpiringProductsBetweenDates(LocalDate start, LocalDate end);
    Map<LocalDate, List<Product>> findExpiredProductsByDate(LocalDate start, LocalDate end);
    Map<DonationCenter, List<Product>> findProductsNeedingDonation();
    Product findByBarcode(String barcode);
    List<Product> findAllSortedByExpiry();
}
