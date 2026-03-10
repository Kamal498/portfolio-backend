package com.portfolio.backend.provider.file;

import com.portfolio.backend.entity.Education;
import com.portfolio.backend.provider.EducationDataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "portfolio.datasource.type", havingValue = "FILE")
@RequiredArgsConstructor
public class FileEducationDataProvider implements EducationDataProvider {
    
    private final FileDataLoader fileDataLoader;

    @Override
    public List<Education> findAllByOrderByDisplayOrder() {
        return fileDataLoader.getData().getEducation().stream()
                .sorted(Comparator.comparing(Education::getDisplayOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }
}
