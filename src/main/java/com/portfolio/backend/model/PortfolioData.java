package com.portfolio.backend.model;

import com.portfolio.backend.entity.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class PortfolioData {
    private PersonalInfo personalInfo;
    private List<Project> projects = new ArrayList<>();
    private List<Experience> experiences = new ArrayList<>();
    private List<Skill> skills = new ArrayList<>();
    private List<Education> education = new ArrayList<>();
    private List<Achievement> achievements = new ArrayList<>();
    // Blogs are now stored in a separate file - see BlogDataLoader
}
