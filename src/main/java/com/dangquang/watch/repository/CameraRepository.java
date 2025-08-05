package com.dangquang.watch.repository;

import com.dangquang.watch.entity.Camera;
import com.dangquang.watch.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CameraRepository extends JpaRepository<Camera, Long> {
    List<Camera> findByActiveTrue();
    List<Camera> findByCategory(Category category);
    List<Camera> findByCategoryAndActiveTrue(Category category);
    List<Camera> findByBrandIgnoreCase(String brand);
    List<Camera> findByBrandIgnoreCaseAndActiveTrue(String brand);
    
    @Query("SELECT c FROM Camera c WHERE c.active = true AND " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Camera> searchByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT c FROM Camera c WHERE c.active = true AND c.price BETWEEN :minPrice AND :maxPrice")
    List<Camera> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    @Query("SELECT DISTINCT c.brand FROM Camera c WHERE c.active = true ORDER BY c.brand")
    List<String> findDistinctBrands();
}
