/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.info6250.restocker.controllers;

import com.info6250.restocker.dao.ProductDao;
import com.info6250.restocker.models.Product;
import com.info6250.restocker.services.BarcodeService;
import com.info6250.restocker.services.DonationCenterService;
import com.info6250.restocker.services.ProductService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author tanmay
 */
@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ProductService productService;

    @Autowired
    private DonationCenterService donationCenterService;

    @Autowired
    private BarcodeService barcodeService;

    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProductsWithExpiryInfo());
        model.addAttribute("expiringProducts", productService.getExpiringProducts(LocalDate.now().plusDays(7)));
        model.addAttribute("allCenters", donationCenterService.getAllCenters());
        model.addAttribute("today", LocalDate.now());
        return "products/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        // Check for scanned barcode in flash attributes
        String scannedBarcode = (String) model.asMap().get("scannedBarcode");
        Product product = new Product();

        if (scannedBarcode != null) {
            product.setBarcode(scannedBarcode);
        }

        model.addAttribute("product", product);
        return "products/create";
    }

    @PostMapping
    public String createProduct(@Valid @ModelAttribute Product product,
            BindingResult result) {
        if (result.hasErrors()) {
            return "products/create";
        }
        productDao.save(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Product product = productDao.findById(id);
        model.addAttribute("product", product);
        return "products/edit";
    }

    @PostMapping("/update")
    public String updateProduct(@Valid @ModelAttribute Product product,
            BindingResult result) {
        if (result.hasErrors()) {
            return "products/edit";
        }
        productDao.update(product);
        return "redirect:/products";
    }

    @GetMapping("/delete")
    public String deleteProduct(@RequestParam("id") Long id) {
        Product product = productDao.findById(id);
        productDao.delete(product);
        return "redirect:/products";
    }

    @GetMapping("/scan")
    public String showScanner(Model model) {
        return "products/scan";
    }

    @GetMapping("/byBarcode/{barcode}")
    public ResponseEntity<?> getProductByBarcode(@PathVariable String barcode) {
        Product product = productDao.findByBarcode(barcode);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    @PostMapping("/scan")
    public String handleBarcodeScan(
            @RequestParam String barcode,
            RedirectAttributes redirectAttributes
    ) {
        Product existing = productDao.findByBarcode(barcode);
        if (existing != null) {
            redirectAttributes.addFlashAttribute("error", "Product with this barcode already exists");
            return "redirect:/products/scan";
        }

        // Add to both flash and request scope
        redirectAttributes.addFlashAttribute("scannedBarcode", barcode);
        redirectAttributes.addAttribute("scannedBarcode", barcode);

        return "redirect:/products/new";
    }

}
