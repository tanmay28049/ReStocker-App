/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.info6250.restocker.controllers;

import com.info6250.restocker.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author tanmay
 */
@Controller
@RequestMapping("/suggestions")
public class SuggestionController {
    
    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public String addSuggestion(
        @RequestParam("productId") Long productId,
        @RequestParam("centerId") Long centerId
    ) {
        productService.addDonationSuggestion(productId, centerId);
        return "redirect:/products";
    }
}
