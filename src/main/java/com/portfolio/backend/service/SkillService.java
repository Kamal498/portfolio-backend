package com.portfolio.backend.service;

import com.portfolio.backend.entity.Skill;
import com.portfolio.backend.provider.SkillDataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillDataProvider skillDataProvider;

    public List<Skill> getAllSkills() {
        return skillDataProvider.findAllByOrderByDisplayOrder();
    }
}
