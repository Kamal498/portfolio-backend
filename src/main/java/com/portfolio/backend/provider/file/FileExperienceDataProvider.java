package com.portfolio.backend.provider.file;

import com.portfolio.backend.entity.Experience;
import com.portfolio.backend.provider.ExperienceDataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "portfolio.datasource.type", havingValue = "FILE")
@RequiredArgsConstructor
public class FileExperienceDataProvider implements ExperienceDataProvider {
    
    private final FileDataLoader fileDataLoader;

    @Override
    public List<Experience> findAllByOrderByDisplayOrder() {
        return fileDataLoader.getData().getExperiences().stream()
                .sorted(Comparator.comparing(Experience::getDisplayOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }
}
