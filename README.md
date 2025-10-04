# Portfolio Backend API

A robust Spring Boot REST API for the Portfolio Website with H2 in-memory database.

## Architecture

This backend follows **industry-standard design patterns** for scalability and maintainability:

### Design Patterns Used
- **Repository Pattern**: Data access abstraction through JPA repositories
- **Service Layer Pattern**: Business logic separation from controllers
- **DTO Pattern**: Data Transfer Objects for API responses
- **Dependency Injection**: Spring's IoC container for loose coupling
- **RESTful API Design**: Standard HTTP methods and resource-based endpoints

### Project Structure

```
portfolio-backend/
├── src/main/java/com/portfolio/backend/
│   ├── config/              # Configuration classes
│   │   ├── CorsConfig.java          # CORS configuration
│   │   └── ModelMapperConfig.java   # DTO mapping configuration
│   ├── controller/          # REST Controllers (Presentation Layer)
│   │   ├── BlogController.java
│   │   ├── PersonalInfoController.java
│   │   ├── ProjectController.java
│   │   ├── AchievementController.java
│   │   ├── SkillController.java
│   │   ├── ExperienceController.java
│   │   └── EducationController.java
│   ├── dto/                 # Data Transfer Objects
│   │   ├── BlogDTO.java
│   │   └── ProjectDTO.java
│   ├── entity/              # JPA Entities (Domain Layer)
│   │   ├── Blog.java
│   │   ├── PersonalInfo.java
│   │   ├── Project.java
│   │   ├── Achievement.java
│   │   ├── Skill.java
│   │   ├── Experience.java
│   │   └── Education.java
│   ├── exception/           # Exception Handling
│   │   ├── ResourceNotFoundException.java
│   │   └── GlobalExceptionHandler.java
│   ├── initializer/         # Database Initialization
│   │   └── DataInitializer.java
│   ├── repository/          # Data Access Layer (JPA)
│   │   ├── BlogRepository.java
│   │   ├── PersonalInfoRepository.java
│   │   ├── ProjectRepository.java
│   │   ├── AchievementRepository.java
│   │   ├── SkillRepository.java
│   │   ├── ExperienceRepository.java
│   │   └── EducationRepository.java
│   ├── service/             # Business Logic Layer
│   │   ├── BlogService.java
│   │   ├── PersonalInfoService.java
│   │   ├── ProjectService.java
│   │   ├── AchievementService.java
│   │   ├── SkillService.java
│   │   ├── ExperienceService.java
│   │   └── EducationService.java
│   └── PortfolioBackendApplication.java
└── src/main/resources/
    └── application.properties

```

## Technology Stack

- **Java 17**
- **Spring Boot 3.1.5**
  - Spring Web (REST APIs)
  - Spring Data JPA (Database operations)
  - Spring Validation (Input validation)
- **H2 Database** (In-memory)
- **Lombok** (Reduce boilerplate code)
- **ModelMapper** (DTO conversions)
- **Maven** (Dependency management)

## API Endpoints

### Personal Information
- `GET /api/personal-info` - Get personal information
- `PUT /api/personal-info` - Update personal information

### Skills
- `GET /api/skills` - Get all skills

### Projects
- `GET /api/projects` - Get all projects
- `GET /api/projects?featured=true` - Get featured projects only
- `GET /api/projects/{id}` - Get project by ID
- `POST /api/projects` - Create new project
- `PUT /api/projects/{id}` - Update project
- `DELETE /api/projects/{id}` - Delete project

### Achievements
- `GET /api/achievements` - Get all achievements

### Experience
- `GET /api/experiences` - Get all work experiences

### Education
- `GET /api/education` - Get all education records

### Blogs
- `GET /api/blogs` - Get all blogs
- `GET /api/blogs?published=true` - Get published blogs only
- `GET /api/blogs/{id}` - Get blog by ID
- `GET /api/blogs/slug/{slug}` - Get blog by slug
- `GET /api/blogs/search?query={query}` - Search blogs
- `POST /api/blogs` - Create new blog post
- `PUT /api/blogs/{id}` - Update blog post
- `DELETE /api/blogs/{id}` - Delete blog post

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Build and Run

1. **Build the project:**
```bash
cd portfolio-backend
mvn clean install
```

2. **Run the application:**
```bash
mvn spring-boot:run
```

The API will start on `http://localhost:8080`

3. **Access H2 Console** (for development):
```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:portfoliodb
Username: sa
Password: (leave blank)
```

### Quick Test

Test the API endpoints:
```bash
# Get personal info
curl http://localhost:8080/api/personal-info

# Get all projects
curl http://localhost:8080/api/projects

# Get published blogs
curl http://localhost:8080/api/blogs?published=true
```

## Database Schema

The H2 database is automatically initialized with sample data on startup.

### Main Tables:
- `personal_info` - Personal information
- `blogs` - Blog posts with tags
- `projects` - Project portfolio
- `achievements` - Awards and certifications
- `skills` - Technical skills by category
- `experiences` - Work experience
- `education` - Educational background

## Features

### ✅ RESTful API Design
- Standard HTTP methods (GET, POST, PUT, DELETE)
- Proper status codes (200, 201, 204, 404, 500)
- Resource-based URLs

### ✅ Data Persistence
- JPA/Hibernate for ORM
- H2 in-memory database
- Automatic schema generation
- Sample data initialization

### ✅ Error Handling
- Global exception handler
- Custom exceptions
- Consistent error responses

### ✅ CORS Configuration
- Configured for frontend (localhost:5173)
- Supports all necessary HTTP methods

### ✅ Clean Architecture
- Separation of concerns
- Dependency injection
- Testable code structure

## Configuration

### Application Properties

Key configurations in `application.properties`:

```properties
server.port=8080                    # Server port
spring.datasource.url=jdbc:h2:mem:portfoliodb  # H2 database
spring.jpa.hibernate.ddl-auto=create-drop      # Schema management
spring.h2.console.enabled=true                  # H2 console access
```

### CORS Configuration

Configured to allow requests from:
- `http://localhost:5173` (Vite dev server)
- `http://localhost:3000` (Alternative port)

## Extending the Backend

### Adding New Entities

1. **Create Entity** in `entity/` package
2. **Create Repository** in `repository/` package
3. **Create Service** in `service/` package
4. **Create Controller** in `controller/` package
5. **Add initialization** in `DataInitializer.java`

### Example: Adding a "Testimonial" feature

```java
// 1. Entity
@Entity
public class Testimonial {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private String message;
    // getters/setters
}

// 2. Repository
public interface TestimonialRepository extends JpaRepository<Testimonial, Long> {}

// 3. Service
@Service
public class TestimonialService {
    private final TestimonialRepository repository;
    // business logic
}

// 4. Controller
@RestController
@RequestMapping("/api/testimonials")
public class TestimonialController {
    private final TestimonialService service;
    // REST endpoints
}
```

## Production Considerations

### Moving to Production Database

Replace H2 with PostgreSQL/MySQL:

1. **Add dependency** in `pom.xml`:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```

2. **Update application.properties**:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/portfolio_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### Security Enhancements

For production, consider adding:
- Spring Security for authentication
- JWT tokens for stateless auth
- Role-based access control (RBAC)
- HTTPS/SSL configuration
- API rate limiting

### Monitoring

Add actuator for health checks:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

## Testing

Run tests:
```bash
mvn test
```

## Building for Production

Create executable JAR:
```bash
mvn clean package
java -jar target/portfolio-backend-1.0.0.jar
```

## License

MIT License

---

Built with ☕ using Spring Boot and Java 17
