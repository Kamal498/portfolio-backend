package com.portfolio.backend.provider;

import com.portfolio.backend.entity.Blog;
import java.util.List;
import java.util.Optional;

public interface BlogDataProvider {
    List<Blog> findAll();
    List<Blog> findByPublishedTrueOrderByDateDesc();
    Optional<Blog> findById(Long id);
    Optional<Blog> findBySlug(String slug);
    Blog save(Blog blog);
    void deleteById(Long id);
    boolean existsById(Long id);
}
