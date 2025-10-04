package com.portfolio.backend.repository;

import com.portfolio.backend.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    @EntityGraph(attributePaths = {"tags"})
    @Query("SELECT p FROM Project p WHERE p.featured = true ORDER BY p.displayOrder ASC")
    List<Project> findByFeaturedTrueOrderByDisplayOrderAsc();
    
    @EntityGraph(attributePaths = {"tags"})
    @Query("SELECT p FROM Project p ORDER BY p.displayOrder ASC")
    List<Project> findAllByOrderByDisplayOrderAsc();
    
    @Override
    @EntityGraph(attributePaths = {"tags"})
    List<Project> findAll();
    
    @Override
    @EntityGraph(attributePaths = {"tags"})
    Optional<Project> findById(Long id);
}
