package com.portfolio.backend.provider.database;

import com.portfolio.backend.entity.Blog;
import com.portfolio.backend.provider.BlogDataProvider;
import com.portfolio.backend.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@ConditionalOnProperty(name = "portfolio.datasource.type", havingValue = "DATABASE", matchIfMissing = true)
@RequiredArgsConstructor
public class DatabaseBlogDataProvider implements BlogDataProvider {
    
    private final BlogRepository blogRepository;

    @Override
    public List<Blog> findAll() {
        return blogRepository.findAll();
    }

    @Override
    public List<Blog> findByPublishedTrueOrderByDateDesc() {
        return blogRepository.findByPublishedTrueOrderByDateDesc();
    }

    @Override
    public Optional<Blog> findById(Long id) {
        return blogRepository.findById(id);
    }

    @Override
    public Optional<Blog> findBySlug(String slug) {
        return blogRepository.findBySlug(slug);
    }

    @Override
    public Blog save(Blog blog) {
        return blogRepository.save(blog);
    }

    @Override
    public void deleteById(Long id) {
        blogRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return blogRepository.existsById(id);
    }
}
