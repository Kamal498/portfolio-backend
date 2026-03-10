package com.portfolio.backend.provider;

import com.portfolio.backend.entity.Experience;
import java.util.List;

public interface ExperienceDataProvider {
    List<Experience> findAllByOrderByDisplayOrder();
}
