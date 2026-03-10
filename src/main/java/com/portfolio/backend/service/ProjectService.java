package com.portfolio.backend.service;

import com.portfolio.backend.dto.ProjectDTO;
import com.portfolio.backend.entity.Project;
import com.portfolio.backend.exception.ResourceNotFoundException;
import com.portfolio.backend.provider.ProjectDataProvider;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.portfolio.backend.config.CacheConfig.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectDataProvider projectDataProvider;
    private final ModelMapper modelMapper;

    @Cacheable(PROJECTS_CACHE)
    public List<ProjectDTO> getAllProjects() {
        return projectDataProvider.findAllByOrderByDisplayOrderAsc().stream()
                .map(project -> modelMapper.map(project, ProjectDTO.class))
                .toList();
    }

    @Cacheable(FEATURED_PROJECTS_CACHE)
    public List<ProjectDTO> getFeaturedProjects() {
        return projectDataProvider.findByFeaturedTrueOrderByDisplayOrderAsc().stream()
                .map(project -> modelMapper.map(project, ProjectDTO.class))
                .toList();
    }

    @Cacheable(value = PROJECT_BY_ID_CACHE, key = "#id")
    public ProjectDTO getProjectById(Long id) {
        Project project = projectDataProvider.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        return modelMapper.map(project, ProjectDTO.class);
    }

    @Transactional
    @CacheEvict(value = {PROJECTS_CACHE, FEATURED_PROJECTS_CACHE}, allEntries = true)
    public ProjectDTO createProject(ProjectDTO projectDTO) {
        Project project = modelMapper.map(projectDTO, Project.class);
        Project savedProject = projectDataProvider.save(project);
        return modelMapper.map(savedProject, ProjectDTO.class);
    }

    @Transactional
    @CacheEvict(value = {PROJECTS_CACHE, FEATURED_PROJECTS_CACHE, PROJECT_BY_ID_CACHE}, allEntries = true)
    public ProjectDTO updateProject(Long id, ProjectDTO projectDTO) {
        Project existingProject = projectDataProvider.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        existingProject.setTitle(projectDTO.getTitle());
        existingProject.setDescription(projectDTO.getDescription());
        existingProject.setImage(projectDTO.getImage());
        existingProject.setTags(projectDTO.getTags());
        existingProject.setGithubUrl(projectDTO.getGithubUrl());
        existingProject.setDemoUrl(projectDTO.getDemoUrl());
        existingProject.setFeatured(projectDTO.getFeatured());

        Project updatedProject = projectDataProvider.save(existingProject);
        return modelMapper.map(updatedProject, ProjectDTO.class);
    }

    @Transactional
    @CacheEvict(value = {PROJECTS_CACHE, FEATURED_PROJECTS_CACHE, PROJECT_BY_ID_CACHE}, allEntries = true)
    public void deleteProject(Long id) {
        if (!projectDataProvider.existsById(id)) {
            throw new ResourceNotFoundException("Project not found with id: " + id);
        }
        projectDataProvider.deleteById(id);
    }
}
