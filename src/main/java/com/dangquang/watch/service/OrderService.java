package com.dangquang.watch.service;

import com.dangquang.watch.dto.OrderRequest;
import com.dangquang.watch.dto.OrderStats;
import com.dangquang.watch.entity.*;
import com.dangquang.watch.repository.OrderRepository;
import com.dangquang.watch.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private CameraService cameraService;

    public List<Order> findAll() {
        try {
            return orderRepository.findAllOrderByOrderDateDesc();
        } catch (Exception e) {
            System.out.println("Error in findAll orders: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    public List<Order> findByUser(User user) {
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findByIdWithItems(id);
    }

    public Optional<Order> findByIdWithItems(Long id) {
        return orderRepository.findByIdWithItems(id);
    }

    public List<Order> findByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Transactional
    public Order createOrder(User user, OrderRequest orderRequest) {
        List<CartItem> cartItems = cartService.getCartItems(user);
        
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống");
        }

        // Validate stock availability
        for (CartItem item : cartItems) {
            if (!cameraService.isInStock(item.getCamera().getId(), item.getQuantity())) {
                throw new RuntimeException("Sản phẩm " + item.getCamera().getName() + " không đủ hàng");
            }
        }

        // Calculate total amount
        BigDecimal totalAmount = cartService.getCartTotal(user);

        // Create order
        Order order = new Order(user, totalAmount, orderRequest.getAddress(), orderRequest.getPhone());
        order.setNotes(orderRequest.getNotes());
        order.setFullName(orderRequest.getFullName());
        order.setEmail(orderRequest.getEmail());
        
        // Set payment method if provided
        if (orderRequest.getPaymentMethod() != null) {
            try {
                Order.PaymentMethod paymentMethod = Order.PaymentMethod.valueOf(orderRequest.getPaymentMethod());
                order.setPaymentMethod(paymentMethod);
            } catch (IllegalArgumentException e) {
                // Default to COD if invalid payment method
                order.setPaymentMethod(Order.PaymentMethod.COD);
            }
        } else {
            order.setPaymentMethod(Order.PaymentMethod.COD);
        }
        
        order = orderRepository.save(order);

        // Create order items and update stock
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem(order, cartItem.getCamera(), 
                                               cartItem.getQuantity(), cartItem.getPrice());
            orderItemRepository.save(orderItem);
            
            // Update stock
            cameraService.updateStock(cartItem.getCamera().getId(), cartItem.getQuantity());
        }

        // Clear cart
        cartService.clearCart(user);

        return order;
    }

    // Keep the old method for backward compatibility
    @Transactional
    public Order createOrder(User user, String shippingAddress, String phoneNumber, String notes) {
        List<CartItem> cartItems = cartService.getCartItems(user);
        
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống");
        }

        // Validate stock availability
        for (CartItem item : cartItems) {
            if (!cameraService.isInStock(item.getCamera().getId(), item.getQuantity())) {
                throw new RuntimeException("Sản phẩm " + item.getCamera().getName() + " không đủ hàng");
            }
        }

        // Calculate total amount
        BigDecimal totalAmount = cartService.getCartTotal(user);

        // Create order
        Order order = new Order(user, totalAmount, shippingAddress, phoneNumber);
        order.setNotes(notes);
        order = orderRepository.save(order);

        // Create order items and update stock
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem(order, cartItem.getCamera(), 
                                               cartItem.getQuantity(), cartItem.getPrice());
            orderItemRepository.save(orderItem);
            
            // Update stock
            cameraService.updateStock(cartItem.getCamera().getId(), cartItem.getQuantity());
        }

        // Clear cart
        cartService.clearCart(user);

        return order;
    }

    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) {
            throw new RuntimeException("Không tìm thấy đơn hàng");
        }

        Order order = orderOpt.get();
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public void cancelOrder(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) {
            throw new RuntimeException("Không tìm thấy đơn hàng");
        }

        Order order = orderOpt.get();
        // Chỉ cho phép hủy khi đơn hàng đang ở trạng thái PENDING (Chờ xác nhận)
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Chỉ có thể hủy đơn hàng khi đang ở trạng thái 'Chờ xác nhận'");
        }

        // Restore stock
        for (OrderItem item : order.getOrderItems()) {
            Camera camera = item.getCamera();
            camera.setStockQuantity(camera.getStockQuantity() + item.getQuantity());
            cameraService.save(camera);
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Transactional
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

    public OrderStats getOrderStats() {
        try {
            long pending = orderRepository.countByStatus(Order.OrderStatus.PENDING);
            long processing = orderRepository.countByStatus(Order.OrderStatus.PROCESSING);
            long shipping = orderRepository.countByStatus(Order.OrderStatus.SHIPPED);
            long delivered = orderRepository.countByStatus(Order.OrderStatus.DELIVERED);
            long cancelled = orderRepository.countByStatus(Order.OrderStatus.CANCELLED);
            long totalOrders = pending + processing + shipping + delivered + cancelled;
            return new OrderStats(totalOrders, pending, processing, shipping, delivered, cancelled);
        } catch (Exception e) {
            System.out.println("Error in getOrderStats: " + e.getMessage());
            e.printStackTrace();
            // Return default stats on error
            return new OrderStats(0, 0, 0, 0, 0, 0);
        }
    }

    public BigDecimal getTotalRevenue() {
        List<Order> deliveredOrders = orderRepository.findByStatus(Order.OrderStatus.DELIVERED);
        return deliveredOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public long countPendingOrders() {
        // Đơn chưa thành công là các đơn không phải DELIVERED và không phải CANCELLED
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .filter(o -> o.getStatus() != Order.OrderStatus.DELIVERED && o.getStatus() != Order.OrderStatus.CANCELLED)
                .count();
    }

    /**
     * Đếm số lượng đơn hàng theo category camera (mỗi order chỉ tính 1 lần cho mỗi category xuất hiện trong orderItems)
     */
    public java.util.Map<String, Integer> countOrdersByCameraCategory(List<com.dangquang.watch.entity.Category> categories) {
        List<Order> orders = orderRepository.findAll();
        java.util.Map<String, Integer> result = new java.util.HashMap<>();
        for (var cat : categories) {
            int count = 0;
            for (Order order : orders) {
                boolean hasCategory = order.getOrderItems().stream()
                    .anyMatch(oi -> oi.getCamera() != null && oi.getCamera().getCategory() != null && oi.getCamera().getCategory().getId().equals(cat.getId()));
                if (hasCategory) count++;
            }
            result.put(cat.getName(), count);
        }
        return result;
    }

    /**
     * Tính tổng doanh thu theo category camera (tính tổng price*quantity của orderItems theo category)
     */
    public java.util.Map<String, java.math.BigDecimal> revenueByCameraCategory(java.util.List<com.dangquang.watch.entity.Category> categories) {
        java.util.Map<String, java.math.BigDecimal> result = new java.util.LinkedHashMap<>();
        for (var cat : categories) {
            java.math.BigDecimal sum = java.math.BigDecimal.ZERO;
            for (Order order : orderRepository.findAll()) {
                for (var oi : order.getOrderItems()) {
                    if (oi.getCamera() != null && oi.getCamera().getCategory() != null && oi.getCamera().getCategory().getId().equals(cat.getId())) {
                        sum = sum.add(oi.getPrice().multiply(new java.math.BigDecimal(oi.getQuantity())));
                    }
                }
            }
            result.put(cat.getName(), sum);
        }
        return result;
    }

    /**
     * Tính tổng doanh thu theo category camera (trả về double để Thymeleaf render JS an toàn)
     */
    public java.util.Map<String, Double> revenueByCameraCategoryAsDouble(java.util.List<com.dangquang.watch.entity.Category> categories) {
        java.util.Map<String, Double> result = new java.util.LinkedHashMap<>();
        for (var cat : categories) {
            java.math.BigDecimal sum = java.math.BigDecimal.ZERO;
            for (Order order : orderRepository.findAll()) {
                for (var oi : order.getOrderItems()) {
                    if (oi.getCamera() != null && oi.getCamera().getCategory() != null && oi.getCamera().getCategory().getId().equals(cat.getId())) {
                        sum = sum.add(oi.getPrice().multiply(new java.math.BigDecimal(oi.getQuantity())));
                    }
                }
            }
            result.put(cat.getName(), sum.doubleValue());
        }
        return result;
    }
}
