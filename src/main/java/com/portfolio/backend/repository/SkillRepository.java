package com.portfolio.backend.repository;

import com.portfolio.backend.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    
    @EntityGraph(attributePaths = {"items"})
    @Query("SELECT s FROM Skill s ORDER BY s.displayOrder")
    List<Skill> findAllByOrderByDisplayOrder();
    
    @Override
    @EntityGraph(attributePaths = {"items"})
    List<Skill> findAll();
    
    @Override
    @EntityGraph(attributePaths = {"items"})
    Optional<Skill> findById(Long id);
}
