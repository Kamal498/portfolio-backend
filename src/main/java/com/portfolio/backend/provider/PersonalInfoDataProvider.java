package com.portfolio.backend.provider;

import com.portfolio.backend.entity.PersonalInfo;
import java.util.Optional;

public interface PersonalInfoDataProvider {
    Optional<PersonalInfo> findFirst();
    PersonalInfo save(PersonalInfo personalInfo);
}
