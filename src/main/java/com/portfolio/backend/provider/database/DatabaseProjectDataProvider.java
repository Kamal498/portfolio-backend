package com.portfolio.backend.provider.database;

import com.portfolio.backend.entity.Project;
import com.portfolio.backend.provider.ProjectDataProvider;
import com.portfolio.backend.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@ConditionalOnProperty(name = "portfolio.datasource.type", havingValue = "DATABASE", matchIfMissing = true)
@RequiredArgsConstructor
public class DatabaseProjectDataProvider implements ProjectDataProvider {
    
    private final ProjectRepository projectRepository;

    @Override
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @Override
    public List<Project> findAllByOrderByDisplayOrderAsc() {
        return projectRepository.findAllByOrderByDisplayOrderAsc();
    }

    @Override
    public List<Project> findByFeaturedTrueOrderByDisplayOrderAsc() {
        return projectRepository.findByFeaturedTrueOrderByDisplayOrderAsc();
    }

    @Override
    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }

    @Override
    public Project save(Project project) {
        return projectRepository.save(project);
    }

    @Override
    public void deleteById(Long id) {
        projectRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return projectRepository.existsById(id);
    }
}
