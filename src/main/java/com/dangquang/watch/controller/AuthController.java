package com.dangquang.watch.controller;

import com.dangquang.watch.entity.User;
import com.dangquang.watch.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerPost(@Valid User user, BindingResult result, 
                              Model model, RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "auth/register";
        }
        
        try {
            userService.register(user);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/register";
        }
    }
    
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/access-denied";
    }
}
