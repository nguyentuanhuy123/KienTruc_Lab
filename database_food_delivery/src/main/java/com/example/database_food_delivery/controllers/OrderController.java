package com.example.database_food_delivery.controllers;

import com.example.database_food_delivery.entity.Order;
import com.example.database_food_delivery.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/new")
    public String newOrderForm(Model model) {
        model.addAttribute("order", new Order());
        return "order_form"; // Thymeleaf template
    }

    @PostMapping("/save")
    public String saveOrder(@ModelAttribute Order order) {
        orderService.createOrder(order);
        return "redirect:/orders/list";
    }

    @GetMapping("/list")
    public String listOrders(Model model, @RequestParam(defaultValue = "1") Integer userId) {
        model.addAttribute("orders", orderService.getOrdersByUser(userId));
        return "order_list"; // Thymeleaf template
    }
}