package com.dangquang.watch.controller.admin;

import com.dangquang.watch.dto.OrderStats;
import com.dangquang.watch.entity.Order;
import com.dangquang.watch.repository.OrderRepository;
import com.dangquang.watch.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping
    @Transactional(readOnly = true)
    public String listOrders(Model model) {
        try {
            System.out.println("=== AdminOrderController.listOrders() started ===");
            
            // Get orders with minimal complexity
            List<Order> orders = null;
            try {
                orders = orderService.findAll();
                System.out.println("Found orders: " + (orders != null ? orders.size() : 0));
            } catch (Exception e) {
                System.out.println("Error getting orders: " + e.getMessage());
                orders = new java.util.ArrayList<>();
            }
            
            // Ensure orders is never null
            if (orders == null) {
                orders = new java.util.ArrayList<>();
            }
            
            // Get stats with error handling
            OrderStats orderStats = null;
            try {
                orderStats = orderService.getOrderStats();
                System.out.println("Got order stats successfully");
            } catch (Exception e) {
                System.out.println("Error getting stats, using defaults: " + e.getMessage());
                orderStats = new OrderStats(0, 0, 0, 0, 0, 0);
            }
            
            // Ensure orderStats is never null
            if (orderStats == null) {
                orderStats = new OrderStats(0, 0, 0, 0, 0, 0);
            }

            // Add safe attributes
            model.addAttribute("orders", orders);
            model.addAttribute("orderStats", orderStats);
            model.addAttribute("totalOrders", orders.size());
            model.addAttribute("totalPages", 1);
            model.addAttribute("currentPage", 0);

            System.out.println("=== AdminOrderController.listOrders() completed successfully ===");
            return "admin/order/list";
            
        } catch (Exception e) {
            System.out.println("=== CRITICAL ERROR in listOrders: " + e.getMessage() + " ===");
            e.printStackTrace();
            
            // Return absolutely minimal safe data
            model.addAttribute("orders", new java.util.ArrayList<>());
            model.addAttribute("orderStats", new OrderStats(0, 0, 0, 0, 0, 0));
            model.addAttribute("totalOrders", 0);
            model.addAttribute("totalPages", 1);
            model.addAttribute("currentPage", 0);
            model.addAttribute("errorMessage", "Có lỗi hệ thống: " + e.getMessage());
            
            return "admin/order/list";
        }
    }

    @GetMapping("/detail/{id}")
    @Transactional(readOnly = true)
    public String orderDetail(@PathVariable Long id, Model model) {
        try {
            System.out.println("=== AdminOrderController.orderDetail() started for ID: " + id + " ===");
            
            // Use simple findById instead of complex join
            Optional<Order> orderOpt = orderRepository.findById(id);
            if (!orderOpt.isPresent()) {
                model.addAttribute("errorMessage", "Không tìm thấy đơn hàng với ID: " + id);
                model.addAttribute("order", null);
                return "admin/order/detail";
            }
            
            Order order = orderOpt.get();
            System.out.println("Found order: " + order.getId());
            
            // Create a safe order object to avoid lazy loading issues
            Order safeOrder = new Order();
            safeOrder.setId(order.getId());
            safeOrder.setFullName(order.getFullName());
            safeOrder.setEmail(order.getEmail());
            safeOrder.setPhoneNumber(order.getPhoneNumber());
            
            // Debug shipping address
            String shippingAddr = order.getShippingAddress();
            System.out.println("Original shipping address: [" + shippingAddr + "]");
            safeOrder.setShippingAddress(shippingAddr != null ? shippingAddr : "Chưa cung cấp");
            
            safeOrder.setNotes(order.getNotes());
            safeOrder.setOrderDate(order.getOrderDate());
            safeOrder.setTotalAmount(order.getTotalAmount());
            safeOrder.setStatus(order.getStatus());
            safeOrder.setPaymentMethod(order.getPaymentMethod());
            
            // Initialize empty list instead of lazy loading
            safeOrder.setOrderItems(new java.util.ArrayList<>());
            
            // Try to get order items count safely using repository
            int itemCount = 0;
            try {
                // Use direct repository query to avoid lazy loading
                Optional<Order> orderWithItems = orderRepository.findByIdWithItems(id);
                if (orderWithItems.isPresent()) {
                    Order fullOrder = orderWithItems.get();
                    if (fullOrder.getOrderItems() != null) {
                        itemCount = fullOrder.getOrderItems().size();
                        System.out.println("Successfully loaded " + itemCount + " items using repository");
                        
                        // Set the actual items to safe order
                        safeOrder.setOrderItems(fullOrder.getOrderItems());
                    }
                }
            } catch (Exception e) {
                System.out.println("Could not load order items via repository: " + e.getMessage());
                safeOrder.setOrderItems(new java.util.ArrayList<>());
            }
            
            System.out.println("Safe order created - Name: " + safeOrder.getFullName() + ", Address: [" + safeOrder.getShippingAddress() + "]");
            
            System.out.println("Adding safe order to model...");
            model.addAttribute("order", safeOrder);
            
            System.out.println("=== AdminOrderController.orderDetail() completed successfully ===");
            return "admin/order/detail";
            
        } catch (Exception e) {
            System.out.println("=== CRITICAL ERROR in orderDetail: " + e.getMessage() + " ===");
            e.printStackTrace();
            
            model.addAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            model.addAttribute("order", null);
            return "admin/order/detail";
        }
    }

    @PostMapping("/update-status")
    public String updateOrderStatus(@RequestParam Long orderId,
                                   @RequestParam Order.OrderStatus status,
                                   RedirectAttributes redirectAttributes,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        try {
            orderService.updateOrderStatus(orderId, status);
            
            // Kiểm tra nếu là AJAX request
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                response.setStatus(HttpServletResponse.SC_OK);
                return null; // Trả về null để không render view
            }
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Cập nhật trạng thái đơn hàng thành công!");
        } catch (Exception e) {
            // Kiểm tra nếu là AJAX request
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return null; // Trả về null để không render view
            }
            
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Có lỗi xảy ra: " + e.getMessage());
        }
        
        return "redirect:/admin/orders";
    }

    @PostMapping("/cancel/{id}")
    public String cancelOrder(@PathVariable Long id, 
                             RedirectAttributes redirectAttributes,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        try {
            orderService.cancelOrder(id);
            
            // Kiểm tra nếu là AJAX request
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                response.setStatus(HttpServletResponse.SC_OK);
                return null; // Trả về null để không render view
            }
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Hủy đơn hàng thành công!");
        } catch (Exception e) {
            // Kiểm tra nếu là AJAX request
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return null; // Trả về null để không render view
            }
            
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Có lỗi xảy ra: " + e.getMessage());
        }
        
        return "redirect:/admin/orders";
    }

    @PostMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Xóa đơn hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Có lỗi xảy ra: " + e.getMessage());
        }
        
        return "redirect:/admin/orders";
    }

    // AJAX endpoints for better user experience
    @PostMapping("/ajax/update-status")
    @ResponseBody
    public ResponseEntity<String> updateOrderStatusAjax(@RequestParam Long orderId,
                                                       @RequestParam Order.OrderStatus status) {
        try {
            orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/ajax/cancel/{id}")
    @ResponseBody
    public ResponseEntity<String> cancelOrderAjax(@PathVariable Long id) {
        try {
            orderService.cancelOrder(id);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
