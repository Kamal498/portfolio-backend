package com.portfolio.backend.repository;

import com.portfolio.backend.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    
    @EntityGraph(attributePaths = {"tags"})
    @Query("SELECT b FROM Blog b WHERE b.slug = :slug")
    Optional<Blog> findBySlug(String slug);
    
    @EntityGraph(attributePaths = {"tags"})
    @Query("SELECT b FROM Blog b WHERE b.published = true ORDER BY b.date DESC")
    List<Blog> findByPublishedTrueOrderByDateDesc();
    
    @EntityGraph(attributePaths = {"tags"})
    List<Blog> findByTagsContainingIgnoreCase(String tag);
    
    @EntityGraph(attributePaths = {"tags"})
    @Query("SELECT DISTINCT b FROM Blog b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(b.excerpt) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Blog> findByTitleContainingIgnoreCaseOrExcerptContainingIgnoreCase(@Param("query") String query1, @Param("query") String query2);
    
    @Override
    @EntityGraph(attributePaths = {"tags"})
    List<Blog> findAll();
    
    @Override
    @EntityGraph(attributePaths = {"tags"})
    Optional<Blog> findById(Long id);
}
