package com.info6250.restocker.controllers;

import com.info6250.restocker.models.DonationCenter;
import com.info6250.restocker.models.Product;
import com.info6250.restocker.services.ProductService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author tanmay
 */
@Controller
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ProductService productService;
    
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    @GetMapping("/expiry")
    public String expiryReport(Model model) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusWeeks(2);
        model.addAttribute("expiryData", productService.getExpiringProductsByDate(startDate, endDate));
        model.addAttribute("today", LocalDate.now());
        return "reports/expiry";
    }
    
    @GetMapping("/expiry/export")
    public void exportExpiryReport(HttpServletResponse response) throws Exception {
        // Set the content type and attachment header.
        response.setContentType("text/csv");
        String headerValue = "attachment; filename=expiry_report.csv";
        response.setHeader("Content-Disposition", headerValue);

        // Define the date range for the report.
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusWeeks(2);
        List<Product> expiryData = productService.getEnhancedExpiringProducts(startDate, endDate);

        // Create a formatter for the expiry date.
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        try (PrintWriter writer = response.getWriter()) {
            // Write header row with fields in quotes.
            writer.println("\"Product Name\",\"Barcode\",\"Expiry Date\",\"Days Remaining\",\"Discount Percentage\"");
            
            // Write data rows.
            for (Product product : expiryData) {
                String name = product.getName();
                String barcode = product.getBarcode();
                String expiry = product.getExpiryDate() != null ? product.getExpiryDate().format(dateFormatter) : "";
                String daysRemaining = product.getDaysUntilExpiry() != null ? product.getDaysUntilExpiry().toString() : "";
                // Display discount as string; for numeric discounts, you might optionally prefix with "%" if desired.
                String discount = product.getDiscountPercentage() != null ? product.getDiscountPercentage().toString() : "N/A";
                
                // Create a CSV row with each value wrapped in double quotes.
                String csvRow = String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                        name, barcode, expiry, daysRemaining, discount);
                writer.println(csvRow);
            }
        }
    }

    @GetMapping("/donations")
    public String donationReport(Model model) {
        Map<DonationCenter, List<Product>> suggestions = productService.getDonationSuggestions();

        // Calculate days remaining for each product
        Map<DonationCenter, List<Map<String, Object>>> enhancedSuggestions = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();

        suggestions.forEach((center, products) -> {
            List<Map<String, Object>> enhancedProducts = products.stream()
                    .map(product -> {
                        Map<String, Object> productInfo = new HashMap<>();
                        productInfo.put("name", product.getName());
                        productInfo.put("expiryDate", product.getExpiryDate());
                        productInfo.put("daysRemaining", ChronoUnit.DAYS.between(today, product.getExpiryDate()));
                        return productInfo;
                    })
                    .collect(Collectors.toList());
            enhancedSuggestions.put(center, enhancedProducts);
        });

        model.addAttribute("donationSuggestions", enhancedSuggestions);
        return "reports/donations";
    }

    @GetMapping("/waste")
    public String wasteReport(Model model) {
        LocalDate endDate = LocalDate.now().minusDays(1);
        LocalDate startDate = endDate.minusMonths(1);

        Map<LocalDate, List<Product>> trends = productService.getWasteTrends();
        System.out.println("Waste trends data: " + trends); // Debug logging

        List<String> formattedDates = Collections.emptyList();
        List<Integer> counts = Collections.emptyList();

        if (trends != null && !trends.isEmpty()) {
            formattedDates = trends.keySet().stream()
                    .sorted()
                    .map(date -> date.format(DateTimeFormatter.ofPattern("MMM dd")))
                    .collect(Collectors.toList());

            counts = trends.values().stream()
                    .map(List::size)
                    .collect(Collectors.toList());
        }

        System.out.println("Formatted dates: " + formattedDates); // Debug
        System.out.println("Counts: " + counts); // Debug

        model.addAttribute("labels", formattedDates);
        model.addAttribute("data", counts);
        return "reports/waste";
    }
}
