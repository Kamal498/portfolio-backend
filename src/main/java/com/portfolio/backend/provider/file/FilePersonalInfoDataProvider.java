package com.portfolio.backend.provider.file;

import com.portfolio.backend.entity.PersonalInfo;
import com.portfolio.backend.provider.PersonalInfoDataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@ConditionalOnProperty(name = "portfolio.datasource.type", havingValue = "FILE")
@RequiredArgsConstructor
public class FilePersonalInfoDataProvider implements PersonalInfoDataProvider {
    
    private final FileDataLoader fileDataLoader;

    @Override
    public Optional<PersonalInfo> findFirst() {
        return Optional.ofNullable(fileDataLoader.getData().getPersonalInfo());
    }

    @Override
    public PersonalInfo save(PersonalInfo personalInfo) {
        fileDataLoader.getData().setPersonalInfo(personalInfo);
        fileDataLoader.saveData();
        return personalInfo;
    }
}
