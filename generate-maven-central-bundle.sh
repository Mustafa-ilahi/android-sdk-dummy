#!/bin/bash

# Script to generate a Maven Central compatible bundle
# This addresses the "Failed to associate file with coordinates" errors

set -e

echo "🔧 Generating Maven Central compatible bundle..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Clean previous builds
print_status "Cleaning previous builds..."
./gradlew clean

# Build the project
print_status "Building project..."
./gradlew :sdk:assembleRelease

# Publish to local Maven repository
print_status "Publishing to local Maven repository..."
./gradlew :sdk:publishToMavenLocal

# Navigate to the local Maven repository
LOCAL_MAVEN_REPO="$HOME/.m2/repository/io/github/mustafa-ilahi/sdk/1.0.0"

if [ -d "$LOCAL_MAVEN_REPO" ]; then
    print_status "Found local Maven repository at: $LOCAL_MAVEN_REPO"
    
    # Create bundle directory
    BUNDLE_DIR="maven-central-bundle"
    rm -rf "$BUNDLE_DIR"
    mkdir -p "$BUNDLE_DIR"
    
    # Copy and rename files to follow Maven Central convention
    print_status "Copying and renaming files to follow Maven Central convention..."
    
    # Copy POM file (should already be correctly named)
    if [ -f "$LOCAL_MAVEN_REPO/pom-default.xml" ]; then
        cp "$LOCAL_MAVEN_REPO/pom-default.xml" "$BUNDLE_DIR/sdk-1.0.0.pom"
    elif [ -f "$LOCAL_MAVEN_REPO/sdk-1.0.0.pom" ]; then
        cp "$LOCAL_MAVEN_REPO/sdk-1.0.0.pom" "$BUNDLE_DIR/sdk-1.0.0.pom"
    else
        print_error "POM file not found in expected location"
        exit 1
    fi
    
    # Copy and rename AAR file
    if [ -f "$LOCAL_MAVEN_REPO/sdk-release.aar" ]; then
        cp "$LOCAL_MAVEN_REPO/sdk-release.aar" "$BUNDLE_DIR/sdk-1.0.0.aar"
        if [ -f "$LOCAL_MAVEN_REPO/sdk-release.aar.asc" ]; then
            cp "$LOCAL_MAVEN_REPO/sdk-release.aar.asc" "$BUNDLE_DIR/sdk-1.0.0.aar.asc"
        fi
    else
        print_error "AAR file not found"
        exit 1
    fi
    
    # Copy and rename sources JAR
    if [ -f "$LOCAL_MAVEN_REPO/sdk-sources.jar" ]; then
        cp "$LOCAL_MAVEN_REPO/sdk-sources.jar" "$BUNDLE_DIR/sdk-1.0.0-sources.jar"
        if [ -f "$LOCAL_MAVEN_REPO/sdk-sources.jar.asc" ]; then
            cp "$LOCAL_MAVEN_REPO/sdk-sources.jar.asc" "$BUNDLE_DIR/sdk-1.0.0-sources.jar.asc"
        fi
    else
        print_error "Sources JAR not found"
        exit 1
    fi
    
    # Copy and rename javadoc JAR
    if [ -f "$LOCAL_MAVEN_REPO/sdk-javadoc.jar" ]; then
        cp "$LOCAL_MAVEN_REPO/sdk-javadoc.jar" "$BUNDLE_DIR/sdk-1.0.0-javadoc.jar"
        if [ -f "$LOCAL_MAVEN_REPO/sdk-javadoc.jar.asc" ]; then
            cp "$LOCAL_MAVEN_REPO/sdk-javadoc.jar.asc" "$BUNDLE_DIR/sdk-1.0.0-javadoc.jar.asc"
        fi
    else
        print_error "Javadoc JAR not found"
        exit 1
    fi
    
    # Verify bundle contents
    print_status "Bundle contents:"
    ls -la "$BUNDLE_DIR"
    
    # Create zip bundle
    BUNDLE_NAME="sdk-1.0.0-maven-central-bundle.zip"
    cd "$BUNDLE_DIR"
    zip -r "../$BUNDLE_NAME" .
    cd ..
    
    print_status "✅ Maven Central bundle created: $BUNDLE_NAME"
    print_status "Bundle contents:"
    unzip -l "$BUNDLE_NAME"
    
    print_status "🎉 Bundle generation completed!"
    print_status "You can now upload $BUNDLE_NAME to Maven Central"
    
else
    print_error "Local Maven repository not found at: $LOCAL_MAVEN_REPO"
    print_error "Please run './gradlew :sdk:publishToMavenLocal' first"
    exit 1
fi
