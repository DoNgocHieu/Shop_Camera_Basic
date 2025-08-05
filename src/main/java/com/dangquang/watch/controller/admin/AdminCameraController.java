package com.dangquang.watch.controller.admin;

import com.dangquang.watch.entity.Camera;
import com.dangquang.watch.entity.Category;
import com.dangquang.watch.service.CameraService;
import com.dangquang.watch.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/cameras")
public class AdminCameraController {

    @Autowired
    private CameraService cameraService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    @Transactional(readOnly = true)
    public String listCameras(Model model) {
        try {
            System.out.println("=== AdminCameraController.listCameras() started ===");
            
            List<Camera> cameras = cameraService.findAll();
            System.out.println("Found active cameras: " + (cameras != null ? cameras.size() : 0));
            
            // Ensure cameras is never null
            if (cameras == null) {
                cameras = new java.util.ArrayList<>();
            }
            
            // Add categories for filter dropdown
            List<Category> categories = categoryService.findAll();
            System.out.println("Found categories: " + (categories != null ? categories.size() : 0));
            
            // Ensure categories is never null
            if (categories == null) {
                categories = new java.util.ArrayList<>();
            }
            
            model.addAttribute("cameras", cameras);
            model.addAttribute("categories", categories);
            model.addAttribute("totalCameras", cameras.size());
            
            System.out.println("=== AdminCameraController.listCameras() completed successfully ===");
            return "admin/camera/list";
            
        } catch (Exception e) {
            System.out.println("=== CRITICAL ERROR in listCameras: " + e.getMessage() + " ===");
            e.printStackTrace();
            
            model.addAttribute("cameras", new java.util.ArrayList<>());
            model.addAttribute("categories", new java.util.ArrayList<>());
            model.addAttribute("totalCameras", 0);
            model.addAttribute("errorMessage", "Có lỗi hệ thống: " + e.getMessage());
            
            return "admin/camera/list";
        }
    }

    @GetMapping("/new")
    @Transactional(readOnly = true)
    public String newCamera(Model model) {
        try {
            System.out.println("=== AdminCameraController.newCamera() started ===");
            
            model.addAttribute("camera", new Camera());
            
            List<Category> categories = categoryService.findAll();
            System.out.println("Found categories: " + (categories != null ? categories.size() : 0));
            
            // Ensure categories is never null
            if (categories == null) {
                categories = new java.util.ArrayList<>();
            }
            
            model.addAttribute("categories", categories);
            
            System.out.println("=== AdminCameraController.newCamera() completed successfully ===");
            return "admin/camera/form";
            
        } catch (Exception e) {
            System.out.println("=== CRITICAL ERROR in newCamera: " + e.getMessage() + " ===");
            e.printStackTrace();
            
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi tải form: " + e.getMessage());
            return "redirect:/admin/cameras";
        }
    }

    @GetMapping("/add")
    @Transactional(readOnly = true)
    public String addCamera(Model model) {
        try {
            System.out.println("=== AdminCameraController.addCamera() started ===");
            
            model.addAttribute("camera", new Camera());
            
            List<Category> categories = categoryService.findAll();
            System.out.println("Found categories: " + (categories != null ? categories.size() : 0));
            
            // Ensure categories is never null
            if (categories == null) {
                categories = new java.util.ArrayList<>();
            }
            
            model.addAttribute("categories", categories);
            
            System.out.println("=== AdminCameraController.addCamera() completed successfully ===");
            return "admin/camera/form";
            
        } catch (Exception e) {
            System.out.println("=== CRITICAL ERROR in addCamera: " + e.getMessage() + " ===");
            e.printStackTrace();
            
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi tải form: " + e.getMessage());
            return "redirect:/admin/cameras";
        }
    }

    @GetMapping("/edit/{id}")
    @Transactional(readOnly = true)
    public String editCamera(@PathVariable Long id, Model model) {
        try {
            System.out.println("=== AdminCameraController.editCamera() started for ID: " + id + " ===");
            
            Camera camera = cameraService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy máy ảnh"));
            
            System.out.println("Found camera: " + camera.getName());
            
            List<Category> categories = categoryService.findAll();
            System.out.println("Found categories: " + (categories != null ? categories.size() : 0));
            
            // Ensure categories is never null
            if (categories == null) {
                categories = new java.util.ArrayList<>();
            }
            
            model.addAttribute("camera", camera);
            model.addAttribute("categories", categories);
            
            System.out.println("=== AdminCameraController.editCamera() completed successfully ===");
            return "admin/camera/form";
            
        } catch (Exception e) {
            System.out.println("=== CRITICAL ERROR in editCamera: " + e.getMessage() + " ===");
            e.printStackTrace();
            
            model.addAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/cameras";
        }
    }

    @PostMapping("/save")
    @Transactional
    public String saveCamera(@Valid Camera camera, BindingResult result, 
                           Model model, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== AdminCameraController.saveCamera() started ===");
            
            if (result.hasErrors()) {
                System.out.println("Validation errors found");
                List<Category> categories = categoryService.findAll();
                if (categories == null) {
                    categories = new java.util.ArrayList<>();
                }
                model.addAttribute("categories", categories);
                return "admin/camera/form";
            }
            
            System.out.println("Saving camera: " + camera.getName());
            System.out.println("Camera imageUrl: " + camera.getImageUrl());
            System.out.println("Camera ID: " + camera.getId());
            
            cameraService.save(camera);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Lưu máy ảnh thành công!");
            
            System.out.println("=== AdminCameraController.saveCamera() completed successfully ===");
            return "redirect:/admin/cameras";
            
        } catch (Exception e) {
            System.out.println("=== CRITICAL ERROR in saveCamera: " + e.getMessage() + " ===");
            e.printStackTrace();
            
            model.addAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            
            List<Category> categories = null;
            try {
                categories = categoryService.findAll();
            } catch (Exception ex) {
                categories = new java.util.ArrayList<>();
            }
            if (categories == null) {
                categories = new java.util.ArrayList<>();
            }
            model.addAttribute("categories", categories);
            
            return "admin/camera/form";
        }
    }

    @PostMapping("/delete/{id}")
    @Transactional
    @ResponseBody
    public ResponseEntity<?> deleteCamera(@PathVariable Long id) {
        try {
            cameraService.deleteById(id);
            return ResponseEntity.ok().build(); 
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Có lỗi xảy ra: " + e.getMessage());
        }
    }
    
    // Xóa cứng camera khỏi database
    @PostMapping("/delete-hard/{id}")
    @Transactional
    @ResponseBody
    public ResponseEntity<?> hardDeleteCamera(@PathVariable Long id) {
        try {
            cameraService.hardDeleteById(id);
            return ResponseEntity.ok().build(); 
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Có lỗi xảy ra khi xóa: " + e.getMessage());
        }
    }
    
    // Toggle trạng thái active/inactive
    @PostMapping("/toggle-active/{id}")
    @Transactional
    @ResponseBody
    public ResponseEntity<?> toggleActive(@PathVariable Long id) {
        try {
            cameraService.toggleActive(id);
            return ResponseEntity.ok().build(); 
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Có lỗi xảy ra khi thay đổi trạng thái: " + e.getMessage());
        }
    }
    
    @PostMapping("/upload-image")
    @ResponseBody
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            System.out.println("=== Upload image started ===");
            System.out.println("File name: " + file.getOriginalFilename());
            System.out.println("File size: " + file.getSize());
            System.out.println("Content type: " + file.getContentType());
            
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File không được để trống");
            }
            
            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("File phải là hình ảnh");
            }
            
            // Validate file size (max 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("File quá lớn. Tối đa 5MB");
            }
            
            // Create upload directory if not exists
            String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/images/cameras/";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                System.out.println("Upload directory created: " + created + " at: " + uploadDir);
            }
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = System.currentTimeMillis() + "_" + UUID.randomUUID().toString() + extension;
            
            // Save file
            File destinationFile = new File(uploadDir + filename);
            file.transferTo(destinationFile);
            
            System.out.println("File saved successfully at: " + destinationFile.getAbsolutePath());
            
            // Return URL
            String imageUrl = "/images/cameras/" + filename;
            System.out.println("Returning image URL: " + imageUrl);
            return ResponseEntity.ok(imageUrl);
            
        } catch (Exception e) {
            System.out.println("Error uploading image: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Có lỗi xảy ra khi upload: " + e.getMessage());
        }
    }

    @PostMapping("/add-stock")
    public String addStock(@RequestParam Long cameraId,
                          @RequestParam Integer addQuantity,
                          @RequestParam(required = false) String note,
                          RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== AdminCameraController.addStock() started ===");
            System.out.println("Camera ID: " + cameraId + ", Add Quantity: " + addQuantity);
            
            // Validate input
            if (addQuantity == null || addQuantity < 1 || addQuantity > 1000) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Số lượng thêm phải từ 1 đến 1000!");
                return "redirect:/admin/cameras";
            }
            
            // Get current camera
            Camera camera = cameraService.findById(cameraId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy máy ảnh"));
            
            // Update stock
            int currentStock = camera.getStockQuantity() != null ? camera.getStockQuantity() : 0;
            int newStock = currentStock + addQuantity;
            
            camera.setStockQuantity(newStock);
            cameraService.save(camera);
            
            // Success message
            String message = String.format("Đã thêm %d chiếc vào kho cho \"%s\". Tồn kho mới: %d chiếc", 
                addQuantity, camera.getName(), newStock);
            if (note != null && !note.trim().isEmpty()) {
                message += " (Ghi chú: " + note.trim() + ")";
            }
            
            redirectAttributes.addFlashAttribute("successMessage", message);
            
            System.out.println("=== AdminCameraController.addStock() completed successfully ===");
            return "redirect:/admin/cameras";
            
        } catch (Exception e) {
            System.out.println("=== ERROR in addStock: " + e.getMessage() + " ===");
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/cameras";
        }
    }
    
    // Debug endpoint to check file existence (remove in production)
    @GetMapping("/debug-image/{filename}")
    @ResponseBody
    public ResponseEntity<String> debugImage(@PathVariable String filename) {
        try {
            String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/images/cameras/";
            File file = new File(uploadDir + filename);
            
            StringBuilder result = new StringBuilder();
            result.append("Upload directory: ").append(uploadDir).append("\n");
            result.append("File path: ").append(file.getAbsolutePath()).append("\n");
            result.append("File exists: ").append(file.exists()).append("\n");
            result.append("File size: ").append(file.exists() ? file.length() : "N/A").append(" bytes\n");
            result.append("URL should be: /images/cameras/").append(filename).append("\n");
            
            return ResponseEntity.ok(result.toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
