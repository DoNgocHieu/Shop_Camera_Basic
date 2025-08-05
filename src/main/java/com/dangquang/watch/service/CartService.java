package com.dangquang.watch.service;

import com.dangquang.watch.entity.CartItem;
import com.dangquang.watch.entity.User;
import com.dangquang.watch.entity.Camera;
import com.dangquang.watch.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CameraService cameraService;

    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByUser(user);
    }

    @Transactional
    public void addToCart(User user, Long cameraId, Integer quantity) {
        Optional<Camera> cameraOpt = cameraService.findById(cameraId);
        if (!cameraOpt.isPresent()) {
            throw new RuntimeException("Không tìm thấy camera");
        }

        Camera camera = cameraOpt.get();
        if (!cameraService.isInStock(cameraId, quantity)) {
            throw new RuntimeException("Không đủ hàng trong kho");
        }

        Optional<CartItem> existingItem = cartItemRepository.findByUserAndCamera(user, camera);
        
        if (existingItem.isPresent()) {
            // Update quantity if item already exists in cart
            CartItem cartItem = existingItem.get();
            int newQuantity = cartItem.getQuantity() + quantity;
            
            if (!cameraService.isInStock(cameraId, newQuantity)) {
                throw new RuntimeException("Không đủ hàng trong kho");
            }
            
            cartItem.setQuantity(newQuantity);
            cartItemRepository.save(cartItem);
        } else {
            // Create new cart item
            CartItem cartItem = new CartItem(user, camera, quantity, camera.getPrice());
            cartItemRepository.save(cartItem);
        }
    }

    @Transactional
    public void updateCartItem(User user, Long cartItemId, Integer quantity) {
        Optional<CartItem> cartItemOpt = cartItemRepository.findById(cartItemId);
        if (!cartItemOpt.isPresent()) {
            throw new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng");
        }

        CartItem cartItem = cartItemOpt.get();
        if (!cartItem.getUser().equals(user)) {
            throw new RuntimeException("Không có quyền truy cập");
        }

        if (!cameraService.isInStock(cartItem.getCamera().getId(), quantity)) {
            throw new RuntimeException("Không đủ hàng trong kho");
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }

    @Transactional
    public void removeFromCart(User user, Long cartItemId) {
        Optional<CartItem> cartItemOpt = cartItemRepository.findById(cartItemId);
        if (!cartItemOpt.isPresent()) {
            throw new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng");
        }

        CartItem cartItem = cartItemOpt.get();
        if (!cartItem.getUser().equals(user)) {
            throw new RuntimeException("Không có quyền truy cập");
        }

        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }

    public BigDecimal getCartTotal(User user) {
        List<CartItem> cartItems = getCartItems(user);
        return cartItems.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getCartItemCount(User user) {
        List<CartItem> cartItems = getCartItems(user);
        return cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}
