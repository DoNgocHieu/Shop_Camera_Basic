package com.dangquang.watch.service;

import com.dangquang.watch.entity.Category;
import com.dangquang.watch.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    public Optional<Category> findByName(String name) {
        return categoryRepository.findByName(name);
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    public boolean existsByNameAndIdNot(String name, Long id) {
        Optional<Category> existing = categoryRepository.findByName(name);
        return existing.isPresent() && !existing.get().getId().equals(id);
    }
}
