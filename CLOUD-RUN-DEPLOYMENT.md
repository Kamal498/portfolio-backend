# Deploy Spring Boot Backend to Cloud Run

## Overview

This guide will deploy your Spring Boot application to Google Cloud Run and connect it to your Cloud SQL PostgreSQL instance.

**Architecture:**
```
Frontend (Cloudflare Pages)
    ↓ HTTPS
Cloud Run (Spring Boot)
    ↓ Unix Socket
Cloud SQL PostgreSQL
```

---

## Prerequisites

✅ Cloud SQL PostgreSQL instance created (`portfolio-db`)  
✅ Database `portfoliodb` created  
✅ Database credentials available  
✅ Docker installed locally (optional, for testing)  
✅ gcloud CLI configured  

---

## Quick Deployment

### Run the Deployment Script

```bash
cd /Users/k0p0ghj/programs/personal/portfolio-backend
./deploy-to-cloud-run.sh
```

**You'll be prompted for:**
1. GCP Project ID
2. Database password
3. Frontend URL (for CORS)

The script will:
- Enable required APIs
- Build Docker image
- Store password in Secret Manager
- Deploy to Cloud Run
- Configure Cloud SQL connection

**Time:** ~5-10 minutes

---

## Manual Deployment Steps

### Step 1: Update Dependencies ✅

Already done! Your `pom.xml` now includes:
```xml
<dependency>
    <groupId>com.google.cloud.sql</groupId>
    <artifactId>postgres-socket-factory</artifactId>
    <version>1.15.0</version>
</dependency>
```

### Step 2: Configure Production Properties ✅

Your `application-prod.properties` is configured with:
```properties
spring.datasource.url=jdbc:postgresql:///${DATABASE_NAME}?cloudSqlInstance=${CLOUD_SQL_CONNECTION_NAME}&socketFactory=com.google.cloud.sql.postgres.SocketFactory
spring.datasource.username=${DATABASE_USERNAME:postgres}
spring.datasource.password=${DATABASE_PASSWORD}
```

### Step 3: Store Database Password

```bash
# Store password securely
echo -n "YOUR_DB_PASSWORD" | gcloud secrets create db-password --data-file=-

# Verify
gcloud secrets versions access latest --secret="db-password"
```

### Step 4: Get Cloud SQL Connection Name

```bash
# Get connection name (format: PROJECT:REGION:INSTANCE)
gcloud sql instances describe portfolio-db --format='value(connectionName)'

# Example output: my-project:us-central1:portfolio-db
```

### Step 5: Build Docker Image

```bash
# Build and push to GCR
gcloud builds submit --tag gcr.io/YOUR_PROJECT_ID/portfolio-backend

# Or build locally first (optional)
docker build -t portfolio-backend .
docker tag portfolio-backend gcr.io/YOUR_PROJECT_ID/portfolio-backend
docker push gcr.io/YOUR_PROJECT_ID/portfolio-backend
```

### Step 6: Deploy to Cloud Run

```bash
# Replace these variables
PROJECT_ID="your-project-id"
DB_CONNECTION_NAME="your-project:us-central1:portfolio-db"
FRONTEND_URL="https://your-site.pages.dev"

gcloud run deploy portfolio-backend \
  --image gcr.io/$PROJECT_ID/portfolio-backend \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --add-cloudsql-instances $DB_CONNECTION_NAME \
  --set-env-vars "SPRING_PROFILES_ACTIVE=prod" \
  --set-env-vars "CLOUD_SQL_CONNECTION_NAME=$DB_CONNECTION_NAME" \
  --set-env-vars "DATABASE_USERNAME=postgres" \
  --set-secrets "DATABASE_PASSWORD=db-password:latest" \
  --set-env-vars "ALLOWED_ORIGINS=$FRONTEND_URL" \
  --memory 1Gi \
  --cpu 1 \
  --min-instances 0 \
  --max-instances 5 \
  --port 8080
```

### Step 7: Get Service URL

```bash
gcloud run services describe portfolio-backend \
  --region us-central1 \
  --format='value(status.url)'

# Example output: https://portfolio-backend-xxxxx-uc.a.run.app
```

---

## Environment Variables Explained

| Variable | Purpose | Example |
|----------|---------|---------|
| `SPRING_PROFILES_ACTIVE` | Activates prod config | `prod` |
| `CLOUD_SQL_CONNECTION_NAME` | Cloud SQL instance identifier | `project:region:instance` |
| `DATABASE_USERNAME` | Database user | `postgres` |
| `DATABASE_PASSWORD` | From Secret Manager | `(secret)` |
| `ALLOWED_ORIGINS` | CORS allowed origins | `https://site.pages.dev` |

---

## Testing Deployment

### Test API Endpoints

```bash
# Get your service URL
SERVICE_URL=$(gcloud run services describe portfolio-backend --region us-central1 --format='value(status.url)')

# Test endpoints
curl $SERVICE_URL/api/personal-info
curl $SERVICE_URL/api/projects
curl $SERVICE_URL/api/skills
curl $SERVICE_URL/api/experience
curl $SERVICE_URL/api/education
curl $SERVICE_URL/api/blog
```

### View Logs

```bash
# Real-time logs
gcloud run services logs tail portfolio-backend --region us-central1

# Recent logs
gcloud run services logs read portfolio-backend --region us-central1 --limit=100
```

### Check Service Status

```bash
gcloud run services describe portfolio-backend --region us-central1
```

---

## Update Frontend

After successful deployment, update your frontend:

### Update .env.production

```bash
cd /Users/k0p0ghj/programs/personal/portfolio-website

# Get backend URL
BACKEND_URL=$(gcloud run services describe portfolio-backend --region us-central1 --format='value(status.url)')

# Update env file
echo "VITE_API_BASE_URL=$BACKEND_URL/api" > .env.production
```

### Redeploy Frontend

Push to GitHub (Cloudflare auto-deploys):
```bash
git add .env.production
git commit -m "Update production API URL"
git push origin main
```

---

## Common Issues & Solutions

### Issue: 502 Bad Gateway

**Cause:** App not listening on PORT or crashed  
**Solution:**
```bash
# Check logs
gcloud run services logs read portfolio-backend --region us-central1

# Verify PORT is 8080 in application
# Check if app started successfully
```

### Issue: Database Connection Failed

**Cause:** Cloud SQL not connected or wrong credentials  
**Solution:**
```bash
# Verify Cloud SQL connection
gcloud run services describe portfolio-backend --region us-central1 --format='value(spec.template.spec.containers[0].cloudSqlInstances)'

# Update connection
gcloud run services update portfolio-backend \
  --region us-central1 \
  --add-cloudsql-instances YOUR_CONNECTION_NAME
```

### Issue: CORS Error

**Cause:** Frontend origin not allowed  
**Solution:**
```bash
# Update CORS origins
gcloud run services update portfolio-backend \
  --region us-central1 \
  --update-env-vars "ALLOWED_ORIGINS=https://your-site.pages.dev,https://other-domain.com"
```

### Issue: Secret Not Found

**Cause:** db-password secret not created  
**Solution:**
```bash
# Create secret
echo -n "YOUR_PASSWORD" | gcloud secrets create db-password --data-file=-

# Grant access to Cloud Run
gcloud secrets add-iam-policy-binding db-password \
  --member="serviceAccount:YOUR_PROJECT_NUMBER-compute@developer.gserviceaccount.com" \
  --role="roles/secretmanager.secretAccessor"
```

---

## Redeploy / Update

### Update Code and Redeploy

```bash
cd /Users/k0p0ghj/programs/personal/portfolio-backend

# Make your code changes
git add .
git commit -m "Update backend"
git push

# Rebuild and redeploy
gcloud builds submit --tag gcr.io/YOUR_PROJECT_ID/portfolio-backend
gcloud run deploy portfolio-backend \
  --image gcr.io/YOUR_PROJECT_ID/portfolio-backend \
  --region us-central1
```

### Update Environment Variables

```bash
gcloud run services update portfolio-backend \
  --region us-central1 \
  --update-env-vars "NEW_VAR=value"
```

### Update Secrets

```bash
echo -n "NEW_PASSWORD" | gcloud secrets versions add db-password --data-file=-
```

---

## Monitoring & Costs

### View Metrics

- Go to: https://console.cloud.google.com/run
- Click on `portfolio-backend`
- View: Metrics, Logs, Revisions

### Cost Estimate

**Cloud Run Free Tier:**
- 2 million requests/month
- 360,000 GB-seconds
- 180,000 vCPU-seconds

**Expected Cost:** $0-5/month for typical portfolio traffic

### Set Budget Alert

```bash
# Via console: https://console.cloud.google.com/billing/budgets
# Set alert at $5/month
```

---

## Rollback

If deployment fails, rollback to previous version:

```bash
# List revisions
gcloud run revisions list --service portfolio-backend --region us-central1

# Rollback to specific revision
gcloud run services update-traffic portfolio-backend \
  --region us-central1 \
  --to-revisions REVISION_NAME=100
```

---

## Security Best Practices

✅ Password stored in Secret Manager (not in code)  
✅ CORS restricted to frontend domain  
✅ Database accessed via private Unix socket  
✅ HTTPS enforced automatically by Cloud Run  
✅ No public database access  

---

## Next Steps

After successful deployment:

1. ✅ Test all API endpoints
2. ✅ Update frontend with new backend URL
3. ✅ Test end-to-end (frontend → backend → database)
4. ⬜ Set up custom domain (optional)
5. ⬜ Configure CI/CD pipeline (optional)
6. ⬜ Set up monitoring alerts

---

## Useful Commands

```bash
# View service details
gcloud run services describe portfolio-backend --region us-central1

# View real-time logs
gcloud run services logs tail portfolio-backend --region us-central1

# List all services
gcloud run services list

# Delete service
gcloud run services delete portfolio-backend --region us-central1

# Update service memory/cpu
gcloud run services update portfolio-backend \
  --region us-central1 \
  --memory 2Gi \
  --cpu 2
```

---

## Support

- Cloud Run Docs: https://cloud.google.com/run/docs
- Cloud SQL Docs: https://cloud.google.com/sql/docs
- Troubleshooting: Check logs first!

---

**Ready to deploy? Run:**
```bash
./deploy-to-cloud-run.sh
```
