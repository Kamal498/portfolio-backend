package com.portfolio.backend.provider.file;

import com.portfolio.backend.entity.Skill;
import com.portfolio.backend.provider.SkillDataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "portfolio.datasource.type", havingValue = "FILE")
@RequiredArgsConstructor
public class FileSkillDataProvider implements SkillDataProvider {
    
    private final FileDataLoader fileDataLoader;

    @Override
    public List<Skill> findAllByOrderByDisplayOrder() {
        return fileDataLoader.getData().getSkills().stream()
                .sorted(Comparator.comparing(Skill::getDisplayOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }
}
