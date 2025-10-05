#!/bin/bash

# Cloud SQL PostgreSQL Setup Script
# This script creates a PostgreSQL instance in Google Cloud Platform

set -e  # Exit on any error

echo "=== Cloud SQL PostgreSQL Setup ==="
echo ""

# Configuration Variables
read -p "Enter your GCP Project ID (or press Enter to use current): " PROJECT_ID
if [ -z "$PROJECT_ID" ]; then
  PROJECT_ID=$(gcloud config get-value project)
fi

echo "Using Project ID: $PROJECT_ID"
gcloud config set project $PROJECT_ID

# Region selection
echo ""
echo "Available regions (free tier compatible):"
echo "  1. us-central1 (Iowa)"
echo "  2. us-east1 (South Carolina)"
echo "  3. us-west1 (Oregon)"
read -p "Select region (1-3, default: 1): " REGION_CHOICE

case $REGION_CHOICE in
  2) REGION="us-east1" ;;
  3) REGION="us-west1" ;;
  *) REGION="us-central1" ;;
esac

echo "Selected region: $REGION"

# Database configuration
INSTANCE_NAME="portfolio-db"
DB_NAME="portfoliodb"
DB_USER="portfolio_user"
DB_PASSWORD=$(openssl rand -base64 32 | tr -d "=+/" | cut -c1-25)

echo ""
echo "Database Configuration:"
echo "  Instance Name: $INSTANCE_NAME"
echo "  Database Name: $DB_NAME"
echo "  Database User: $DB_USER"
echo "  Generated Password: $DB_PASSWORD"
echo ""
read -p "Continue with these settings? (y/n): " CONFIRM

if [ "$CONFIRM" != "y" ]; then
  echo "Setup cancelled."
  exit 1
fi

# Step 1: Enable required APIs
echo ""
echo "Step 1: Enabling required APIs..."
gcloud services enable sqladmin.googleapis.com sql-component.googleapis.com

# Step 2: Create Cloud SQL instance
echo ""
echo "Step 2: Creating Cloud SQL PostgreSQL instance..."
echo "This may take 5-10 minutes..."

gcloud sql instances create $INSTANCE_NAME \
  --database-version=POSTGRES_15 \
  --tier=db-f1-micro \
  --region=$REGION \
  --storage-type=HDD \
  --storage-size=10GB \
  --network=default \
  --backup-start-time=03:00 \
  --maintenance-window-day=SUN \
  --maintenance-window-hour=4 \
  --maintenance-release-channel=production

echo "✓ Instance created successfully!"

# Step 3: Create database
echo ""
echo "Step 3: Creating database..."
gcloud sql databases create $DB_NAME --instance=$INSTANCE_NAME
echo "✓ Database created!"

# Step 4: Create user
echo ""
echo "Step 4: Creating database user..."
gcloud sql users create $DB_USER \
  --instance=$INSTANCE_NAME \
  --password=$DB_PASSWORD
echo "✓ User created!"

# Step 5: Get connection details
echo ""
echo "Step 5: Retrieving connection details..."
CONNECTION_NAME=$(gcloud sql instances describe $INSTANCE_NAME --format='value(connectionName)')
PUBLIC_IP=$(gcloud sql instances describe $INSTANCE_NAME --format='value(ipAddresses[0].ipAddress)')

# Step 6: Create credentials file
CREDS_FILE="$HOME/.portfolio-db-credentials"
cat > $CREDS_FILE << EOF
# Portfolio Database Credentials
# Generated: $(date)

PROJECT_ID=$PROJECT_ID
REGION=$REGION
INSTANCE_NAME=$INSTANCE_NAME
CONNECTION_NAME=$CONNECTION_NAME
PUBLIC_IP=$PUBLIC_IP

DB_NAME=$DB_NAME
DB_USER=$DB_USER
DB_PASSWORD=$DB_PASSWORD

# Connection String for Cloud Run
DATABASE_URL=jdbc:postgresql:///$DB_NAME?cloudSqlInstance=$CONNECTION_NAME&socketFactory=com.google.cloud.sql.postgres.SocketFactory&user=$DB_USER

# Connection String for local development via Cloud SQL Proxy
LOCAL_DATABASE_URL=jdbc:postgresql://localhost:5432/$DB_NAME

# Cloud SQL Proxy command
# Run this in a separate terminal:
# cloud_sql_proxy -instances=$CONNECTION_NAME=tcp:5432

EOF

chmod 600 $CREDS_FILE

# Step 7: Store password in Secret Manager
echo ""
echo "Step 6: Storing password in Secret Manager..."
gcloud services enable secretmanager.googleapis.com

echo -n "$DB_PASSWORD" | gcloud secrets create db-password \
  --data-file=- \
  --replication-policy="automatic" 2>/dev/null || \
  echo -n "$DB_PASSWORD" | gcloud secrets versions add db-password --data-file=-

echo "✓ Password stored in Secret Manager!"

# Summary
echo ""
echo "=========================================="
echo "✓ Cloud SQL PostgreSQL Setup Complete!"
echo "=========================================="
echo ""
echo "Instance Details:"
echo "  Name: $INSTANCE_NAME"
echo "  Region: $REGION"
echo "  Connection Name: $CONNECTION_NAME"
echo "  Public IP: $PUBLIC_IP"
echo ""
echo "Database Details:"
echo "  Database: $DB_NAME"
echo "  User: $DB_USER"
echo "  Password: $DB_PASSWORD"
echo ""
echo "Credentials saved to: $CREDS_FILE"
echo ""
echo "Next Steps:"
echo "  1. Install Cloud SQL Proxy:"
echo "     curl -o cloud_sql_proxy https://dl.google.com/cloudsql/cloud_sql_proxy.darwin.amd64"
echo "     chmod +x cloud_sql_proxy"
echo ""
echo "  2. Start Cloud SQL Proxy (in a new terminal):"
echo "     ./cloud_sql_proxy -instances=$CONNECTION_NAME=tcp:5432"
echo ""
echo "  3. Update your application-dev.properties:"
echo "     spring.datasource.url=jdbc:postgresql://localhost:5432/$DB_NAME"
echo "     spring.datasource.username=$DB_USER"
echo "     spring.datasource.password=$DB_PASSWORD"
echo ""
echo "  4. Connect to database:"
echo "     gcloud sql connect $INSTANCE_NAME --user=$DB_USER --database=$DB_NAME"
echo ""
echo "Monthly Cost Estimate: \$0 (within free tier limits)"
echo "=========================================="
