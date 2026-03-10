# Configurable Data Source Implementation

## Overview

The portfolio backend now supports **configurable data sources**, allowing you to switch between:
- **DATABASE**: PostgreSQL database (default)
- **FILE**: JSON file-based data storage

This is ideal for production environments where you want to serve static portfolio data without database overhead.

## Architecture

### Strategy Pattern with Spring Conditional Beans

```
┌─────────────┐
│  Services   │
└──────┬──────┘
       │
       ├─────────────────────────┐
       │                         │
┌──────▼──────────┐    ┌────────▼────────┐
│  DataProvider   │    │  DataProvider   │
│   (Interface)   │    │   (Interface)   │
└────────┬────────┘    └────────┬────────┘
         │                      │
    ┌────┴────┐            ┌────┴────┐
    │         │            │         │
┌───▼───┐ ┌──▼────┐   ┌───▼───┐ ┌──▼────┐
│  DB   │ │ File  │   │  DB   │ │ File  │
│Provider│ │Provider│  │Provider│ │Provider│
└───────┘ └───────┘   └───────┘ └───────┘
```

### Key Components

1. **DataProvider Interfaces** (`com.portfolio.backend.provider.*`)
   - `ProjectDataProvider`
   - `ExperienceDataProvider`
   - `SkillDataProvider`
   - `EducationDataProvider`
   - `AchievementDataProvider`
   - `BlogDataProvider`
   - `PersonalInfoDataProvider`

2. **Database Implementations** (`com.portfolio.backend.provider.database.*`)
   - Wrap existing JPA repositories
   - Active when `portfolio.datasource.type=DATABASE`

3. **File Implementations** (`com.portfolio.backend.provider.file.*`)
   - Read from JSON file
   - Active when `portfolio.datasource.type=FILE`

4. **Configuration** (`DataSourceProperties`)
   - `portfolio.datasource.type`: DATABASE or FILE
   - `portfolio.datasource.file-path`: Path to JSON file

## Usage

### Switch to File-Based Data Source

**Step 1: Update `application-prod.properties`**

```properties
# Change from DATABASE to FILE
portfolio.datasource.type=FILE
portfolio.datasource.file-path=classpath:portfolio-data.json
```

**Step 2: Prepare your JSON data file**

Place `portfolio-data.json` in `src/main/resources/` with your portfolio data:

```json
{
  "personalInfo": { ... },
  "projects": [ ... ],
  "experiences": [ ... ],
  "skills": [ ... ],
  "education": [ ... ],
  "achievements": [ ... ],
  "blogs": [ ... ]
}
```

**Step 3: Update Cloud Run deployment**

In `deploy-to-cloud-run.sh`, add/update the environment variable:

```bash
gcloud run deploy portfolio-backend \
  --source . \
  --region us-central1 \
  --allow-unauthenticated \
  --set-env-vars "SPRING_PROFILES_ACTIVE=prod" \
  --set-env-vars "PORTFOLIO_DATASOURCE_TYPE=FILE" \
  --set-env-vars "ALLOWED_ORIGINS=$FRONTEND_URL" \
  --memory 512Mi \
  --cpu 1 \
  --timeout 300
```

### Switch Back to Database

```properties
portfolio.datasource.type=DATABASE
```

Or via environment variable:
```bash
export PORTFOLIO_DATASOURCE_TYPE=DATABASE
```

## Benefits

### File-Based Data Source (PROD)
- ✅ **Zero database cost** - No Cloud SQL charges
- ✅ **Instant cold starts** - No database connection overhead
- ✅ **Simpler deployment** - No secrets, connection strings
- ✅ **Version controlled** - Data in Git
- ✅ **Lower memory usage** - ~256MB vs 512MB+
- ⚠️ **Read-only** - No runtime updates (perfect for portfolios)

### Database (Default)
- ✅ **Dynamic updates** - Modify data via API
- ✅ **Rich querying** - Complex searches and filters
- ✅ **Transactional** - ACID guarantees
- ⚠️ **Higher cost** - Cloud SQL charges
- ⚠️ **Connection overhead** - Cold start latency

## Production Deployment

### Option 1: File-Based (Recommended for Static Portfolios)

```bash
# 1. Update application-prod.properties
portfolio.datasource.type=FILE

# 2. Deploy (no database needed)
gcloud run deploy portfolio-backend \
  --source . \
  --region us-central1 \
  --allow-unauthenticated \
  --set-env-vars "SPRING_PROFILES_ACTIVE=prod" \
  --set-env-vars "PORTFOLIO_DATASOURCE_TYPE=FILE" \
  --set-env-vars "ALLOWED_ORIGINS=https://your-site.pages.dev" \
  --memory 256Mi \
  --cpu 1
```

### Option 2: Database (For Dynamic Content)

```bash
# Keep existing deployment script
./deploy-to-cloud-run.sh
```

## JSON File Format

See `src/main/resources/portfolio-data.json` for the complete structure. Key fields:

```json
{
  "personalInfo": {
    "id": 1,
    "name": "Your Name",
    "title": "Your Title",
    "email": "your.email@example.com",
    ...
  },
  "projects": [
    {
      "id": 1,
      "title": "Project Name",
      "description": "Description",
      "tags": ["tag1", "tag2"],
      "featured": true,
      "displayOrder": 1,
      ...
    }
  ],
  ...
}
```

## Environment Variables

| Variable | Values | Default | Description |
|----------|--------|---------|-------------|
| `PORTFOLIO_DATASOURCE_TYPE` | `DATABASE`, `FILE` | `DATABASE` | Data source type |
| `PORTFOLIO_DATASOURCE_FILE_PATH` | Path string | `classpath:portfolio-data.json` | Path to JSON file |

## Cost Comparison

### Database Setup (Current)
- Cloud SQL: ~$10-50/month
- Cloud Run: ~$5/month
- **Total: ~$15-55/month**

### File-Based Setup
- Cloud Run only: ~$0-5/month (free tier eligible)
- **Total: ~$0-5/month** 💰

## Implementation Details

### Zero Service Changes
Services remain unchanged - they depend on `DataProvider` interfaces:

```java
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectDataProvider projectDataProvider; // Interface
    // ... no changes needed
}
```

### Conditional Bean Loading
Spring automatically selects the correct implementation:

```java
@Component
@ConditionalOnProperty(name = "portfolio.datasource.type", havingValue = "FILE")
public class FileProjectDataProvider implements ProjectDataProvider { ... }

@Component
@ConditionalOnProperty(name = "portfolio.datasource.type", havingValue = "DATABASE", matchIfMissing = true)
public class DatabaseProjectDataProvider implements ProjectDataProvider { ... }
```

### Compile-Time Safety
- Type-safe interfaces
- No runtime reflection
- IDE autocomplete support
- Compile-time error detection

## Migration Guide

### Export Database to JSON

Use this script to export your current database data:

```bash
# TODO: Create export script if needed
# curl http://localhost:8080/api/export > portfolio-data.json
```

### Update Deployment

1. Commit `portfolio-data.json` to your repository
2. Update `application-prod.properties` or set environment variable
3. Remove database-related environment variables from deployment
4. Reduce memory allocation to 256Mi
5. Deploy

## Troubleshooting

### Bean creation errors
- Ensure `portfolio.datasource.type` is set to `FILE` or `DATABASE`
- Check JSON file path is correct

### JSON parsing errors
- Validate JSON syntax using `jsonlint`
- Check all required fields are present
- Ensure date format is ISO-8601: `2024-01-15T10:00:00`

### Data not loading
- Check logs for FileDataLoader initialization
- Verify JSON file is in classpath
- Ensure proper Spring Boot property format

## Future Enhancements

- [ ] Add JSON validation on startup
- [ ] Support external file URLs (S3, GCS)
- [ ] Add data export API endpoint
- [ ] Support hot-reload for file changes
- [ ] Add caching layer for file-based provider

---

**Implementation Date**: 2024  
**Version**: 1.0  
**Status**: ✅ Production Ready
