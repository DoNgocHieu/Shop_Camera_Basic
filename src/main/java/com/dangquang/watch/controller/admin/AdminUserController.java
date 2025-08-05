package com.dangquang.watch.controller.admin;

import com.dangquang.watch.entity.User;
import com.dangquang.watch.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String listUsers(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           @RequestParam(defaultValue = "") String search,
                           Model model) {
        
        List<User> users;
        
        if (!search.trim().isEmpty()) {
            users = userService.searchUsers(search);
        } else {
            users = userService.findAll();
        }
        
        // Add statistics
        long totalUsers = userService.findAll().size();
        long adminCount = userService.countByRole(User.Role.ADMIN);
        long userCount = userService.countByRole(User.Role.USER);
        long disabledCount = userService.findAll().stream()
                .mapToLong(user -> user.isEnabled() ? 0 : 1)
                .sum();
        
        model.addAttribute("users", users);
        model.addAttribute("search", search);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("adminCount", adminCount);
        model.addAttribute("userCount", userCount);
        model.addAttribute("disabledCount", disabledCount);
        
        return "admin/user/list";
    }

    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        
        // Add admin count for security checks
        long adminCount = userService.countByRole(User.Role.ADMIN);
        
        model.addAttribute("user", user);
        model.addAttribute("adminCount", adminCount);
        return "admin/user/view";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute User user, 
                         RedirectAttributes redirectAttributes) {
        try {
            userService.save(user);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Đã thêm người dùng " + user.getUsername() + " thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Có lỗi xảy ra: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/edit")
    public String editUser(@PathVariable Long id,
                          @ModelAttribute User user,
                          RedirectAttributes redirectAttributes) {
        try {
            user.setId(id);
            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Đã cập nhật thông tin người dùng thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Có lỗi xảy ra: " + e.getMessage());
        }
        
        return "redirect:/admin/users/" + id;
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id, 
                                  RedirectAttributes redirectAttributes) {
        try {
            User user = userService.toggleUserStatus(id);
            
            String status = user.isEnabled() ? "kích hoạt" : "vô hiệu hóa";
            redirectAttributes.addFlashAttribute("successMessage", 
                "Đã " + status + " tài khoản " + user.getUsername() + " thành công");
                
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Có lỗi xảy ra: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/change-role")
    public String changeUserRole(@PathVariable Long id,
                                @RequestParam User.Role role,
                                RedirectAttributes redirectAttributes) {
        try {
            User user = userService.changeUserRole(id, role);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Đã thay đổi quyền của " + user.getUsername() + " thành " + role.name());
                
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Có lỗi xảy ra: " + e.getMessage());
        }
        
        return "redirect:/admin/users/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, 
                            RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
            
            // Không cho phép xóa admin cuối cùng
            if (user.getRole() == User.Role.ADMIN) {
                long adminCount = userService.countByRole(User.Role.ADMIN);
                if (adminCount <= 1) {
                    redirectAttributes.addFlashAttribute("errorMessage", 
                        "Không thể xóa admin cuối cùng trong hệ thống");
                    return "redirect:/admin/users";
                }
            }
            
            String username = user.getUsername();
            userService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Đã xóa tài khoản " + username + " thành công");
                
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Có lỗi xảy ra: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }

    // API endpoint for AJAX calls
    @GetMapping("/{id}/api")
    @ResponseBody
    public User getUserApi(@PathVariable Long id) {
        return userService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
    }
}
