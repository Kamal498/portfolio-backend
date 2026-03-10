package com.portfolio.backend.service;

import com.portfolio.backend.entity.PersonalInfo;
import com.portfolio.backend.exception.ResourceNotFoundException;
import com.portfolio.backend.provider.PersonalInfoDataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.portfolio.backend.config.CacheConfig.PERSONAL_INFO_CACHE;

@Service
@RequiredArgsConstructor
public class PersonalInfoService {

    private final PersonalInfoDataProvider personalInfoDataProvider;

    @Cacheable(PERSONAL_INFO_CACHE)
    public PersonalInfo getPersonalInfo() {
        return personalInfoDataProvider.findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Personal info not found"));
    }

    @Transactional
    @CacheEvict(value = PERSONAL_INFO_CACHE, allEntries = true)
    public PersonalInfo updatePersonalInfo(PersonalInfo personalInfo) {
        PersonalInfo existing = getPersonalInfo();
        existing.setName(personalInfo.getName());
        existing.setTitle(personalInfo.getTitle());
        existing.setBio(personalInfo.getBio());
        existing.setEmail(personalInfo.getEmail());
        existing.setPhone(personalInfo.getPhone());
        existing.setLocation(personalInfo.getLocation());
        existing.setAvatar(personalInfo.getAvatar());
        existing.setGithubUrl(personalInfo.getGithubUrl());
        existing.setLinkedinUrl(personalInfo.getLinkedinUrl());
        existing.setTwitterUrl(personalInfo.getTwitterUrl());
        existing.setPortfolioUrl(personalInfo.getPortfolioUrl());
        existing.setResumeUrl(personalInfo.getResumeUrl());
        return personalInfoDataProvider.save(existing);
    }
}
