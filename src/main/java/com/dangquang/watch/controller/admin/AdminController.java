package com.dangquang.watch.controller.admin;

import com.dangquang.watch.service.CameraService;
import com.dangquang.watch.service.CategoryService;
import com.dangquang.watch.service.UserService;
import com.dangquang.watch.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CameraService cameraService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String dashboard(Model model) {
        long totalCameras = cameraService.findAll().size();
        long totalCategories = categoryService.findAll().size();
        long totalUsers = userService.findAll().size();
        long totalOrders = orderService.findAll().size();
        BigDecimal totalRevenue = orderService.getTotalRevenue();
        long pendingOrders = orderService.countPendingOrders();
        
        model.addAttribute("totalCameras", totalCameras);
        model.addAttribute("totalCategories", totalCategories);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("pendingOrders", pendingOrders);
        
        // Thống kê đơn hàng theo category camera
        var categories = categoryService.findAll();
        var ordersByCategory = orderService.countOrdersByCameraCategory(categories);
        model.addAttribute("ordersByCategory", ordersByCategory);
        model.addAttribute("categoryLabels", categories.stream().map(c -> c.getName()).toList());
        
        // Fallback an toàn cho Thymeleaf
        var categoryLabels = categories.stream().map(c -> c.getName()).toList();
        if (categoryLabels == null) categoryLabels = java.util.Collections.emptyList();
        if (ordersByCategory == null) ordersByCategory = new java.util.LinkedHashMap<>();
        model.addAttribute("categoryLabels", categoryLabels);
        model.addAttribute("ordersByCategory", ordersByCategory);
        
        var revenueByCategory = orderService.revenueByCameraCategoryAsDouble(categories);
        if (revenueByCategory == null) revenueByCategory = new java.util.LinkedHashMap<>();
        model.addAttribute("revenueByCategory", revenueByCategory);
        
        return "admin/dashboard";
    }
}
