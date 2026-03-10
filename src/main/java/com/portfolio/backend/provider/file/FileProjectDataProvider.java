package com.portfolio.backend.provider.file;

import com.portfolio.backend.entity.Project;
import com.portfolio.backend.provider.ProjectDataProvider;
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
public class FileProjectDataProvider implements ProjectDataProvider {
    
    private final FileDataLoader fileDataLoader;
    private final AtomicLong idCounter = new AtomicLong(1000);

    @Override
    public List<Project> findAll() {
        return fileDataLoader.getData().getProjects();
    }

    @Override
    public List<Project> findAllByOrderByDisplayOrderAsc() {
        return fileDataLoader.getData().getProjects().stream()
                .sorted(Comparator.comparing(Project::getDisplayOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    @Override
    public List<Project> findByFeaturedTrueOrderByDisplayOrderAsc() {
        return fileDataLoader.getData().getProjects().stream()
                .filter(Project::getFeatured)
                .sorted(Comparator.comparing(Project::getDisplayOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Project> findById(Long id) {
        return fileDataLoader.getData().getProjects().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    @Override
    public Project save(Project project) {
        if (project.getId() == null) {
            project.setId(idCounter.incrementAndGet());
        }
        List<Project> projects = fileDataLoader.getData().getProjects();
        projects.removeIf(p -> p.getId().equals(project.getId()));
        projects.add(project);
        fileDataLoader.saveData();
        return project;
    }

    @Override
    public void deleteById(Long id) {
        fileDataLoader.getData().getProjects().removeIf(p -> p.getId().equals(id));
        fileDataLoader.saveData();
    }

    @Override
    public boolean existsById(Long id) {
        return fileDataLoader.getData().getProjects().stream()
                .anyMatch(p -> p.getId().equals(id));
    }
}
