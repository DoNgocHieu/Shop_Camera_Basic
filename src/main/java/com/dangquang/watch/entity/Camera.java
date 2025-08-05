package com.dangquang.watch.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cameras")
public class Camera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên camera không được để trống")
    @Size(max = 200, message = "Tên camera không được quá 200 ký tự")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Hãng sản xuất không được để trống")
    @Size(max = 100, message = "Hãng sản xuất không được quá 100 ký tự")
    @Column(nullable = false)
    private String brand;

    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Size(max = 255, message = "URL hình ảnh không được quá 255 ký tự")
    private String imageUrl;

    @Min(value = 0, message = "Số lượng không được âm")
    @Column(nullable = false)
    private Integer stockQuantity = 0;

    @Column(nullable = false)
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Danh mục không được để trống")
    private Category category;

    // Constructors
    public Camera() {}

    public Camera(String name, String brand, BigDecimal price, String description, 
                String imageUrl, Integer stockQuantity, Category category) {
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.stockQuantity = stockQuantity;
        this.category = category;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
