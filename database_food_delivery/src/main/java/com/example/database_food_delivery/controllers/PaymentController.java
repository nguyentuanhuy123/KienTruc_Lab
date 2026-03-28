package com.example.database_food_delivery.controllers;

import com.example.database_food_delivery.entity.Payment;
import com.example.database_food_delivery.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/new")
    public String newPaymentForm(Model model) {
        model.addAttribute("payment", new Payment());
        return "payment_form"; // Thymeleaf template
    }

    @PostMapping("/save")
    public String savePayment(@ModelAttribute Payment payment) {
        paymentService.addPayment(payment);
        return "redirect:/payments/list?orderId=" + payment.getOrderId();
    }

    @GetMapping("/list")
    public String listPayments(Model model, @RequestParam Integer orderId) {
        // Lấy payment theo orderId
        String sql = "SELECT * FROM payments WHERE order_id = ?";
        List<Payment> payments = paymentService.getPaymentsByOrder(orderId);
        model.addAttribute("payments", payments);
        return "payment_list"; // Thymeleaf template
    }
}
