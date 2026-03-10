package com.portfolio.backend.provider;

import com.portfolio.backend.entity.Education;
import java.util.List;

public interface EducationDataProvider {
    List<Education> findAllByOrderByDisplayOrder();
}
