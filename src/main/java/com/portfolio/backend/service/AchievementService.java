package com.portfolio.backend.service;

import com.portfolio.backend.entity.Achievement;
import com.portfolio.backend.provider.AchievementDataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementDataProvider achievementDataProvider;

    public List<Achievement> getAllAchievements() {
        return achievementDataProvider.findAllByOrderByDisplayOrder();
    }
}
