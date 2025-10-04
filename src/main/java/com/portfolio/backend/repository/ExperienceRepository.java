package com.portfolio.backend.repository;

import com.portfolio.backend.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    
    @EntityGraph(attributePaths = {"description"})
    @Query("SELECT e FROM Experience e ORDER BY e.displayOrder")
    List<Experience> findAllByOrderByDisplayOrder();
    
    @Override
    @EntityGraph(attributePaths = {"description"})
    List<Experience> findAll();
    
    @Override
    @EntityGraph(attributePaths = {"description"})
    Optional<Experience> findById(Long id);
}
