package com.info6250.restocker.models;


import com.info6250.restocker.models.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author tanmay
 */
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String message;
    private LocalDate createdDate = LocalDate.now();
    private boolean acknowledged = false;
    private LocalDate acknowledgedDate;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }
    public boolean isAcknowledged() { return acknowledged; }
    public void setAcknowledged(boolean acknowledged) { this.acknowledged = acknowledged; }
    public LocalDate getAcknowledgedDate() { return acknowledgedDate; }
    public void setAcknowledgedDate(LocalDate acknowledgedDate) { this.acknowledgedDate = acknowledgedDate; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}
