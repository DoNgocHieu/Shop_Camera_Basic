package com.dangquang.watch.service;

import com.dangquang.watch.entity.User;
import com.dangquang.watch.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public long count() {
        return userRepository.count();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User save(User user) {
        // Encode password if it's not already encoded
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public User register(User user) {
        // Check if username or email already exists
        if (existsByUsername(user.getUsername())) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }
        if (existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng");
        }
        
        // Set default role
        user.setRole(User.Role.USER);
        user.setEnabled(true);
        
        return save(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUsernameAndIdNot(String username, Long id) {
        Optional<User> existing = userRepository.findByUsername(username);
        return existing.isPresent() && !existing.get().getId().equals(id);
    }

    public boolean existsByEmailAndIdNot(String email, Long id) {
        Optional<User> existing = userRepository.findByEmail(email);
        return existing.isPresent() && !existing.get().getId().equals(id);
    }

    // Thêm các method cần thiết cho quản lý admin
    public Page<User> findAllWithPagination(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public List<User> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(
                keyword, keyword, keyword);
    }

    public long countByRole(User.Role role) {
        return userRepository.countByRole(role);
    }

    public List<User> findByRole(User.Role role) {
        return userRepository.findByRole(role);
    }

    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // Update basic info
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setFullName(user.getFullName());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        existingUser.setRole(user.getRole());
        existingUser.setEnabled(user.isEnabled());

        // Only update password if provided
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(existingUser);
    }

    public User toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        user.setEnabled(!user.isEnabled());
        return userRepository.save(user);
    }

    public User changeUserRole(Long id, User.Role newRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        user.setRole(newRole);
        return userRepository.save(user);
    }
}
