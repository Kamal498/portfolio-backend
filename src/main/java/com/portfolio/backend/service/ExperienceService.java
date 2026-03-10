package com.portfolio.backend.service;

import com.portfolio.backend.entity.Experience;
import com.portfolio.backend.provider.ExperienceDataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperienceService {

    private final ExperienceDataProvider experienceDataProvider;

    public List<Experience> getAllExperiences() {
        return experienceDataProvider.findAllByOrderByDisplayOrder();
    }
}
