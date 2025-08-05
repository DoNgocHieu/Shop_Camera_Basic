package com.dangquang.watch.controller.admin;

import com.dangquang.watch.entity.Category;
import com.dangquang.watch.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String listCategories(Model model) {
        try {
            List<Category> categories = categoryService.findAll();
            model.addAttribute("categories", categories);
            return "admin/category/list";
        } catch (Exception e) {
            e.printStackTrace(); // Log the error for debugging
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi tải danh sách danh mục: " + e.getMessage());
            model.addAttribute("categories", java.util.Collections.emptyList());
            return "admin/category/list";
        }
    }

    @GetMapping("/new")
    public String newCategory(Model model) {
        model.addAttribute("category", new Category());
        return "admin/category/form";
    }

    @GetMapping("/add")
    public String addCategory(Model model) {
        model.addAttribute("category", new Category());
        return "admin/category/form";
    }
    
    @PostMapping("/add")
    public String saveNewCategory(@Valid Category category, BindingResult result, 
                              Model model, RedirectAttributes redirectAttributes) {
        // Check for duplicate name
        if (categoryService.existsByName(category.getName())) {
            result.rejectValue("name", "error.category", "Tên danh mục đã tồn tại");
        }
        
        if (result.hasErrors()) {
            return "admin/category/form";
        }
        
        try {
            categoryService.save(category);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Thêm danh mục thành công!");
            return "redirect:/admin/categories";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            return "admin/category/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editCategory(@PathVariable Long id, Model model) {
        Category category = categoryService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
        
        model.addAttribute("category", category);
        return "admin/category/form";
    }

    @PostMapping("/save")
    public String saveCategory(@Valid Category category, BindingResult result, 
                              Model model, RedirectAttributes redirectAttributes) {
        
        // Check for duplicate name
        if (category.getId() == null && categoryService.existsByName(category.getName())) {
            result.rejectValue("name", "error.category", "Tên danh mục đã tồn tại");
        } else if (category.getId() != null && 
                  categoryService.existsByNameAndIdNot(category.getName(), category.getId())) {
            result.rejectValue("name", "error.category", "Tên danh mục đã tồn tại");
        }
        
        if (result.hasErrors()) {
            return "admin/category/form";
        }
        
        try {
            categoryService.save(category);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Lưu danh mục thành công!");
            return "redirect:/admin/categories";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            return "admin/category/form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Category category = categoryService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
            
            // Check if category has cameras
            if (!category.getCameras().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Không thể xóa danh mục này vì vẫn có máy ảnh thuộc danh mục");
                return "redirect:/admin/categories";
            }
            
            categoryService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Xóa danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Có lỗi xảy ra: " + e.getMessage());
        }
        
        return "redirect:/admin/categories";
    }
}
