#!/bin/bash

# Complete Maven Central bundle fix script
# This addresses checksums, signatures, and directory structure issues

set -e

echo "🔧 Creating complete Maven Central compliant bundle..."

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
    
    # Create proper Maven directory structure
    BUNDLE_DIR="maven-central-complete"
    rm -rf "$BUNDLE_DIR"
    mkdir -p "$BUNDLE_DIR"
    
    # Create the proper Maven directory structure
    # Maven Central expects: io/github/mustafa-ilahi/sdk/1.0.0/
    MAVEN_DIR="$BUNDLE_DIR/io/github/mustafa-ilahi/sdk/1.0.0"
    mkdir -p "$MAVEN_DIR"
    
    print_status "Creating proper Maven directory structure..."
    
    # Copy POM file
    if [ -f "$LOCAL_MAVEN_REPO/pom-default.xml" ]; then
        cp "$LOCAL_MAVEN_REPO/pom-default.xml" "$MAVEN_DIR/sdk-1.0.0.pom"
    elif [ -f "$LOCAL_MAVEN_REPO/sdk-1.0.0.pom" ]; then
        cp "$LOCAL_MAVEN_REPO/sdk-1.0.0.pom" "$MAVEN_DIR/sdk-1.0.0.pom"
    else
        print_error "POM file not found"
        exit 1
    fi
    
    # Copy AAR file
    if [ -f "$LOCAL_MAVEN_REPO/sdk-1.0.0.aar" ]; then
        cp "$LOCAL_MAVEN_REPO/sdk-1.0.0.aar" "$MAVEN_DIR/sdk-1.0.0.aar"
    else
        print_error "AAR file not found"
        exit 1
    fi
    
    # Copy sources JAR
    if [ -f "$LOCAL_MAVEN_REPO/sdk-1.0.0-sources.jar" ]; then
        cp "$LOCAL_MAVEN_REPO/sdk-1.0.0-sources.jar" "$MAVEN_DIR/sdk-1.0.0-sources.jar"
    else
        print_error "Sources JAR not found"
        exit 1
    fi
    
    # Copy javadoc JAR
    if [ -f "$LOCAL_MAVEN_REPO/sdk-1.0.0-javadoc.jar" ]; then
        cp "$LOCAL_MAVEN_REPO/sdk-1.0.0-javadoc.jar" "$MAVEN_DIR/sdk-1.0.0-javadoc.jar"
    else
        print_error "Javadoc JAR not found"
        exit 1
    fi
    
    # Generate checksums for all files
    print_status "Generating MD5 and SHA1 checksums..."
    cd "$MAVEN_DIR"
    
    for file in sdk-1.0.0.*; do
        if [ -f "$file" ]; then
            print_status "Generating checksums for $file"
            md5sum "$file" > "${file}.md5"
            sha1sum "$file" > "${file}.sha1"
        fi
    done
    
    cd - > /dev/null
    
    # Generate new signatures (remove old invalid ones first)
    print_status "Generating new digital signatures..."
    
    # Check if GPG is available
    if command -v gpg &> /dev/null; then
        # Remove old signature files
        rm -f "$MAVEN_DIR"/*.asc
        
        # Generate new signatures
        for file in "$MAVEN_DIR"/sdk-1.0.0.*; do
            if [ -f "$file" ] && [[ ! "$file" =~ \.(md5|sha1)$ ]]; then
                print_status "Signing $file"
                gpg --armor --detach-sign "$file"
                mv "${file}.asc" "$MAVEN_DIR/"
            fi
        done
    else
        print_warning "GPG not found. Skipping signature generation."
        print_warning "You'll need to sign the files manually or configure GPG."
    fi
    
    # Verify the structure
    print_status "Final bundle structure:"
    find "$BUNDLE_DIR" -type f | sort
    
    # Create zip bundle
    BUNDLE_NAME="sdk-1.0.0-maven-central-complete.zip"
    cd "$BUNDLE_DIR"
    zip -r "../$BUNDLE_NAME" .
    cd ..
    
    print_status "✅ Complete Maven Central bundle created: $BUNDLE_NAME"
    print_status "Bundle contents:"
    unzip -l "$BUNDLE_NAME"
    
    print_status "🎉 Bundle generation completed!"
    print_status "This bundle includes:"
    print_status "  - Proper Maven directory structure"
    print_status "  - MD5 and SHA1 checksums for all files"
    print_status "  - Digital signatures (if GPG is configured)"
    print_status "  - Correctly named files"
    
else
    print_error "Local Maven repository not found at: $LOCAL_MAVEN_REPO"
    print_error "Please run './gradlew :sdk:publishToMavenLocal' first"
    exit 1
fi
