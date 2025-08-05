package com.dangquang.watch.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull(message = "Đơn hàng không được để trống")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "camera_id", nullable = false)
    @NotNull(message = "Camera không được để trống")
    private Camera camera;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    @Column(nullable = false)
    private Integer quantity;

    @NotNull(message = "Giá không được để trống")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    // Constructors
    public OrderItem() {}

    public OrderItem(Order order, Camera camera, Integer quantity, BigDecimal price) {
        this.order = order;
        this.camera = camera;
        this.quantity = quantity;
        this.price = price;
    }

    // Method to calculate total price
    public BigDecimal getTotalPrice() {
        return price.multiply(new BigDecimal(quantity));
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
