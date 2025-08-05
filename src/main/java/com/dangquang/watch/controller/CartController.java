package com.dangquang.watch.controller;

import com.dangquang.watch.entity.CartItem;
import com.dangquang.watch.entity.User;
import com.dangquang.watch.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    @Transactional(readOnly = true)
    public String cart(@AuthenticationPrincipal User user, Model model) {
        List<CartItem> cartItems = cartService.getCartItems(user);
        BigDecimal total = cartService.getCartTotal(user);
        
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long cameraId,
                           @RequestParam(defaultValue = "1") Integer quantity,
                           @AuthenticationPrincipal User user,
                           RedirectAttributes redirectAttributes) {
        try {
            cartService.addToCart(user, cameraId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Đã thêm sản phẩm vào giỏ hàng");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/camera?id=" + cameraId;
    }

    @PostMapping("/update")
    public String updateCart(@RequestParam Long cartItemId,
                            @RequestParam Integer quantity,
                            @AuthenticationPrincipal User user,
                            RedirectAttributes redirectAttributes) {
        try {
            if (quantity <= 0) {
                cartService.removeFromCart(user, cartItemId);
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Đã xóa sản phẩm khỏi giỏ hàng");
            } else {
                cartService.updateCartItem(user, cartItemId, quantity);
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Đã cập nhật giỏ hàng");
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long cartItemId,
                                @AuthenticationPrincipal User user,
                                RedirectAttributes redirectAttributes) {
        try {
            cartService.removeFromCart(user, cartItemId);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Đã xóa sản phẩm khỏi giỏ hàng");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/cart";
    }

    // Add new endpoint for updating quantity via path variables (used by JavaScript)
    @GetMapping("/ajax/update/{cartItemId}/{quantity}")
    @ResponseBody
    public String updateCartItemById(@PathVariable Long cartItemId,
                                    @PathVariable Integer quantity,
                                    @AuthenticationPrincipal User user) {
        try {
            if (quantity <= 0) {
                cartService.removeFromCart(user, cartItemId);
                return "success";
            } else {
                cartService.updateCartItem(user, cartItemId, quantity);
                return "success";
            }
        } catch (RuntimeException e) {
            return "error";
        }
    }

    @GetMapping("/ajax/remove/{cartItemId}")
    @ResponseBody
    public String removeFromCartById(@PathVariable Long cartItemId,
                                        @AuthenticationPrincipal User user) {
        try {
            cartService.removeFromCart(user, cartItemId);
            return "success";
        } catch (RuntimeException e) {
            return "error";
        }
    }

    @PostMapping("/clear")
    public String clearCart(@AuthenticationPrincipal User user,
                           RedirectAttributes redirectAttributes) {
        cartService.clearCart(user);
        redirectAttributes.addFlashAttribute("successMessage", 
            "Đã xóa tất cả sản phẩm khỏi giỏ hàng");
        
        return "redirect:/cart";
    }

    // Test method - remove in production
    @GetMapping("/test")
    public String testCart(Model model) {
        model.addAttribute("cartItems", new java.util.ArrayList<>());
        model.addAttribute("total", java.math.BigDecimal.ZERO);
        return "cart";
    }
}
