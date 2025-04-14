/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.info6250.restocker.controllers;

import com.info6250.restocker.models.DonationCenter;
import com.info6250.restocker.services.DonationCenterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author tanmay
 */
@Controller
@RequestMapping("/centers")
public class DonationCenterController {
    
    @Autowired
    private DonationCenterService centerService;

    @GetMapping
    public String listCenters(Model model) {
        model.addAttribute("centers", centerService.getAllCenters());
        return "centers/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("center", new DonationCenter());
        return "centers/create";
    }

    @PostMapping
    public String createCenter(@Valid @ModelAttribute("center") DonationCenter center, 
                              BindingResult result) {
        if(result.hasErrors()) {
            return "centers/create";
        }
        centerService.saveCenter(center);
        return "redirect:/centers";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("center", centerService.getCenterById(id));
        return "centers/edit";
    }

    @PostMapping("/update")
    public String updateCenter(@Valid @ModelAttribute("center") DonationCenter center,
                              BindingResult result) {
        if(result.hasErrors()) {
            return "centers/edit";
        }
        centerService.saveCenter(center);
        return "redirect:/centers";
    }

    @GetMapping("/delete/{id}")
    public String deleteCenter(@PathVariable("id") Long id) {
        centerService.deleteCenter(id);
        return "redirect:/centers";
    }
}
