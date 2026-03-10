package com.portfolio.backend.service;

import com.portfolio.backend.entity.Achievement;
import com.portfolio.backend.provider.AchievementDataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static com.portfolio.backend.config.CacheConfig.ACHIEVEMENTS_CACHE;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementDataProvider achievementDataProvider;

    @Cacheable(ACHIEVEMENTS_CACHE)
    public List<Achievement> getAllAchievements() {
        return achievementDataProvider.findAllByOrderByDisplayOrder();
    }
}
