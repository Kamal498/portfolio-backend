package com.portfolio.backend.service;

import com.portfolio.backend.entity.Education;
import com.portfolio.backend.provider.EducationDataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EducationService {

    private final EducationDataProvider educationDataProvider;

    public List<Education> getAllEducation() {
        return educationDataProvider.findAllByOrderByDisplayOrder();
    }
}
