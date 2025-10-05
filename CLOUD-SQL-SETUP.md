# Cloud SQL PostgreSQL Setup Guide

## Prerequisites

### 1. Install Google Cloud SDK
```bash
# macOS
brew install --cask google-cloud-sdk

# Or download from: https://cloud.google.com/sdk/docs/install
```

### 2. Authenticate with Google Cloud
```bash
gcloud auth login
gcloud auth application-default login
```

### 3. Create or Select a GCP Project
```bash
# List existing projects
gcloud projects list

# Create new project (optional)
gcloud projects create portfolio-prod-$(date +%s) --name="Portfolio"

# Set active project
gcloud config set project YOUR_PROJECT_ID
```

### 4. Enable Billing
**Important:** You need billing enabled to use Cloud SQL (even for free tier)
- Go to: https://console.cloud.google.com/billing
- Link a billing account to your project
- Free tier includes: db-f1-micro instance with 10GB storage

---

## Quick Setup (Automated)

### Run the Setup Script

```bash
cd /Users/k0p0ghj/programs/personal/portfolio-backend
./setup-cloud-sql.sh
```

The script will:
1. ✓ Enable required APIs
2. ✓ Create Cloud SQL PostgreSQL instance (db-f1-micro)
3. ✓ Create database `portfoliodb`
4. ✓ Create user with secure password
5. ✓ Store credentials in Secret Manager
6. ✓ Save connection details to `~/.portfolio-db-credentials`

**Time:** ~10 minutes (instance creation takes 5-8 minutes)

---

## Manual Setup (Step by Step)

### Step 1: Enable APIs
```bash
gcloud services enable sqladmin.googleapis.com sql-component.googleapis.com
```

### Step 2: Create Cloud SQL Instance
```bash
gcloud sql instances create portfolio-db \
  --database-version=POSTGRES_15 \
  --tier=db-f1-micro \
  --region=us-central1 \
  --storage-type=HDD \
  --storage-size=10GB \
  --backup-start-time=03:00
```

**Free Tier Specs:**
- Instance: db-f1-micro (shared CPU, 614MB RAM)
- Storage: 10GB HDD
- Backups: Daily automated backups
- Cost: $0/month (within free tier limits)

### Step 3: Create Database
```bash
gcloud sql databases create portfoliodb --instance=portfolio-db
```

### Step 4: Create User
```bash
# Generate secure password
DB_PASSWORD=$(openssl rand -base64 32 | tr -d "=+/" | cut -c1-25)
echo "Password: $DB_PASSWORD"

# Create user
gcloud sql users create portfolio_user \
  --instance=portfolio-db \
  --password=$DB_PASSWORD
```

### Step 5: Get Connection Details
```bash
# Get connection name
gcloud sql instances describe portfolio-db --format='value(connectionName)'
# Output: YOUR_PROJECT_ID:us-central1:portfolio-db

# Get public IP
gcloud sql instances describe portfolio-db --format='value(ipAddresses[0].ipAddress)'
```

---

## Local Development Setup

### Install Cloud SQL Proxy

**macOS:**
```bash
curl -o cloud_sql_proxy https://dl.google.com/cloudsql/cloud_sql_proxy.darwin.amd64
chmod +x cloud_sql_proxy
sudo mv cloud_sql_proxy /usr/local/bin/
```

**Linux:**
```bash
curl -o cloud_sql_proxy https://dl.google.com/cloudsql/cloud_sql_proxy.linux.amd64
chmod +x cloud_sql_proxy
sudo mv cloud_sql_proxy /usr/local/bin/
```

**Windows:**
Download from: https://dl.google.com/cloudsql/cloud_sql_proxy.x64.exe

### Start Cloud SQL Proxy

```bash
# Get your connection name first
CONNECTION_NAME=$(gcloud sql instances describe portfolio-db --format='value(connectionName)')

# Start proxy (keep this running in a separate terminal)
cloud_sql_proxy -instances=$CONNECTION_NAME=tcp:5432
```

**Alternative: Run in background**
```bash
cloud_sql_proxy -instances=$CONNECTION_NAME=tcp:5432 &
```

### Update Application Properties

**application-dev.properties:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/portfoliodb
spring.datasource.username=portfolio_user
spring.datasource.password=YOUR_GENERATED_PASSWORD
```

**For Production (application-prod.properties):**
```properties
spring.datasource.url=jdbc:postgresql:///portfoliodb?cloudSqlInstance=${CONNECTION_NAME}&socketFactory=com.google.cloud.sql.postgres.SocketFactory&user=portfolio_user
spring.datasource.password=${DATABASE_PASSWORD}
```

---

## Connect to Database

### Via gcloud CLI
```bash
gcloud sql connect portfolio-db --user=portfolio_user --database=portfoliodb
```

### Via Cloud SQL Proxy + psql
```bash
# Make sure proxy is running
cloud_sql_proxy -instances=$CONNECTION_NAME=tcp:5432

# In another terminal
psql -h localhost -U portfolio_user -d portfoliodb
```

### Via DBeaver / pgAdmin
- Host: localhost
- Port: 5432
- Database: portfoliodb
- Username: portfolio_user
- Password: (your generated password)

---

## Initialize Database Schema

### Option 1: Let Spring Boot Auto-Create

Your `application-dev.properties` has:
```properties
spring.jpa.hibernate.ddl-auto=update
```

Just run your Spring Boot app and tables will be created automatically.

### Option 2: Run SQL Script Manually

```bash
# Connect to database
gcloud sql connect portfolio-db --user=portfolio_user --database=portfoliodb

# Run your SQL script
\i /path/to/schema.sql
```

---

## Security Best Practices

### 1. Store Password in Secret Manager
```bash
echo -n "YOUR_PASSWORD" | gcloud secrets create db-password --data-file=-

# Grant access to Cloud Run service account
gcloud secrets add-iam-policy-binding db-password \
  --member="serviceAccount:YOUR_PROJECT_ID@appspot.gserviceaccount.com" \
  --role="roles/secretmanager.secretAccessor"
```

### 2. Use Private IP (Recommended for Production)
```bash
# Enable Private IP
gcloud sql instances patch portfolio-db \
  --network=projects/YOUR_PROJECT_ID/global/networks/default \
  --no-assign-ip
```

### 3. Restrict Authorized Networks
```bash
# Allow only specific IPs
gcloud sql instances patch portfolio-db \
  --authorized-networks=YOUR_IP_ADDRESS/32
```

---

## Monitoring & Maintenance

### View Instance Status
```bash
gcloud sql instances describe portfolio-db
```

### View Database Logs
```bash
gcloud sql operations list --instance=portfolio-db --limit=10
```

### Check Connection Count
```bash
gcloud sql instances describe portfolio-db \
  --format='value(settings.ipConfiguration.requireSsl)'
```

### Create Manual Backup
```bash
gcloud sql backups create --instance=portfolio-db
```

### List Backups
```bash
gcloud sql backups list --instance=portfolio-db
```

---

## Cost Management

### Free Tier Limits
- **Instance:** db-f1-micro (1 per project)
- **Storage:** 10GB HDD
- **Backups:** Included
- **Cost:** $0/month if within limits

### Monitor Costs
```bash
# Check current billing
gcloud billing accounts list
gcloud billing projects describe YOUR_PROJECT_ID
```

### Set Budget Alert
1. Go to: https://console.cloud.google.com/billing/budgets
2. Create budget: $5/month
3. Set alert at 50%, 90%, 100%

---

## Troubleshooting

### Issue: "Cloud SQL Admin API has not been used"
```bash
gcloud services enable sqladmin.googleapis.com
```

### Issue: "Permission denied"
```bash
# Check your account
gcloud auth list

# Re-authenticate
gcloud auth login
gcloud auth application-default login
```

### Issue: "Cannot connect via proxy"
```bash
# Check proxy is running
ps aux | grep cloud_sql_proxy

# Restart proxy
pkill cloud_sql_proxy
cloud_sql_proxy -instances=$CONNECTION_NAME=tcp:5432
```

### Issue: "Connection refused"
```bash
# Verify instance is running
gcloud sql instances describe portfolio-db --format='value(state)'
# Should output: RUNNABLE

# Check Cloud SQL Proxy logs
cloud_sql_proxy -instances=$CONNECTION_NAME=tcp:5432 -verbose
```

### Issue: "Password authentication failed"
```bash
# Reset password
gcloud sql users set-password portfolio_user \
  --instance=portfolio-db \
  --password=NEW_PASSWORD
```

---

## Useful Commands

### Start/Stop Instance
```bash
# Stop instance (saves costs)
gcloud sql instances patch portfolio-db --activation-policy=NEVER

# Start instance
gcloud sql instances patch portfolio-db --activation-policy=ALWAYS
```

### Delete Instance (Careful!)
```bash
gcloud sql instances delete portfolio-db
```

### Export Database
```bash
# Create bucket first
gsutil mb gs://portfolio-backup

# Export database
gcloud sql export sql portfolio-db gs://portfolio-backup/backup-$(date +%Y%m%d).sql \
  --database=portfoliodb
```

### Import Database
```bash
gcloud sql import sql portfolio-db gs://portfolio-backup/backup.sql \
  --database=portfoliodb
```

---

## Next Steps

After setup is complete:

1. ✅ Database instance running
2. ✅ Cloud SQL Proxy installed
3. ✅ Local development configured
4. → Test connection from Spring Boot app
5. → Deploy backend to Cloud Run
6. → Configure production environment variables

Credentials saved in: `~/.portfolio-db-credentials`

For deployment to Cloud Run, see: `DEPLOYMENT_GUIDE.md`
