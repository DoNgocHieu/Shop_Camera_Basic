package com.dangquang.watch.repository;

import com.dangquang.watch.entity.CartItem;
import com.dangquang.watch.entity.User;
import com.dangquang.watch.entity.Camera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.camera c LEFT JOIN FETCH c.category WHERE ci.user = :user")
    List<CartItem> findByUser(@Param("user") User user);
    
    Optional<CartItem> findByUserAndCamera(User user, Camera camera);
    void deleteByUser(User user);
    void deleteByUserAndCamera(User user, Camera camera);
}
