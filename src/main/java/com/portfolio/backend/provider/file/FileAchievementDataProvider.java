package com.portfolio.backend.provider.file;

import com.portfolio.backend.entity.Achievement;
import com.portfolio.backend.provider.AchievementDataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "portfolio.datasource.type", havingValue = "FILE")
@RequiredArgsConstructor
public class FileAchievementDataProvider implements AchievementDataProvider {
    
    private final FileDataLoader fileDataLoader;

    @Override
    public List<Achievement> findAllByOrderByDisplayOrder() {
        return fileDataLoader.getData().getAchievements().stream()
                .sorted(Comparator.comparing(Achievement::getDisplayOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }
}
