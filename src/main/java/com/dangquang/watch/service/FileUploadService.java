package com.dangquang.watch.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileUploadService {
    
    @Value("${file.upload.dir:uploads/watches}")
    private String uploadDir;
    
    public String uploadImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Tệp không được để trống");
        }
        
        // Kiểm tra định dạng file
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Chỉ chấp nhận file ảnh");
        }
        
        // Tạo tên file duy nhất
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + fileExtension;
        
        // Tạo đường dẫn lưu file
        Path uploadPath = Paths.get("src/main/resources/static/" + uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        Path filePath = uploadPath.resolve(fileName);
        
        // Lưu file
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Trả về đường dẫn relative để lưu vào database
        return "/" + uploadDir + "/" + fileName;
    }
    
    public void deleteImage(String imageUrl) {
        if (imageUrl != null && imageUrl.startsWith("/" + uploadDir)) {
            try {
                Path filePath = Paths.get("src/main/resources/static" + imageUrl);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // Log error but don't throw exception
                System.err.println("Không thể xóa file: " + imageUrl);
            }
        }
    }
    
    public boolean isValidImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return false;
        }
        
        // Kiểm tra URL online
        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            return true;
        }
        
        // Kiểm tra đường dẫn local
        if (imageUrl.startsWith("/" + uploadDir)) {
            Path filePath = Paths.get("src/main/resources/static" + imageUrl);
            return Files.exists(filePath);
        }
        
        return false;
    }
}
