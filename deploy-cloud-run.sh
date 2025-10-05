#!/bin/bash

# Configuration
PROJECT_ID="YOUR_PROJECT_ID"
REGION="us-central1"
SERVICE_NAME="portfolio-backend"
DB_CONNECTION_NAME="$PROJECT_ID:$REGION:portfolio-db"

# Set project
gcloud config set project $PROJECT_ID

# Enable required APIs
gcloud services enable \
  run.googleapis.com \
  cloudbuild.googleapis.com \
  sql-component.googleapis.com \
  sqladmin.googleapis.com

# Build and push container to Artifact Registry
gcloud builds submit --tag gcr.io/$PROJECT_ID/$SERVICE_NAME

# Deploy to Cloud Run with Cloud SQL connection
gcloud run deploy $SERVICE_NAME \
  --image gcr.io/$PROJECT_ID/$SERVICE_NAME \
  --platform managed \
  --region $REGION \
  --allow-unauthenticated \
  --add-cloudsql-instances $DB_CONNECTION_NAME \
  --set-env-vars "SPRING_PROFILES_ACTIVE=prod" \
  --set-env-vars "DATABASE_URL=jdbc:postgresql://localhost:5432/portfoliodb?cloudSqlInstance=$DB_CONNECTION_NAME&socketFactory=com.google.cloud.sql.postgres.SocketFactory" \
  --set-env-vars "DATABASE_USERNAME=portfolio_user" \
  --set-secrets "DATABASE_PASSWORD=db-password:latest" \
  --set-env-vars "ALLOWED_ORIGINS=https://your-portfolio.pages.dev" \
  --memory 512Mi \
  --cpu 1 \
  --min-instances 0 \
  --max-instances 10 \
  --port 8080

# Get the deployed URL
gcloud run services describe $SERVICE_NAME --region $REGION --format 'value(status.url)'

echo "Backend deployed successfully!"
