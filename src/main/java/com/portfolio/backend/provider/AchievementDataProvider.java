package com.portfolio.backend.provider;

import com.portfolio.backend.entity.Achievement;
import java.util.List;

public interface AchievementDataProvider {
    List<Achievement> findAllByOrderByDisplayOrder();
}
