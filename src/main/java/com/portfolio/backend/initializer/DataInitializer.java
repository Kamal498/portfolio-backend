package com.portfolio.backend.initializer;

import com.portfolio.backend.entity.*;
import com.portfolio.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final PersonalInfoRepository personalInfoRepository;
    private final BlogRepository blogRepository;
    private final ProjectRepository projectRepository;
    private final AchievementRepository achievementRepository;
    private final SkillRepository skillRepository;
    private final ExperienceRepository experienceRepository;
    private final EducationRepository educationRepository;
    private final Environment environment;

    @Override
    public void run(String... args) {
        // Skip initialization in production environment
        String[] activeProfiles = environment.getActiveProfiles();
        boolean isProduction = Arrays.asList(activeProfiles).contains("prod");
        
        if (isProduction) {
            log.info("Production environment detected - skipping data initialization");
            return;
        }
        
        log.info("Development environment - starting data initialization...");
        
        // Only initialize if database is empty
        if (personalInfoRepository.count() == 0) {
            initializePersonalInfo();
        } else {
            log.info("Personal info already exists, skipping initialization");
        }
        
        if (skillRepository.count() == 0) {
            initializeSkills();
        } else {
            log.info("Skills already exist, skipping initialization");
        }
        
        if (projectRepository.count() == 0) {
            initializeProjects();
        } else {
            log.info("Projects already exist, skipping initialization");
        }
        
        if (achievementRepository.count() == 0) {
            initializeAchievements();
        } else {
            log.info("Achievements already exist, skipping initialization");
        }
        
        if (experienceRepository.count() == 0) {
            initializeExperiences();
        } else {
            log.info("Experiences already exist, skipping initialization");
        }
        
        if (educationRepository.count() == 0) {
            initializeEducation();
        } else {
            log.info("Education already exists, skipping initialization");
        }
        
        if (blogRepository.count() == 0) {
            initializeBlogs();
        } else {
            log.info("Blogs already exist, skipping initialization");
        }
        
        log.info("Data initialization completed!");
    }

    private void initializePersonalInfo() {
        PersonalInfo personalInfo = new PersonalInfo();
        personalInfo.setName("Your Name");
        personalInfo.setTitle("Full Stack Developer");
        personalInfo.setBio("Passionate developer with expertise in building scalable web applications.");
        personalInfo.setEmail("your.email@example.com");
        personalInfo.setPhone("+1 (123) 456-7890");
        personalInfo.setLocation("Your City, Country");
        personalInfo.setAvatar("https://via.placeholder.com/150");
        personalInfo.setGithubUrl("https://github.com/yourusername");
        personalInfo.setLinkedinUrl("https://linkedin.com/in/yourusername");
        personalInfo.setTwitterUrl("https://twitter.com/yourusername");
        personalInfo.setPortfolioUrl("https://yourwebsite.com");
        personalInfo.setResumeUrl("/path/to/resume.pdf");
        
        personalInfoRepository.save(personalInfo);
        log.info("Personal info initialized");
    }

    private void initializeSkills() {
        List<Skill> skills = Arrays.asList(
            createSkill("Frontend", Arrays.asList("React", "JavaScript", "HTML/CSS", "TypeScript", "Vue.js", "Tailwind CSS"), 1),
            createSkill("Backend", Arrays.asList("Node.js", "Express", "Python", "Django", "REST APIs", "GraphQL"), 2),
            createSkill("Database", Arrays.asList("MongoDB", "PostgreSQL", "MySQL", "Redis", "Firebase"), 3),
            createSkill("Tools & Others", Arrays.asList("Git", "Docker", "AWS", "CI/CD", "Jest", "Webpack"), 4)
        );
        
        skillRepository.saveAll(skills);
        log.info("Skills initialized: {} categories", skills.size());
    }

    private Skill createSkill(String category, List<String> items, int order) {
        Skill skill = new Skill();
        skill.setCategory(category);
        skill.setItems(items);
        skill.setDisplayOrder(order);
        return skill;
    }

    private void initializeProjects() {
        List<Project> projects = Arrays.asList(
            createProject("E-Commerce Platform", 
                "A full-featured e-commerce platform with payment integration, inventory management, and user authentication.",
                "https://via.placeholder.com/400x300",
                Arrays.asList("React", "Node.js", "MongoDB", "Stripe"),
                "https://github.com/yourusername/project1",
                "https://project1-demo.com",
                true, 1),
            createProject("Task Management App",
                "Real-time collaborative task management application with drag-and-drop functionality.",
                "https://via.placeholder.com/400x300",
                Arrays.asList("React", "Firebase", "Material-UI"),
                "https://github.com/yourusername/project2",
                "https://project2-demo.com",
                true, 2),
            createProject("Weather Dashboard",
                "Beautiful weather dashboard with detailed forecasts and interactive maps.",
                "https://via.placeholder.com/400x300",
                Arrays.asList("React", "OpenWeather API", "Chart.js"),
                "https://github.com/yourusername/project3",
                "https://project3-demo.com",
                false, 3)
        );
        
        projectRepository.saveAll(projects);
        log.info("Projects initialized: {}", projects.size());
    }

    private Project createProject(String title, String description, String image, 
                                  List<String> tags, String githubUrl, String demoUrl, 
                                  boolean featured, int order) {
        Project project = new Project();
        project.setTitle(title);
        project.setDescription(description);
        project.setImage(image);
        project.setTags(tags);
        project.setGithubUrl(githubUrl);
        project.setDemoUrl(demoUrl);
        project.setFeatured(featured);
        project.setDisplayOrder(order);
        return project;
    }

    private void initializeAchievements() {
        List<Achievement> achievements = Arrays.asList(
            createAchievement("Best Hackathon Project", "Tech Conference 2023", "June 2023",
                "Won first place for developing an AI-powered code review assistant.", "🏆", 1),
            createAchievement("Open Source Contributor", "Various Projects", "2022 - Present",
                "Active contributor to popular open-source projects with 500+ contributions.", "💻", 2),
            createAchievement("Certified Solutions Architect", "AWS", "March 2023",
                "AWS Certified Solutions Architect - Associate certification.", "📜", 3),
            createAchievement("Published Technical Writer", "Medium & Dev.to", "2021 - Present",
                "Published 50+ technical articles with 100K+ views.", "✍️", 4)
        );
        
        achievementRepository.saveAll(achievements);
        log.info("Achievements initialized: {}", achievements.size());
    }

    private Achievement createAchievement(String title, String organization, String date,
                                         String description, String icon, int order) {
        Achievement achievement = new Achievement();
        achievement.setTitle(title);
        achievement.setOrganization(organization);
        achievement.setDate(date);
        achievement.setDescription(description);
        achievement.setIcon(icon);
        achievement.setDisplayOrder(order);
        return achievement;
    }

    private void initializeExperiences() {
        List<Experience> experiences = Arrays.asList(
            createExperience("Senior Software Engineer", "Tech Company Inc.", "San Francisco, CA",
                "Jan 2022 - Present",
                Arrays.asList(
                    "Led development of microservices architecture serving 1M+ users",
                    "Mentored junior developers and conducted code reviews",
                    "Implemented CI/CD pipelines reducing deployment time by 60%"
                ), 1),
            createExperience("Software Engineer", "StartUp XYZ", "Remote",
                "Jun 2020 - Dec 2021",
                Arrays.asList(
                    "Built RESTful APIs and integrated third-party services",
                    "Developed responsive web applications using React",
                    "Collaborated with cross-functional teams in Agile environment"
                ), 2)
        );
        
        experienceRepository.saveAll(experiences);
        log.info("Experiences initialized: {}", experiences.size());
    }

    private Experience createExperience(String title, String company, String location,
                                       String duration, List<String> descriptions, int order) {
        Experience experience = new Experience();
        experience.setTitle(title);
        experience.setCompany(company);
        experience.setLocation(location);
        experience.setDuration(duration);
        experience.setDescription(descriptions);
        experience.setDisplayOrder(order);
        return experience;
    }

    private void initializeEducation() {
        Education education = new Education();
        education.setDegree("Bachelor of Science in Computer Science");
        education.setInstitution("University Name");
        education.setLocation("City, Country");
        education.setDuration("2016 - 2020");
        education.setGpa("3.8/4.0");
        education.setDisplayOrder(1);
        
        educationRepository.save(education);
        log.info("Education initialized");
    }

    private void initializeBlogs() {
        Blog blog = new Blog();
        blog.setTitle("Getting Started with React");
        blog.setSlug("getting-started-with-react");
        blog.setExcerpt("Learn the basics of React and start building modern web applications.");
        blog.setContent("""
        Getting Started with React

        React is a powerful JavaScript library for building user interfaces. In this tutorial, we'll explore the fundamentals of React and build our first component.


        ## What is React?

        React is a declarative, efficient, and flexible JavaScript library for building user interfaces. It lets you compose complex UIs from small and isolated pieces of code called components.


        ## Creating Your First Component

        ```javascript
        function Welcome() {
          return <h1>Hello, React!</h1>;
        }
        ```
        
        This is a simple functional component that renders a heading.


        ## Conclusion

        React makes it painless to create interactive UIs. Start learning React today!
        """);
        blog.setAuthor("Your Name");
        blog.setDate(LocalDateTime.now());
        blog.setTags(Arrays.asList("React", "JavaScript", "Web Development"));
        blog.setReadTime("5 min read");
        blog.setPublished(true);
        
        blogRepository.save(blog);
        log.info("Sample blog initialized");
    }
}
