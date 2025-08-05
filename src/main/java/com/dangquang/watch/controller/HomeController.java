package com.dangquang.watch.controller;

import com.dangquang.watch.entity.Camera;
import com.dangquang.watch.entity.Category;
import com.dangquang.watch.service.CameraService;
import com.dangquang.watch.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private CameraService cameraService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public String home(Model model) {
        List<Camera> featuredCameras = cameraService.findAllActive();
        List<Category> categories = categoryService.findAll();
        List<String> brands = cameraService.getDistinctBrands();
        
        model.addAttribute("featuredCameras", featuredCameras);
        model.addAttribute("categories", categories);
        model.addAttribute("brands", brands);
        
        return "index";
    }

    @GetMapping("/shop")
    public String shop(@RequestParam(required = false) Long categoryId,
                      @RequestParam(required = false) String brand,
                      @RequestParam(required = false) String keyword,
                      @RequestParam(required = false) BigDecimal minPrice,
                      @RequestParam(required = false) BigDecimal maxPrice,
                      Model model) {
        
        List<Camera> cameras;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            cameras = cameraService.searchByKeyword(keyword.trim());
        } else if (categoryId != null) {
            Category category = categoryService.findById(categoryId).orElse(null);
            if (category != null) {
                cameras = cameraService.findByCategory(category);
            } else {
                cameras = cameraService.findAllActive();
            }
        } else if (brand != null && !brand.trim().isEmpty()) {
            cameras = cameraService.findByBrand(brand.trim());
        } else if (minPrice != null && maxPrice != null) {
            cameras = cameraService.findByPriceRange(minPrice, maxPrice);
        } else {
            cameras = cameraService.findAllActive();
        }
        
        List<Category> categories = categoryService.findAll();
        List<String> brands = cameraService.getDistinctBrands();
        
        model.addAttribute("cameras", cameras);
        model.addAttribute("categories", categories);
        model.addAttribute("brands", brands);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedBrand", brand);
        model.addAttribute("keyword", keyword);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        
        return "shop";
    }

    @GetMapping("/camera")
    public String cameraDetail(@RequestParam Long id, Model model) {
        Camera camera = cameraService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy máy ảnh"));
        
        List<Camera> relatedCameras = cameraService.findByCategory(camera.getCategory())
                .stream()
                .filter(c -> !c.getId().equals(id))
                .limit(4)
                .toList();
        
        model.addAttribute("camera", camera);
        model.addAttribute("relatedCameras", relatedCameras);
        
        return "camera-detail";
    }
}
