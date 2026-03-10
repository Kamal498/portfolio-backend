package com.portfolio.backend.provider.file;

import com.portfolio.backend.entity.Blog;
import com.portfolio.backend.provider.BlogDataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "portfolio.datasource.type", havingValue = "FILE")
@RequiredArgsConstructor
public class FileBlogDataProvider implements BlogDataProvider {
    
    private final BlogDataLoader blogDataLoader;
    private final AtomicLong idCounter = new AtomicLong(1000);

    @Override
    public List<Blog> findAll() {
        return blogDataLoader.getBlogs();
    }

    @Override
    public List<Blog> findByPublishedTrueOrderByDateDesc() {
        return blogDataLoader.getBlogs().stream()
                .filter(Blog::getPublished)
                .sorted(Comparator.comparing(Blog::getDate).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Blog> findById(Long id) {
        return blogDataLoader.getBlogs().stream()
                .filter(b -> b.getId().equals(id))
                .findFirst();
    }

    @Override
    public Optional<Blog> findBySlug(String slug) {
        return blogDataLoader.getBlogs().stream()
                .filter(b -> b.getSlug().equals(slug))
                .findFirst();
    }

    @Override
    public Blog save(Blog blog) {
        if (blog.getId() == null) {
            blog.setId(idCounter.incrementAndGet());
        }
        List<Blog> blogs = blogDataLoader.getBlogs();
        blogs.removeIf(b -> b.getId().equals(blog.getId()));
        blogs.add(blog);
        blogDataLoader.saveData();
        return blog;
    }

    @Override
    public void deleteById(Long id) {
        blogDataLoader.getBlogs().removeIf(b -> b.getId().equals(id));
        blogDataLoader.saveData();
    }

    @Override
    public boolean existsById(Long id) {
        return blogDataLoader.getBlogs().stream()
                .anyMatch(b -> b.getId().equals(id));
    }
}
