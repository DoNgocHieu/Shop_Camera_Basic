package com.dangquang.watch.repository;

import com.dangquang.watch.entity.Order;
import com.dangquang.watch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    
    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.orderItems oi " +
           "LEFT JOIN FETCH oi.camera c " +
           "LEFT JOIN FETCH c.category " +
           "WHERE o.user = :user " +
           "ORDER BY o.orderDate DESC")
    List<Order> findByUserOrderByOrderDateDesc(@Param("user") User user);
    
    List<Order> findByStatus(Order.OrderStatus status);
    
    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.orderItems oi " +
           "LEFT JOIN FETCH oi.camera c " +
           "LEFT JOIN FETCH c.category " +
           "WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);
    
    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.orderItems oi " +
           "LEFT JOIN FETCH oi.camera c " +
           "LEFT JOIN FETCH c.category " +
           "ORDER BY o.orderDate DESC")
    List<Order> findAllOrderByOrderDateDesc();
    
    // Count by status
    long countByStatus(Order.OrderStatus status);
}
