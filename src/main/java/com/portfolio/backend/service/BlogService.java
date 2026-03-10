package com.portfolio.backend.service;

import com.portfolio.backend.dto.BlogDTO;
import com.portfolio.backend.entity.Blog;
import com.portfolio.backend.exception.ResourceNotFoundException;
import com.portfolio.backend.provider.BlogDataProvider;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogDataProvider blogDataProvider;
    private final ModelMapper modelMapper;

    public List<BlogDTO> getAllBlogs() {
        return blogDataProvider.findAll().stream()
                .map(blog -> modelMapper.map(blog, BlogDTO.class))
                .collect(Collectors.toList());
    }

    public List<BlogDTO> getPublishedBlogs() {
        return blogDataProvider.findByPublishedTrueOrderByDateDesc().stream()
                .map(blog -> modelMapper.map(blog, BlogDTO.class))
                .collect(Collectors.toList());
    }

    public BlogDTO getBlogById(Long id) {
        Blog blog = blogDataProvider.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with id: " + id));
        return modelMapper.map(blog, BlogDTO.class);
    }

    public BlogDTO getBlogBySlug(String slug) {
        Blog blog = blogDataProvider.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with slug: " + slug));
        return modelMapper.map(blog, BlogDTO.class);
    }

    public List<BlogDTO> searchBlogs(String query) {
        return blogDataProvider.findAll().stream()
                .filter(blog -> blog.getTitle().toLowerCase().contains(query.toLowerCase()) 
                        || (blog.getExcerpt() != null && blog.getExcerpt().toLowerCase().contains(query.toLowerCase())))
                .map(blog -> modelMapper.map(blog, BlogDTO.class))
                .toList();
    }

    @Transactional
    public BlogDTO createBlog(BlogDTO blogDTO) {
        Blog blog = modelMapper.map(blogDTO, Blog.class);
        
        // Generate slug from title if not provided
        if (blog.getSlug() == null || blog.getSlug().isEmpty()) {
            blog.setSlug(generateSlug(blog.getTitle()));
        }
        
        if (blog.getDate() == null) {
            blog.setDate(LocalDateTime.now());
        }
        
        Blog savedBlog = blogDataProvider.save(blog);
        return modelMapper.map(savedBlog, BlogDTO.class);
    }

    @Transactional
    public BlogDTO updateBlog(Long id, BlogDTO blogDTO) {
        Blog existingBlog = blogDataProvider.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with id: " + id));

        existingBlog.setTitle(blogDTO.getTitle());
        existingBlog.setSlug(blogDTO.getSlug() != null ? blogDTO.getSlug() : generateSlug(blogDTO.getTitle()));
        existingBlog.setExcerpt(blogDTO.getExcerpt());
        existingBlog.setContent(blogDTO.getContent());
        existingBlog.setAuthor(blogDTO.getAuthor());
        existingBlog.setTags(blogDTO.getTags());
        existingBlog.setReadTime(blogDTO.getReadTime());
        existingBlog.setPublished(blogDTO.getPublished());

        Blog updatedBlog = blogDataProvider.save(existingBlog);
        return modelMapper.map(updatedBlog, BlogDTO.class);
    }

    @Transactional
    public void deleteBlog(Long id) {
        if (!blogDataProvider.existsById(id)) {
            throw new ResourceNotFoundException("Blog not found with id: " + id);
        }
        blogDataProvider.deleteById(id);
    }

    private String generateSlug(String title) {
        return title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }
}
