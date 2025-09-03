#!/bin/bash

# Publishing script for Alterna Android SDK to Maven Central
# Based on the Central Publisher Portal Guide: https://central.sonatype.org/publish/publish-portal-guide/

set -e

echo "🚀 Starting Alterna SDK publication to Maven Central..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if required environment variables are set
check_credentials() {
    print_status "Checking credentials..."
    
    if [ -z "$ossrhUsername" ] || [ -z "$ossrhPassword" ]; then
        print_error "OSSRH credentials not found!"
        print_warning "Please set the following environment variables:"
        print_warning "  export ossrhUsername=your_sonatype_username"
        print_warning "  export ossrhPassword=your_sonatype_password"
        print_warning "Or add them to gradle.properties file"
        exit 1
    fi
    
    if [ -z "$signingKeyId" ] || [ -z "$signingSecretKeyRingFile" ]; then
        print_error "GPG signing configuration not found!"
        print_warning "Please set the following environment variables:"
        print_warning "  export signingKeyId=your_gpg_key_id"
        print_warning "  export signingSecretKeyRingFile=path_to_your_secring_gpg"
        print_warning "Or add them to gradle.properties file"
        exit 1
    fi
    
    print_success "Credentials check passed"
}

# Clean and build the project
build_project() {
    print_status "Cleaning and building project..."
    
    ./gradlew clean
    ./gradlew :sdk:assembleRelease
    
    print_success "Project built successfully"
}

# Generate sources and javadoc JARs
generate_artifacts() {
    print_status "Generating sources and javadoc JARs..."
    
    ./gradlew :sdk:sourcesJar
    ./gradlew :sdk:javadocJar
    
    print_success "Artifacts generated successfully"
}

# Publish to Sonatype staging repository
publish_to_staging() {
    print_status "Publishing to Sonatype staging repository..."
    
    ./gradlew :sdk:publishReleasePublicationToSonatypeRepository
    
    print_success "Published to staging repository"
}

# Close and release the staging repository
close_and_release() {
    print_status "Closing and releasing staging repository..."
    
    ./gradlew closeAndReleaseSonatypeStagingRepository
    
    print_success "Staging repository closed and released"
}

# Main execution
main() {
    print_status "Starting Maven Central publication process..."
    print_warning "Make sure you have:"
    print_warning "1. Verified your namespace with Sonatype"
    print_warning "2. Set up GPG signing"
    print_warning "3. Configured your credentials"
    echo ""
    
    read -p "Do you want to continue? (y/N): " -n 1 -r
    echo ""
    
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        print_warning "Publication cancelled by user"
        exit 0
    fi
    
    check_credentials
    build_project
    generate_artifacts
    publish_to_staging
    close_and_release
    
    print_success "🎉 Publication completed successfully!"
    print_status "Your SDK should be available on Maven Central within a few minutes"
    print_status "Check: https://repo1.maven.org/maven2/com/alterna/sdk/"
}

# Run main function
main "$@"
