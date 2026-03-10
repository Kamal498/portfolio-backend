package com.portfolio.backend.provider.database;

import com.portfolio.backend.entity.Skill;
import com.portfolio.backend.provider.SkillDataProvider;
import com.portfolio.backend.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "portfolio.datasource.type", havingValue = "DATABASE", matchIfMissing = true)
@RequiredArgsConstructor
public class DatabaseSkillDataProvider implements SkillDataProvider {
    
    private final SkillRepository skillRepository;

    @Override
    public List<Skill> findAllByOrderByDisplayOrder() {
        return skillRepository.findAllByOrderByDisplayOrder();
    }
}
