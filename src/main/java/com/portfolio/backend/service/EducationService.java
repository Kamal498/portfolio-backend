package com.portfolio.backend.service;

import com.portfolio.backend.entity.Education;
import com.portfolio.backend.repository.EducationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EducationService {

    private final EducationRepository educationRepository;

    public List<Education> getAllEducation() {
        return educationRepository.findAllByOrderByDisplayOrder();
    }
}
