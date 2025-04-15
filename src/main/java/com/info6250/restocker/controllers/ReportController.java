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
                        productInfo.put("barcode", product.getBarcode());
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
    
    @GetMapping("/donations/export")
    public void exportDonationsReport(HttpServletResponse response) throws Exception {
        // Set content type and header so the browser downloads a CSV file.
        response.setContentType("text/csv");
        String headerValue = "attachment; filename=donations_report.csv";
        response.setHeader("Content-Disposition", headerValue);

        // Retrieve donation suggestions from your service.
        Map<DonationCenter, List<Product>> suggestions = productService.getDonationSuggestions();
        LocalDate today = LocalDate.now();
        // Date formatter for the expiry date field.
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        try (PrintWriter writer = response.getWriter()) {
            // Write CSV header row.
            writer.println("\"Donation Center\",\"Address\",\"Contact Email\",\"Product Name\",\"Barcode\",\"Expiry Date\",\"Days Remaining\"");
            
            // Iterate over each donation center and its suggested products.
            for (Map.Entry<DonationCenter, List<Product>> entry : suggestions.entrySet()) {
                DonationCenter center = entry.getKey();
                String centerName = center.getName();
                String centerAddress = center.getAddress();
                String contactEmail = center.getContactEmail() != null ? center.getContactEmail() : "";

                // For each product associated with this donation center.
                for (Product product : entry.getValue()) {
                    String productName = product.getName();
                    String barcode = product.getBarcode();
                    String expiryDateStr = product.getExpiryDate() != null ? product.getExpiryDate().format(dateFormatter) : "";
                    String daysRemaining = "";
                    if (product.getExpiryDate() != null) {
                        daysRemaining = Long.toString(ChronoUnit.DAYS.between(today, product.getExpiryDate()));
                    }
                    // Build a CSV row with each value in double quotes.
                    String csvRow = String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                        centerName, centerAddress, contactEmail,
                        productName, barcode, expiryDateStr, daysRemaining);
                    writer.println(csvRow);
                }
            }
        }
    }

    @GetMapping("/waste")
    public String wasteReport(Model model) {
        // Define reporting period: from one month ago (excluding today) until yesterday.
        LocalDate endDate = LocalDate.now().minusDays(1);
        LocalDate startDate = endDate.minusMonths(1);
        
        // Retrieve waste trends map: key = date, value = list of expired products on that day.
        Map<LocalDate, List<Product>> trends = productService.getWasteTrends();
        
        // Sort dates and build chart data.
        List<LocalDate> sortedDates = trends.keySet().stream().sorted().collect(Collectors.toList());
        List<String> labels = sortedDates.stream()
                .map(date -> date.format(DateTimeFormatter.ofPattern("MMM dd")))
                .collect(Collectors.toList());
        List<Integer> counts = sortedDates.stream()
                .map(date -> trends.get(date).size())
                .collect(Collectors.toList());

        // Compute summary KPIs.
        int totalWaste = counts.stream().mapToInt(Integer::intValue).sum();
        double averageWaste = counts.isEmpty() ? 0 : (double) totalWaste / counts.size();
        int maxWasteCount = counts.isEmpty() ? 0 : counts.stream().mapToInt(Integer::intValue).max().getAsInt();
        LocalDate maxWasteDate = sortedDates.stream()
                .filter(date -> trends.get(date).size() == maxWasteCount)
                .findFirst().orElse(null);

        // Prepare detailed daily waste records.
        List<Map<String, Object>> dailyWasteDetails = sortedDates.stream().map(date -> {
            Map<String, Object> record = new HashMap<>();
            record.put("date", date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            record.put("count", trends.get(date).size());
            String productNames = trends.get(date).stream()
                    .map(Product::getName)
                    .collect(Collectors.joining(", "));
            record.put("products", productNames);
            return record;
        }).collect(Collectors.toList());

        // Add values to model.
        model.addAttribute("labels", labels);
        model.addAttribute("data", counts);
        model.addAttribute("totalWaste", totalWaste);
        model.addAttribute("averageWaste", String.format("%.2f", averageWaste));
        model.addAttribute("maxWasteCount", maxWasteCount);
        model.addAttribute("maxWasteDate", maxWasteDate != null ?
                maxWasteDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) : "N/A");
        model.addAttribute("dailyWasteDetails", dailyWasteDetails);
        
        return "reports/waste";
    }

    @GetMapping("/waste/export")
    public void exportWasteReport(HttpServletResponse response) throws Exception {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=waste_report.csv");
        
        // Define the reporting period: from one month ago (excluding today) until yesterday.
        LocalDate endDate = LocalDate.now().minusDays(1);
        LocalDate startDate = endDate.minusMonths(1);
        
        // Retrieve daily waste trends.
        Map<LocalDate, List<Product>> trends = productService.getWasteTrends();
        List<LocalDate> sortedDates = trends.keySet().stream().sorted().collect(Collectors.toList());
        
        // Compute summary KPIs.
        List<Integer> counts = sortedDates.stream()
                .map(date -> trends.get(date).size())
                .collect(Collectors.toList());
        int totalWaste = counts.stream().mapToInt(Integer::intValue).sum();
        double averageWaste = counts.isEmpty() ? 0 : (double) totalWaste / counts.size();
        int maxWasteCount = counts.isEmpty() ? 0 : counts.stream().mapToInt(Integer::intValue).max().getAsInt();
        LocalDate maxWasteDate = sortedDates.stream()
                .filter(date -> trends.get(date).size() == maxWasteCount)
                .findFirst().orElse(null);
        
        DateTimeFormatter dateFormatterFull = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        
        try (PrintWriter writer = response.getWriter()) {
            // SECTION 1: Report Metadata Header
            writer.println("Waste Report");
            writer.println("Report Generated:," + escapeCsv(LocalDate.now().toString()));
            writer.println("Reporting Period:," 
                    + escapeCsv(startDate.format(dateFormatterFull) + " to " + endDate.format(dateFormatterFull)));
            writer.println(); // blank line
            
            // SECTION 2: Summary Metrics
            writer.println("Metric,Value");
            writer.println("Total Waste:," + totalWaste);
            writer.println("Average Daily Waste:," + String.format("%.2f", averageWaste));
            writer.println("Max Waste Day:," + (maxWasteDate != null ? escapeCsv(maxWasteDate.format(dateFormatterFull)) : escapeCsv("N/A")));
            writer.println("Max Waste Count:," + maxWasteCount);
            writer.println(); // blank line
            
            // SECTION 3: Detailed Daily Data
            writer.println("Date,Waste Count,Product Names");
            for (LocalDate date : sortedDates) {
                String dateStr = date.format(dateFormatterFull);
                int count = trends.get(date).size();
                // Use a semicolon separator for product names.
                String productNames = trends.get(date).stream()
                        .map(Product::getName)
                        .collect(Collectors.joining("; "));
                String csvRow = String.format("%s,%d,%s",
                        escapeCsv(dateStr), count, escapeCsv(productNames));
                writer.println(csvRow);
            }
        }
    }

    /**
     * Helper method that always returns a CSV-escaped field.
     * This method wraps the field in double quotes and escapes any existing double quotes.
     */
    private String escapeCsv(String field) {
        if (field == null) {
            return "\"\"";
        }
        // Replace any " with double "".
        String escaped = field.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}
