# PostgreSQL Migration Guide

## ✅ Migration Completed

Your portfolio backend has been successfully migrated from H2 (in-memory) to PostgreSQL (persistent database).

## 🔧 Changes Made

### 1. **Dependencies** (`pom.xml`)
- ❌ Removed: `com.h2database:h2`
- ✅ Added: `org.postgresql:postgresql`

### 2. **Configuration Files**
- ✅ Updated: `application.properties` - PostgreSQL settings
- ✅ Created: `application-dev.properties` - Development environment
- ✅ Created: `application-prod.properties` - Production environment
- ✅ Created: `docker-compose.yml` - Easy PostgreSQL setup
- ✅ Created: `schema.sql` - Database schema (optional)

### 3. **Database Settings**
| Setting | H2 (Old) | PostgreSQL (New) |
|---------|----------|------------------|
| URL | `jdbc:h2:mem:portfoliodb` | `jdbc:postgresql://localhost:5432/portfoliodb` |
| Username | `sa` | `postgres` |
| Password | (empty) | `postgres` |
| Dialect | `H2Dialect` | `PostgreSQLDialect` |
| DDL | `create-drop` | `update` |
| Persistence | ❌ In-memory | ✅ Persistent |

## 🚀 Setup Instructions

### Option 1: Using Docker (Recommended)

**Start PostgreSQL:**
```bash
cd /Users/k0p0ghj/programs/personal/portfolio-backend
docker-compose up -d
```

**Verify it's running:**
```bash
docker ps
# Should see: portfolio-postgres
```

**Stop PostgreSQL:**
```bash
docker-compose down
# To remove data: docker-compose down -v
```

### Option 2: Install PostgreSQL Locally

**macOS (using Homebrew):**
```bash
brew install postgresql@15
brew services start postgresql@15
```

**Create database:**
```bash
# Add to your PATH
export PATH="/opt/homebrew/opt/postgresql@15/bin:$PATH"
createdb portfoliodb
```

**Access PostgreSQL:**
```bash
psql -d portfoliodb
psql -U postgres -d portfoliodb
```

### Option 3: Use Existing PostgreSQL

Update `application.properties` with your database credentials:
```properties
spring.datasource.url=jdbc:postgresql://your-host:5432/your-database
spring.datasource.username=your-username
spring.datasource.password=your-password
```

## 🏃 Running the Application

### 1. Start PostgreSQL
```bash
docker-compose up -d
```

### 2. Run Spring Boot Application
```bash
./mvnw clean install
./mvnw spring-boot:run
```

### 3. Verify Connection
Check the logs for:
```
Hibernate: 
    create table if not exists blogs ...
```

Application should start on: `http://localhost:8080`

## 📊 Verify Data Persistence

### Test 1: Create Data
```bash
# Create a blog post via API
curl -X POST http://localhost:8080/api/blogs \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Post",
    "excerpt": "Test excerpt",
    "content": "Test content",
    "author": "Admin",
    "tags": ["test"],
    "readTime": "5 min read"
  }'
```

### Test 2: Restart Application
```bash
# Stop application (Ctrl+C)
# Restart it
./mvnw spring-boot:run
```

### Test 3: Verify Data Persists
```bash
# Fetch blogs - should show previously created data
curl http://localhost:8080/api/blogs
```

✅ **Success!** Data persists after restart (unlike H2 in-memory)

## 🗄️ Database Access

### Using psql CLI:
```bash
# Connect to database
docker exec -it portfolio-postgres psql -U postgres -d portfoliodb

# List tables
\dt

# Query data
SELECT * FROM blogs;
SELECT * FROM projects;

# Exit
\q
```

### Using GUI Tool (pgAdmin, DBeaver, etc.):
```
Host: localhost
Port: 5432
Database: portfoliodb
Username: postgres
Password: postgres
```

## 🔒 Environment-Specific Configuration

### Development (default)
- Profile: `dev`
- DDL: `update` (auto-creates/updates tables)
- Logging: Detailed SQL logs
- CORS: Allows localhost

### Production
- Profile: `prod`
- DDL: `validate` (requires pre-created schema)
- Logging: Minimal
- CORS: Restricted to allowed origins

**Run with production profile:**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

Or set environment variable:
```bash
export SPRING_PROFILES_ACTIVE=prod
./mvnw spring-boot:run
```

## 📝 Database Schema

Schema is automatically created by Hibernate. For manual setup, use:
```bash
psql -U postgres -d portfoliodb -f src/main/resources/schema.sql
```

## 🐛 Troubleshooting

### Issue 1: Connection refused
```
org.postgresql.util.PSQLException: Connection refused
```
**Solution:** Ensure PostgreSQL is running
```bash
docker-compose ps
# or
brew services list | grep postgresql
```

### Issue 2: Database does not exist
```
PSQLException: database "portfoliodb" does not exist
```
**Solution:** Create database
```bash
docker exec -it portfolio-postgres psql -U postgres -c "CREATE DATABASE portfoliodb;"
```

### Issue 3: Authentication failed
```
PSQLException: password authentication failed
```
**Solution:** Check credentials in `application.properties`

### Issue 4: Port already in use
```
Bind for 0.0.0.0:5432 failed: port is already allocated
```
**Solution:** Change port in `docker-compose.yml` or stop existing PostgreSQL

## ⚡ Performance Benefits

### PostgreSQL vs H2:
| Feature | H2 | PostgreSQL |
|---------|----|-----------:|
| Data Persistence | ❌ Lost on restart | ✅ Persistent |
| Production Ready | ❌ Development only | ✅ Yes |
| Concurrent Users | Limited | Excellent |
| Data Integrity | Basic | Advanced |
| Backup/Restore | Manual | Built-in |
| Scalability | Limited | Excellent |

## 🔄 Rollback to H2 (if needed)

If you need to revert:

1. Restore old `pom.xml` dependency:
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

2. Restore old `application.properties`:
```properties
spring.datasource.url=jdbc:h2:mem:portfoliodb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

## 🎯 Next Steps

1. ✅ **Configure backup strategy**
   ```bash
   # Backup database
   docker exec portfolio-postgres pg_dump -U postgres portfoliodb > backup.sql
   
   # Restore database
   docker exec -i portfolio-postgres psql -U postgres portfoliodb < backup.sql
   ```

2. ✅ **Set up production database** (when deploying)
   - Use managed PostgreSQL (AWS RDS, Google Cloud SQL, etc.)
   - Update `application-prod.properties` with production credentials
   - Use environment variables for sensitive data

3. ✅ **Monitor performance**
   - Enable connection pool monitoring
   - Set up database performance metrics
   - Configure slow query logging

## 📚 Resources

- PostgreSQL Documentation: https://www.postgresql.org/docs/
- Spring Data JPA: https://spring.io/projects/spring-data-jpa
- Docker Compose: https://docs.docker.com/compose/
- pgAdmin: https://www.pgadmin.org/

---

**Migration Status:** ✅ Complete
**Data Persistence:** ✅ Enabled
**Production Ready:** ✅ Yes

Your backend now has full data persistence with PostgreSQL! 🎉
