package com.portfolio.backend.provider.database;

import com.portfolio.backend.entity.PersonalInfo;
import com.portfolio.backend.provider.PersonalInfoDataProvider;
import com.portfolio.backend.repository.PersonalInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@ConditionalOnProperty(name = "portfolio.datasource.type", havingValue = "DATABASE", matchIfMissing = true)
@RequiredArgsConstructor
public class DatabasePersonalInfoDataProvider implements PersonalInfoDataProvider {
    
    private final PersonalInfoRepository personalInfoRepository;

    @Override
    public Optional<PersonalInfo> findFirst() {
        return personalInfoRepository.findAll().stream().findFirst();
    }

    @Override
    public PersonalInfo save(PersonalInfo personalInfo) {
        return personalInfoRepository.save(personalInfo);
    }
}
