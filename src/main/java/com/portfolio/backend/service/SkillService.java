package com.portfolio.backend.service;

import com.portfolio.backend.entity.Skill;
import com.portfolio.backend.provider.SkillDataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static com.portfolio.backend.config.CacheConfig.SKILLS_CACHE;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillDataProvider skillDataProvider;

    @Cacheable(SKILLS_CACHE)
    public List<Skill> getAllSkills() {
        return skillDataProvider.findAllByOrderByDisplayOrder();
    }
}
