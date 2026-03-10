package com.portfolio.backend.provider;

import com.portfolio.backend.entity.Skill;
import java.util.List;

public interface SkillDataProvider {
    List<Skill> findAllByOrderByDisplayOrder();
}
