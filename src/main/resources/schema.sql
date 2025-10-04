-- Portfolio Database Schema for PostgreSQL
-- This file is optional - Hibernate will auto-create tables
-- Use this for manual database setup or production deployments

-- Create database (run manually outside application)
-- CREATE DATABASE portfoliodb;

-- Personal Info Table
CREATE TABLE IF NOT EXISTS personal_info (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    bio TEXT,
    email VARCHAR(255),
    phone VARCHAR(50),
    location VARCHAR(255),
    github_url VARCHAR(500),
    linkedin_url VARCHAR(500),
    twitter_url VARCHAR(500)
);

-- Skills Table
CREATE TABLE IF NOT EXISTS skills (
    id BIGSERIAL PRIMARY KEY,
    category VARCHAR(255) NOT NULL,
    display_order INTEGER
);

CREATE TABLE IF NOT EXISTS skill_items (
    skill_id BIGINT NOT NULL,
    item VARCHAR(255),
    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE
);

-- Experience Table
CREATE TABLE IF NOT EXISTS experiences (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    company VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    duration VARCHAR(255) NOT NULL,
    display_order INTEGER
);

CREATE TABLE IF NOT EXISTS experience_descriptions (
    experience_id BIGINT NOT NULL,
    description TEXT,
    FOREIGN KEY (experience_id) REFERENCES experiences(id) ON DELETE CASCADE
);

-- Education Table
CREATE TABLE IF NOT EXISTS education (
    id BIGSERIAL PRIMARY KEY,
    degree VARCHAR(255) NOT NULL,
    institution VARCHAR(255) NOT NULL,
    year VARCHAR(50) NOT NULL,
    description TEXT,
    display_order INTEGER
);

-- Projects Table
CREATE TABLE IF NOT EXISTS projects (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    image VARCHAR(500),
    github_url VARCHAR(500),
    demo_url VARCHAR(500),
    featured BOOLEAN NOT NULL DEFAULT FALSE,
    display_order INTEGER
);

CREATE TABLE IF NOT EXISTS project_tags (
    project_id BIGINT NOT NULL,
    tag VARCHAR(255),
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

-- Achievements Table
CREATE TABLE IF NOT EXISTS achievements (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    date VARCHAR(100),
    category VARCHAR(100),
    display_order INTEGER
);

-- Blogs Table
CREATE TABLE IF NOT EXISTS blogs (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    excerpt TEXT,
    content TEXT NOT NULL,
    author VARCHAR(255) NOT NULL,
    date TIMESTAMP NOT NULL,
    read_time VARCHAR(50),
    published BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS blog_tags (
    blog_id BIGINT NOT NULL,
    tag VARCHAR(255),
    FOREIGN KEY (blog_id) REFERENCES blogs(id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_blogs_published ON blogs(published);
CREATE INDEX IF NOT EXISTS idx_blogs_date ON blogs(date);
CREATE INDEX IF NOT EXISTS idx_blogs_slug ON blogs(slug);
CREATE INDEX IF NOT EXISTS idx_projects_featured ON projects(featured);
CREATE INDEX IF NOT EXISTS idx_blog_tags_blog_id ON blog_tags(blog_id);
CREATE INDEX IF NOT EXISTS idx_project_tags_project_id ON project_tags(project_id);
