# Environment Configuration Guide

## Spring Profile Management

The application uses the `SPRING_PROFILES_ACTIVE` environment variable to determine which profile to load.

### Available Profiles

- **dev** (default) - Development environment with local PostgreSQL
- **prod** - Production environment for Cloud Run deployment

---

## Local Development

### Option 1: No Configuration (Default)
```bash
# Runs with 'dev' profile by default
./mvnw spring-boot:run
```

### Option 2: Explicitly Set Environment Variable

**macOS/Linux:**
```bash
# Set for current session
export SPRING_PROFILES_ACTIVE=dev
./mvnw spring-boot:run

# Or inline
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

**Windows (PowerShell):**
```powershell
$env:SPRING_PROFILES_ACTIVE="dev"
./mvnw spring-boot:run
```

**Windows (CMD):**
```cmd
set SPRING_PROFILES_ACTIVE=dev
mvnw spring-boot:run
```

### Option 3: IDE Configuration

**IntelliJ IDEA:**
1. Run → Edit Configurations
2. Select your Spring Boot configuration
3. Environment variables: `SPRING_PROFILES_ACTIVE=dev`
4. Apply

**VS Code:**
Add to `.vscode/launch.json`:
```json
{
  "configurations": [
    {
      "type": "java",
      "name": "Spring Boot App",
      "request": "launch",
      "mainClass": "com.portfolio.backend.BackendApplication",
      "env": {
        "SPRING_PROFILES_ACTIVE": "dev"
      }
    }
  ]
}
```

### Option 4: .env File (with direnv)

Create `.env` file:
```bash
SPRING_PROFILES_ACTIVE=dev
```

Install direnv:
```bash
# macOS
brew install direnv

# Add to shell config
echo 'eval "$(direnv hook bash)"' >> ~/.bashrc
```

---

## Production Deployment (Cloud Run)

The profile is automatically set to `prod` during Cloud Run deployment:

```bash
gcloud run deploy portfolio-backend \
  --set-env-vars "SPRING_PROFILES_ACTIVE=prod" \
  ...
```

This is already configured in `deploy-cloud-run.sh`.

---

## Verify Active Profile

Check application logs on startup:

```
The following 1 profile is active: "dev"
```

Or in production:
```
The following 1 profile is active: "prod"
```

---

## Profile-Specific Configuration Files

- `application.properties` - Base configuration + default profile
- `application-dev.properties` - Development overrides (optional)
- `application-prod.properties` - Production overrides

Configuration loading order:
1. application.properties (base)
2. application-{profile}.properties (overrides)
3. Environment variables (highest priority)

---

## Testing Different Profiles Locally

### Test with dev profile (default):
```bash
./mvnw spring-boot:run
# Uses local PostgreSQL
```

### Test with prod profile (use with caution):
```bash
# Set production database credentials first
export DATABASE_URL="jdbc:postgresql://..."
export DATABASE_USERNAME="portfolio_user"
export DATABASE_PASSWORD="your-password"
export SPRING_PROFILES_ACTIVE=prod

./mvnw spring-boot:run
# Uses production database - BE CAREFUL!
```

---

## Common Issues

### Issue: Application uses wrong profile
**Solution:** Check environment variable is set correctly
```bash
echo $SPRING_PROFILES_ACTIVE
```

### Issue: Profile not found
**Solution:** Ensure profile-specific properties file exists
```bash
ls src/main/resources/application-*.properties
```

### Issue: Can't connect to database in prod profile
**Solution:** Verify all required environment variables are set:
- `DATABASE_URL`
- `DATABASE_USERNAME`
- `DATABASE_PASSWORD`
- `ALLOWED_ORIGINS` (for CORS)
