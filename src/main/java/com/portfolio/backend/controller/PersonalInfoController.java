package com.portfolio.backend.controller;

import com.portfolio.backend.entity.PersonalInfo;
import com.portfolio.backend.service.PersonalInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/personal-info")
@RequiredArgsConstructor
public class PersonalInfoController {

    private final PersonalInfoService personalInfoService;

    @GetMapping
    public ResponseEntity<PersonalInfo> getPersonalInfo() {
        return ResponseEntity.ok(personalInfoService.getPersonalInfo());
    }

    @PutMapping
    public ResponseEntity<PersonalInfo> updatePersonalInfo(@RequestBody PersonalInfo personalInfo) {
        return ResponseEntity.ok(personalInfoService.updatePersonalInfo(personalInfo));
    }
}
