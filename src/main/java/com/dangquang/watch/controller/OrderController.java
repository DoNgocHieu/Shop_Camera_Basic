package com.dangquang.watch.controller;

import com.dangquang.watch.dto.OrderRequest;
import com.dangquang.watch.entity.CartItem;
import com.dangquang.watch.entity.Order;
import com.dangquang.watch.entity.User;
import com.dangquang.watch.service.CartService;
import com.dangquang.watch.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @GetMapping("/checkout")
    public String checkout(@AuthenticationPrincipal User user, Model model) {
        List<CartItem> cartItems = cartService.getCartItems(user);
        
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }
        
        BigDecimal total = cartService.getCartTotal(user);
        
        // Create OrderRequest with user's info pre-filled
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setFullName(user.getFullName());
        orderRequest.setPhone(user.getPhoneNumber());
        orderRequest.setEmail(user.getEmail());
        orderRequest.setAddress(user.getAddress());
        
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        model.addAttribute("user", user);
        model.addAttribute("orderRequest", orderRequest);
        
        return "order/checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@Valid @ModelAttribute OrderRequest orderRequest,
                            BindingResult bindingResult,
                            @AuthenticationPrincipal User user,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        if (bindingResult.hasErrors()) {
            // Get cart items again for display
            List<CartItem> cartItems = cartService.getCartItems(user);
            BigDecimal total = cartService.getCartTotal(user);
            
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("total", total);
            model.addAttribute("user", user);
            return "order/checkout";
        }
        
        try {
            Order order = orderService.createOrder(user, orderRequest);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Đặt hàng thành công! Mã đơn hàng: " + order.getId());
            // Redirect to order history instead of payment selection
            return "redirect:/order/history";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/order/checkout";
        }
    }

    @GetMapping("/history")
    public String orderHistory(@AuthenticationPrincipal User user, Model model) {
        try {
            List<Order> orders = orderService.findByUser(user);
            // Trigger loading of all order items to avoid lazy loading issues
            for (Order order : orders) {
                order.getOrderItems().size(); // Trigger lazy loading
                for (var item : order.getOrderItems()) {
                    item.getCamera().getName(); // Trigger lazy loading for camera
                }
            }
            model.addAttribute("orders", orders);
            return "order/history";
        } catch (Exception e) {
            e.printStackTrace(); // Log the error for debugging
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi tải lịch sử đơn hàng: " + e.getMessage());
            model.addAttribute("orders", java.util.Collections.emptyList());
            return "order/history";
        }
    }

    @GetMapping("/detail")
    public String orderDetail(@RequestParam Long id,
                             @AuthenticationPrincipal User user,
                             Model model) {
        try {
            Order order = orderService.findByIdWithItems(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với id=" + id));
            if (order.getUser() == null) {
                model.addAttribute("errorMessage", "Order không có thông tin user!");
                model.addAttribute("order", null);
                return "order/detail";
            }
            if (!order.getUser().getId().equals(user.getId())) {
                model.addAttribute("errorMessage", "Bạn không có quyền xem đơn hàng này!");
                model.addAttribute("order", null);
                return "order/detail";
            }
            // Debug log: số lượng orderItems
            System.out.println("Order " + id + " có " + order.getOrderItems().size() + " items");
            for (var item : order.getOrderItems()) {
                if (item.getCamera() == null) {
                    model.addAttribute("errorMessage", "OrderItem id=" + item.getId() + " không có camera!");
                    model.addAttribute("order", null);
                    return "order/detail";
                }
                System.out.println("OrderItem " + item.getId() + " camera: " + item.getCamera().getName());
                if (item.getCamera().getCategory() == null) {
                    System.out.println("Camera id=" + item.getCamera().getId() + " không có category!");
                }
            }
            model.addAttribute("order", order);
            return "order/detail";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            model.addAttribute("order", null);
            return "order/detail";
        }
    }

    @PostMapping("/cancel")
    public String cancelOrder(@RequestParam Long id,
                             @AuthenticationPrincipal User user,
                             RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
            
            // Check if user owns this order (compare by ID)
            if (!order.getUser().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Không có quyền hủy đơn hàng này");
                return "redirect:/order/history";
            }
            
            // Check if order can be cancelled (only PENDING status)
            if (order.getStatus() != Order.OrderStatus.PENDING) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Chỉ có thể hủy đơn hàng khi đang ở trạng thái 'Chờ xác nhận'");
                return "redirect:/order/history";
            }
            
            orderService.cancelOrder(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Đã hủy đơn hàng thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/order/history";
    }
}
