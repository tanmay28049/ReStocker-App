/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.info6250.restocker.services;

import com.info6250.restocker.dao.ProductDao;
import com.info6250.restocker.models.DonationCenter;
import com.info6250.restocker.models.Product;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author tanmay
 */
@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductDao productDao;

    public List<Product> getExpiringProducts(LocalDate threshold) {
        return productDao.findExpiringProducts(threshold);
    }

    public List<Product> getExpiringProductsByDate(LocalDate start, LocalDate end) {
        return productDao.findExpiringProductsBetweenDates(start, end);
    }

    public Map<LocalDate, List<Product>> getWasteTrends() {
        LocalDate endDate = LocalDate.now().minusDays(1);
        LocalDate startDate = endDate.minusMonths(1);
        System.out.println("Fetching waste trends between " + startDate + " and " + endDate);

        Map<LocalDate, List<Product>> trends = productDao.findExpiredProductsByDate(startDate, endDate);
        return trends != null ? trends : Collections.emptyMap();
    }

    public Map<DonationCenter, List<Product>> getDonationSuggestions() {
        return productDao.findProductsNeedingDonation();
    }

    public void addDonationSuggestion(Long productId, Long centerId) {
        productDao.addDonationSuggestion(productId, centerId);
    }

    public void calculateDiscount(Product product, LocalDate today) {
        long daysUntilExpiry = ChronoUnit.DAYS.between(today, product.getExpiryDate());

        if (daysUntilExpiry <= 0) {
            product.setDiscountPercentage(70);
        } else if (daysUntilExpiry <= 2) {
            product.setDiscountPercentage(50);
        } else if (daysUntilExpiry <= 5) {
            product.setDiscountPercentage(30);
        } else if (daysUntilExpiry <= 7) {
            product.setDiscountPercentage(15);
        } else {
            product.setDiscountPercentage(null);
        }
    }
    
    public List<Product> getAllProductsWithExpiryInfo() {
        List<Product> products = productDao.findAll();
        LocalDate today = LocalDate.now();

        products.forEach(product -> {
            if(product.getExpiryDate() != null) {
                long days = ChronoUnit.DAYS.between(today, product.getExpiryDate());
                product.setDaysUntilExpiry(days);
                calculateDiscount(product, today);
            }
        });

        return products;
    }

    public List<Product> getEnhancedExpiringProducts(LocalDate start, LocalDate end) {
        List<Product> products = productDao.findExpiringProductsBetweenDates(start, end);
        LocalDate today = LocalDate.now();
        products.forEach(product -> {
            if(product.getExpiryDate() != null) {
                long days = ChronoUnit.DAYS.between(today, product.getExpiryDate());
                product.setDaysUntilExpiry(days);
                calculateDiscount(product, today);  // This method sets discountPercentage based on the expiry date.
            }
        });
        return products;
    }
}
