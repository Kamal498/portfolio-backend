package com.portfolio.backend.provider;

import com.portfolio.backend.entity.Project;
import java.util.List;
import java.util.Optional;

public interface ProjectDataProvider {
    List<Project> findAll();
    List<Project> findAllByOrderByDisplayOrderAsc();
    List<Project> findByFeaturedTrueOrderByDisplayOrderAsc();
    Optional<Project> findById(Long id);
    Project save(Project project);
    void deleteById(Long id);
    boolean existsById(Long id);
}
