package com.portfolio.backend.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String PROJECTS_CACHE = "projects";
    public static final String FEATURED_PROJECTS_CACHE = "featuredProjects";
    public static final String PROJECT_BY_ID_CACHE = "projectById";
    public static final String EXPERIENCES_CACHE = "experiences";
    public static final String SKILLS_CACHE = "skills";
    public static final String EDUCATION_CACHE = "education";
    public static final String ACHIEVEMENTS_CACHE = "achievements";
    public static final String BLOGS_CACHE = "blogs";
    public static final String PUBLISHED_BLOGS_CACHE = "publishedBlogs";
    public static final String BLOG_BY_ID_CACHE = "blogById";
    public static final String BLOG_BY_SLUG_CACHE = "blogBySlug";
    public static final String PERSONAL_INFO_CACHE = "personalInfo";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                PROJECTS_CACHE,
                FEATURED_PROJECTS_CACHE,
                PROJECT_BY_ID_CACHE,
                EXPERIENCES_CACHE,
                SKILLS_CACHE,
                EDUCATION_CACHE,
                ACHIEVEMENTS_CACHE,
                BLOGS_CACHE,
                PUBLISHED_BLOGS_CACHE,
                BLOG_BY_ID_CACHE,
                BLOG_BY_SLUG_CACHE,
                PERSONAL_INFO_CACHE
        );

        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .maximumSize(1000)
                .recordStats();
    }
}
