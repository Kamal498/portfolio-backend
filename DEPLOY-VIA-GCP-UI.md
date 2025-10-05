# Deploy Spring Boot Backend to Cloud Run via GCP UI

## Overview

This guide walks you through deploying your Spring Boot application to Cloud Run using the Google Cloud Console web interface.

---

## Prerequisites

Before starting:
- ✅ Cloud SQL instance created (`portfolio-db`)
- ✅ Database `portfoliodb` exists
- ✅ Backend code pushed to GitHub
- ✅ Database password available

---

## Step 1: Prepare Secret for Database Password

### 1.1 Go to Secret Manager

1. Open: https://console.cloud.google.com/security/secret-manager
2. Click **"Create Secret"**

### 1.2 Create Secret

**Configuration:**
- **Name:** `db-password`
- **Secret value:** Your database password
- **Regions:** Automatic
- Click **"Create Secret"**

---

## Step 2: Deploy to Cloud Run

### 2.1 Open Cloud Run Console

Go to: https://console.cloud.google.com/run

### 2.2 Create Service

Click **"Create Service"**

### 2.3 Configure Container

**Source:**
- Select: **"Continuously deploy from a repository (source or function)"**
- Click **"Set up with Cloud Build"**

**Repository Connection:**
1. Click **"Connect repository"**
2. Choose: **GitHub**
3. Authenticate and select repository: `Kamal498/portfolio-backend`
4. Branch: `main`
5. Build type: **Dockerfile**
6. Dockerfile path: `/Dockerfile`
7. Click **"Save"**

### 2.4 Service Settings

**Service name:** `portfolio-backend`

**Region:** `us-central1 (Iowa)`

**Authentication:** 
- Select: **"Allow unauthenticated invocations"**

### 2.5 Container Settings

Click **"Container, Networking, Security"** to expand

**Container Tab:**

**General:**
- Container port: `8080`
- Memory: `1 GiB`
- CPU: `1`
- Request timeout: `300` seconds

**Environment Variables:**
Click **"Variables & Secrets"** → **"Add Variable"**

Add these variables:

| Name | Value |
|------|-------|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `CLOUD_SQL_CONNECTION_NAME` | `YOUR_PROJECT:us-central1:portfolio-db` | 
| `DATABASE_USERNAME` | `postgres` |
| `ALLOWED_ORIGINS` | `https://your-frontend.pages.dev` |

**Get CLOUD_SQL_CONNECTION_NAME:**
1. Go to: https://console.cloud.google.com/sql/instances
2. Click `portfolio-db`
3. Copy **"Connection name"** (format: `project:region:instance`)

**Secrets:**
Click **"Reference a secret"**
- Secret: `db-password`
- Reference method: **"Exposed as environment variable"**
- Environment variable name: `DATABASE_PASSWORD`
- Version: **"Latest"**

### 2.6 Connections Tab

**Cloud SQL connections:**
1. Click **"Add Connection"**
2. Select: `portfolio-db`
3. This enables Unix socket connection

### 2.7 Networking Tab (Optional)

**Ingress:**
- Select: **"All"** (allows public access)

**Egress:**
- Select: **"All traffic"** (default)

### 2.8 Security Tab

Leave defaults

### 2.9 Autoscaling

**Minimum instances:** `0` (scales to zero to save costs)

**Maximum instances:** `5`

---

## Step 3: Deploy

Click **"Create"** at the bottom

**Build Process:**
- Cloud Build will:
  1. Clone your GitHub repository
  2. Build Docker image using Dockerfile
  3. Push to Container Registry
  4. Deploy to Cloud Run
- **Time:** 5-10 minutes
- You can watch progress in the UI

---

## Step 4: Get Service URL

After deployment completes:

1. You'll be redirected to service details
2. Copy the **Service URL** (looks like: `https://portfolio-backend-xxxxx-uc.a.run.app`)
3. Save this URL - you'll need it for frontend configuration

---

## Step 5: Test Deployment

### 5.1 Test in Browser

Open these URLs in your browser:

```
https://YOUR-SERVICE-URL/api/personal-info
https://YOUR-SERVICE-URL/api/projects
https://YOUR-SERVICE-URL/api/skills
```

You should see JSON responses.

### 5.2 View Logs

1. In Cloud Run console, click your service
2. Go to **"Logs"** tab
3. Check for startup logs and errors

---

## Step 6: Update Frontend

### 6.1 Update Environment Variable

1. Go to your frontend code
2. Edit `.env.production`:
   ```
   VITE_API_BASE_URL=https://YOUR-SERVICE-URL/api
   ```

### 6.2 Deploy Frontend

**If using Cloudflare Pages:**
1. Go to: https://dash.cloudflare.com
2. Navigate to your Pages project
3. Go to **Settings** → **Environment variables**
4. Update `VITE_API_BASE_URL` with your new backend URL
5. Trigger a new deployment (or push to GitHub)

**Or push to GitHub:**
```bash
cd /Users/k0p0ghj/programs/personal/portfolio-website
git add .env.production
git commit -m "Update production API URL"
git push origin main
```

### 6.3 Update CORS in Backend

If you change frontend URL later:

1. Go to: https://console.cloud.google.com/run
2. Click `portfolio-backend`
3. Click **"Edit & Deploy New Revision"**
4. Go to **"Variables & Secrets"**
5. Update `ALLOWED_ORIGINS` with new frontend URL
6. Click **"Deploy"**

---

## Troubleshooting via UI

### View Logs

1. Cloud Run console → Your service
2. **"Logs"** tab
3. Filter by severity if needed

### Check Metrics

1. Cloud Run console → Your service
2. **"Metrics"** tab
3. View: Request count, Latency, Memory usage

### Check Cloud SQL Connection

1. Cloud Run console → Your service
2. **"Connections"** tab
3. Verify `portfolio-db` is listed

### Check Environment Variables

1. Cloud Run console → Your service
2. **"Configuration"** tab
3. Verify all environment variables are set

### Common Issues

**502 Bad Gateway:**
- Check logs for application errors
- Verify app is listening on port 8080
- Check Cloud SQL connection

**Database Connection Failed:**
- Verify Cloud SQL connection is added
- Check database password secret
- Verify `CLOUD_SQL_CONNECTION_NAME` is correct

**CORS Error:**
- Update `ALLOWED_ORIGINS` environment variable
- Redeploy service

---

## Redeploy / Update

### Automatic Redeployment

If you set up continuous deployment from GitHub:
1. Push code changes to GitHub
2. Cloud Build automatically rebuilds
3. New revision deployed automatically

### Manual Redeployment

1. Go to: https://console.cloud.google.com/run
2. Click `portfolio-backend`
3. Click **"Edit & Deploy New Revision"**
4. Make changes if needed
5. Click **"Deploy"**

### Rollback to Previous Version

1. Cloud Run console → Your service
2. **"Revisions"** tab
3. Find previous working revision
4. Click **⋮** (three dots)
5. Click **"Manage traffic"**
6. Set 100% traffic to that revision
7. Click **"Save"**

---

## Monitoring

### Set Up Alerts

1. Go to: https://console.cloud.google.com/monitoring
2. Create alerting policy for:
   - High error rate
   - High latency
   - Memory usage

### View Metrics

1. Cloud Run console → Your service
2. **"Metrics"** tab
3. View graphs for:
   - Request count
   - Request latency
   - Container instance count
   - Memory utilization

---

## Cost Management

### View Current Costs

1. Go to: https://console.cloud.google.com/billing
2. **"Reports"**
3. Filter by service: Cloud Run

### Set Budget Alert

1. Go to: https://console.cloud.google.com/billing/budgets
2. Click **"Create Budget"**
3. Set amount: `$5/month`
4. Set alerts at: 50%, 90%, 100%

---

## Security Best Practices

✅ **Completed:**
- Database password in Secret Manager
- Cloud SQL private connection (Unix socket)
- CORS restricted to frontend domain
- HTTPS enforced automatically

**Optional Enhancements:**
- Enable VPC Connector for additional security
- Set up IAM service accounts with minimal permissions
- Enable Binary Authorization

---

## Useful Links

**Your Resources:**
- Cloud Run: https://console.cloud.google.com/run
- Cloud SQL: https://console.cloud.google.com/sql
- Secret Manager: https://console.cloud.google.com/security/secret-manager
- Cloud Build: https://console.cloud.google.com/cloud-build
- Logs: https://console.cloud.google.com/logs

**Documentation:**
- Cloud Run Docs: https://cloud.google.com/run/docs
- Cloud SQL for Cloud Run: https://cloud.google.com/sql/docs/postgres/connect-run

---

## Summary

**What You Did:**
1. ✅ Created secret for database password
2. ✅ Deployed Spring Boot app from GitHub to Cloud Run
3. ✅ Connected to Cloud SQL PostgreSQL
4. ✅ Configured environment variables
5. ✅ Got backend URL
6. ✅ Updated frontend with backend URL

**Your Stack:**
```
Frontend: Cloudflare Pages
Backend: Cloud Run (Spring Boot)
Database: Cloud SQL (PostgreSQL)
```

**Monthly Cost:** $0-5 (within free tiers)

---

## Next Steps

After successful deployment:
- ✅ Test all API endpoints
- ✅ Test frontend → backend → database flow
- ⬜ Set up custom domain (optional)
- ⬜ Configure monitoring alerts
- ⬜ Set up CI/CD pipeline for auto-deploy

Congratulations! Your backend is now live on Cloud Run! 🎉
