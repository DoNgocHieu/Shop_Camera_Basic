package com.dangquang.watch.service;

import com.dangquang.watch.entity.Camera;
import com.dangquang.watch.entity.Category;
import com.dangquang.watch.repository.CameraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CameraService {

    @Autowired
    private CameraRepository cameraRepository;

    public List<Camera> findAll() {
        return cameraRepository.findAll();
    }

    public List<Camera> findAllActive() {
        return cameraRepository.findByActiveTrue();
    }

    public Optional<Camera> findById(Long id) {
        return cameraRepository.findById(id);
    }

    public List<Camera> findByCategory(Category category) {
        return cameraRepository.findByCategoryAndActiveTrue(category);
    }

    public List<Camera> findByBrand(String brand) {
        return cameraRepository.findByBrandIgnoreCaseAndActiveTrue(brand);
    }

    public List<Camera> searchByKeyword(String keyword) {
        return cameraRepository.searchByKeyword(keyword);
    }

    public List<Camera> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return cameraRepository.findByPriceRange(minPrice, maxPrice);
    }

    public List<String> getDistinctBrands() {
        return cameraRepository.findDistinctBrands();
    }

    public Camera save(Camera camera) {
        return cameraRepository.save(camera);
    }

    public void deleteById(Long id) {
        Optional<Camera> camera = cameraRepository.findById(id);
        if (camera.isPresent()) {
            Camera c = camera.get();
            c.setActive(false); 
            cameraRepository.save(c);
        }
    }

    // Xóa cứng khỏi database
    public void hardDeleteById(Long id) {
        cameraRepository.deleteById(id);
    }

    // Toggle trạng thái active/inactive
    public void toggleActive(Long id) {
        Optional<Camera> cameraOpt = cameraRepository.findById(id);
        if (cameraOpt.isPresent()) {
            Camera camera = cameraOpt.get();
            camera.setActive(!camera.getActive());
            cameraRepository.save(camera);
        }
    }

    public void updateStock(Long cameraId, Integer quantity) {
        Optional<Camera> cameraOpt = cameraRepository.findById(cameraId);
        if (cameraOpt.isPresent()) {
            Camera camera = cameraOpt.get();
            camera.setStockQuantity(camera.getStockQuantity() - quantity);
            cameraRepository.save(camera);
        }
    }

    public boolean isInStock(Long cameraId, Integer quantity) {
        Optional<Camera> cameraOpt = cameraRepository.findById(cameraId);
        return cameraOpt.isPresent() && cameraOpt.get().getStockQuantity() >= quantity;
    }
}
