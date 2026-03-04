#!/bin/bash

# Configuration
LOCAL_JAR_PATH="/Volumes/blackcat/workspace/connector/ConnIdSCIMBundle/target/net.tirasa.connid.bundles.scim-1.0.6-SNAPSHOT-bundle.jar"
REMOTE_USER="moonlc"
REMOTE_HOST="moonlc"
REMOTE_WORKSPACE="~/workspace/midpoint"
DOCKER_CONTAINER="midpoint_midpoint_server_1"
DOCKER_DEST="/opt/midpoint/var/icf-connectors/"
JAR_FILENAME="net.tirasa.connid.bundles.scim-1.0.6-SNAPSHOT-bundle.jar"

set -e  # Exit on error

echo "Starting connector deployment..."

# Step 1: Copy JAR to remote server
echo "Step 1: Copying JAR to remote server..."
scp "$LOCAL_JAR_PATH" "$REMOTE_HOST:$REMOTE_WORKSPACE/"

# Step 2: Execute remote commands via SSH
echo "Step 2: Deploying to Docker container..."
ssh "$REMOTE_USER@$REMOTE_HOST" << 'EOF'
set -e
DOCKER_CONTAINER="midpoint_midpoint_server_1"
DOCKER_DEST="/opt/midpoint/var/icf-connectors/"
JAR_FILENAME="net.tirasa.connid.bundles.scim-1.0.6-SNAPSHOT-bundle.jar"
REMOTE_WORKSPACE="$HOME/workspace/midpoint"

echo "Copying JAR into Docker container..."
docker cp "$REMOTE_WORKSPACE/$JAR_FILENAME" "$DOCKER_CONTAINER:$DOCKER_DEST"

echo "Restarting Docker container..."
docker restart "$DOCKER_CONTAINER"

echo "Deployment complete!"
EOF

echo "All done!"