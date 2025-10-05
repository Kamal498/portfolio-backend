#!/bin/bash

# Fix Secret Manager Permission Error
# This grants Cloud Run service account access to the db-password secret

echo "Granting Secret Manager access to Cloud Run service account..."

# Your service account from the error
SERVICE_ACCOUNT="54173901099-compute@developer.gserviceaccount.com"

# Grant access to db-password secret
gcloud secrets add-iam-policy-binding db-password \
  --member="serviceAccount:$SERVICE_ACCOUNT" \
  --role="roles/secretmanager.secretAccessor"

echo ""
echo "✓ Permission granted!"
echo ""
echo "Now retry your Cloud Run deployment."
