#!/bin/bash
set -e

echo "=== Portfolio Backend - Cloud Run Deployment ==="
echo ""

# Get Project ID
read -p "Enter your GCP Project ID: " PROJECT_ID
if [ -z "$PROJECT_ID" ]; then
  echo "Error: Project ID is required"
  exit 1
fi

# Configuration
REGION="us-central1"
SERVICE_NAME="portfolio-backend"
INSTANCE_NAME="portfolio-db"
DB_NAME="portfoliodb"
DB_USER="postgres"

# Get DB Password
read -sp "Enter database password: " DB_PASSWORD
echo ""

# Get frontend URL for CORS
read -p "Enter frontend URL (e.g., https://your-site.pages.dev): " FRONTEND_URL

echo ""
echo "Configuration:"
echo "  Project: $PROJECT_ID"
echo "  Region: $REGION"
echo "  Service: $SERVICE_NAME"
echo "  Database: $INSTANCE_NAME"
echo "  Frontend: $FRONTEND_URL"
echo ""
read -p "Continue with deployment? (y/n): " CONFIRM

if [ "$CONFIRM" != "y" ]; then
  echo "Deployment cancelled"
  exit 0
fi

# Set project
echo ""
echo "Step 1: Setting GCP project..."
gcloud config set project $PROJECT_ID

# Enable APIs
echo ""
echo "Step 2: Enabling required APIs..."
gcloud services enable \
  run.googleapis.com \
  cloudbuild.googleapis.com \
  artifactregistry.googleapis.com \
  secretmanager.googleapis.com

# Get Cloud SQL connection name
echo ""
echo "Step 3: Getting Cloud SQL connection details..."
DB_CONNECTION_NAME=$(gcloud sql instances describe $INSTANCE_NAME --format='value(connectionName)')
echo "Connection Name: $DB_CONNECTION_NAME"

# Store password in Secret Manager
echo ""
echo "Step 4: Storing database password in Secret Manager..."
echo -n "$DB_PASSWORD" | gcloud secrets create db-password --data-file=- 2>/dev/null || \
  echo -n "$DB_PASSWORD" | gcloud secrets versions add db-password --data-file=-

echo "✓ Password stored in Secret Manager"

# Build and push container
echo ""
echo "Step 5: Building container image (this may take 3-5 minutes)..."
gcloud builds submit --tag gcr.io/$PROJECT_ID/$SERVICE_NAME

# Deploy to Cloud Run
echo ""
echo "Step 6: Deploying to Cloud Run..."
gcloud run deploy $SERVICE_NAME \
  --image gcr.io/$PROJECT_ID/$SERVICE_NAME \
  --platform managed \
  --region $REGION \
  --allow-unauthenticated \
  --add-cloudsql-instances $DB_CONNECTION_NAME \
  --set-env-vars "SPRING_PROFILES_ACTIVE=prod" \
  --set-env-vars "CLOUD_SQL_CONNECTION_NAME=$DB_CONNECTION_NAME" \
  --set-env-vars "DATABASE_USERNAME=$DB_USER" \
  --set-secrets "DATABASE_PASSWORD=db-password:latest" \
  --set-env-vars "ALLOWED_ORIGINS=$FRONTEND_URL" \
  --memory 1Gi \
  --cpu 1 \
  --min-instances 0 \
  --max-instances 5 \
  --port 8080 \
  --timeout 300

# Get service URL
echo ""
echo "Step 7: Getting service URL..."
SERVICE_URL=$(gcloud run services describe $SERVICE_NAME --region $REGION --format='value(status.url)')

echo ""
echo "=========================================="
echo "✓ Deployment Complete!"
echo "=========================================="
echo ""
echo "Backend URL: $SERVICE_URL"
echo ""
echo "Test your API:"
echo "  curl $SERVICE_URL/api/personal-info"
echo ""
echo "Update your frontend .env.production:"
echo "  VITE_API_BASE_URL=$SERVICE_URL/api"
echo ""
echo "View logs:"
echo "  gcloud run services logs read $SERVICE_NAME --region $REGION"
echo ""
echo "=========================================="
