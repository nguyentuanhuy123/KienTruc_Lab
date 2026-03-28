package com.example.database_food_delivery.controllers;

import com.example.database_food_delivery.entity.Shipment;
import com.example.database_food_delivery.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/shipments")
public class ShippingController {

    @Autowired
    private ShippingService shippingService;

    @GetMapping("/new")
    public String newShipmentForm(Model model) {
        model.addAttribute("shipment", new Shipment());
        return "shipment_form"; // Thymeleaf template
    }

    @PostMapping("/save")
    public String saveShipment(@ModelAttribute Shipment shipment) {
        shippingService.addShipment(shipment);
        return "redirect:/shipments/list?orderId=" + shipment.getOrderId();
    }

    @GetMapping("/list")
    public String listShipments(Model model, @RequestParam Integer orderId) {
        List<Shipment> shipments = shippingService.getShipmentsByOrder(orderId);
        model.addAttribute("shipments", shipments);
        return "shipment_list"; // Thymeleaf template
    }
}
