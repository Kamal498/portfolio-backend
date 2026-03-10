package com.portfolio.backend.provider.database;

import com.portfolio.backend.entity.Achievement;
import com.portfolio.backend.provider.AchievementDataProvider;
import com.portfolio.backend.repository.AchievementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "portfolio.datasource.type", havingValue = "DATABASE", matchIfMissing = true)
@RequiredArgsConstructor
public class DatabaseAchievementDataProvider implements AchievementDataProvider {
    
    private final AchievementRepository achievementRepository;

    @Override
    public List<Achievement> findAllByOrderByDisplayOrder() {
        return achievementRepository.findAllByOrderByDisplayOrder();
    }
}
