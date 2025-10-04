package com.portfolio.backend.service;

import com.portfolio.backend.entity.Achievement;
import com.portfolio.backend.repository.AchievementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;

    public List<Achievement> getAllAchievements() {
        return achievementRepository.findAllByOrderByDisplayOrder();
    }
}
