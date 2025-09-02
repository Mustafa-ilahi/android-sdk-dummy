#!/bin/bash

# Alterna SDK Publishing Script
# This script helps publish the SDK to various repositories

set -e

echo "🚀 Starting Alterna SDK Publishing Process..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if we're in the right directory
if [ ! -f "settings.gradle" ]; then
    print_error "Please run this script from the project root directory"
    exit 1
fi

# Check if gradlew exists
if [ ! -f "gradlew" ]; then
    print_error "gradlew not found. Please ensure you're in the correct directory"
    exit 1
fi

# Make gradlew executable
chmod +x gradlew

# Clean and build the project
print_status "Cleaning and building the project..."
./gradlew clean
./gradlew :sdk:assembleRelease

# Check if build was successful
if [ $? -eq 0 ]; then
    print_status "Build completed successfully!"
else
    print_error "Build failed. Please check the errors above."
    exit 1
fi

# Function to publish to GitHub Packages
publish_to_github() {
    print_status "Publishing to GitHub Packages..."
    
    # Check if credentials are set
    if [ -z "$GITHUB_USERNAME" ] || [ -z "$GITHUB_TOKEN" ]; then
        print_warning "GitHub credentials not found in environment variables."
        print_warning "Please set GITHUB_USERNAME and GITHUB_TOKEN environment variables."
        print_warning "Or add them to gradle.properties as gpr.user and gpr.key"
        return 1
    fi
    
    # Set credentials for this session
    export ORG_GRADLE_PROJECT_gpr_user=$GITHUB_USERNAME
    export ORG_GRADLE_PROJECT_gpr_key=$GITHUB_TOKEN
    
    ./gradlew :sdk:publishReleasePublicationToGitHubPackagesRepository
    
    if [ $? -eq 0 ]; then
        print_status "Successfully published to GitHub Packages!"
        print_status "Package URL: https://github.com/your-org/alterna-android-sdk/packages"
    else
        print_error "Failed to publish to GitHub Packages"
        return 1
    fi
}

# Function to publish to Maven Central
publish_to_maven_central() {
    print_status "Publishing to Maven Central..."
    
    # Check if credentials are set
    if [ -z "$OSSRH_USERNAME" ] || [ -z "$OSSRH_PASSWORD" ]; then
        print_warning "Maven Central credentials not found in environment variables."
        print_warning "Please set OSSRH_USERNAME and OSSRH_PASSWORD environment variables."
        return 1
    fi
    
    # Set credentials for this session
    export ORG_GRADLE_PROJECT_ossrhUsername=$OSSRH_USERNAME
    export ORG_GRADLE_PROJECT_ossrhPassword=$OSSRH_PASSWORD
    
    ./gradlew :sdk:publishReleasePublicationToMavenCentralRepository
    
    if [ $? -eq 0 ]; then
        print_status "Successfully published to Maven Central!"
    else
        print_error "Failed to publish to Maven Central"
        return 1
    fi
}

# Function to generate local AAR
generate_local_aar() {
    print_status "Generating local AAR file..."
    
    # The AAR is already generated in the build process
    AAR_PATH="sdk/build/outputs/aar/sdk-release.aar"
    
    if [ -f "$AAR_PATH" ]; then
        print_status "AAR file generated successfully!"
        print_status "Location: $AAR_PATH"
        print_status "File size: $(du -h "$AAR_PATH" | cut -f1)"
        
        # Copy to a more accessible location
        cp "$AAR_PATH" "alterna-sdk-1.0.0.aar"
        print_status "Copied to: alterna-sdk-1.0.0.aar"
    else
        print_error "AAR file not found at $AAR_PATH"
        return 1
    fi
}

# Main menu
echo ""
echo "Select publishing option:"
echo "1) Publish to GitHub Packages"
echo "2) Publish to Maven Central"
echo "3) Generate local AAR only"
echo "4) All of the above"
echo "5) Exit"
echo ""

read -p "Enter your choice (1-5): " choice

case $choice in
    1)
        publish_to_github
        ;;
    2)
        publish_to_maven_central
        ;;
    3)
        generate_local_aar
        ;;
    4)
        generate_local_aar
        publish_to_github
        publish_to_maven_central
        ;;
    5)
        print_status "Exiting..."
        exit 0
        ;;
    *)
        print_error "Invalid choice. Please run the script again."
        exit 1
        ;;
esac

print_status "Publishing process completed!"
echo ""
print_status "Next steps:"
print_status "1. Update your integration guide with the new version"
print_status "2. Test the published package in a sample project"
print_status "3. Update documentation if needed"
print_status "4. Tag the release in Git: git tag v1.0.0 && git push origin v1.0.0"
