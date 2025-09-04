#!/bin/bash

# Create an unsigned Maven Central bundle to test basic validation
# This addresses invalid signature errors by removing signatures

set -e

echo "🔧 Creating unsigned Maven Central bundle..."

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
    BUNDLE_DIR="maven-central-unsigned"
    rm -rf "$BUNDLE_DIR"
    mkdir -p "$BUNDLE_DIR"
    
    # Create the proper Maven directory structure
    MAVEN_DIR="$BUNDLE_DIR/io/github/mustafa-ilahi/sdk/1.0.0"
    mkdir -p "$MAVEN_DIR"
    
    print_status "Creating proper Maven directory structure..."
    
    # Copy all files from local Maven repo
    print_status "Copying files from local Maven repository..."
    cp "$LOCAL_MAVEN_REPO"/* "$MAVEN_DIR/"
    
    # Remove any existing checksum and signature files
    print_status "Cleaning existing checksums and signatures..."
    rm -f "$MAVEN_DIR"/*.md5
    rm -f "$MAVEN_DIR"/*.sha1
    rm -f "$MAVEN_DIR"/*.asc
    
    # Generate correct checksums for all files
    print_status "Generating correct MD5 and SHA1 checksums..."
    cd "$MAVEN_DIR"
    
    for file in sdk-1.0.0.*; do
        if [ -f "$file" ] && [[ ! "$file" =~ \.(md5|sha1|asc)$ ]]; then
            print_status "Generating checksums for $file"
            # Generate MD5 checksum
            md5sum "$file" | cut -d' ' -f1 > "${file}.md5"
            # Generate SHA1 checksum
            sha1sum "$file" | cut -d' ' -f1 > "${file}.sha1"
            
            # Verify checksums
            echo "MD5 for $file: $(cat ${file}.md5)"
            echo "SHA1 for $file: $(cat ${file}.sha1)"
        fi
    done
    
    cd - > /dev/null
    
    # Verify the final structure
    print_status "Final bundle structure:"
    find "$BUNDLE_DIR" -type f | sort
    
    # Verify checksums
    print_status "Verifying checksums..."
    cd "$MAVEN_DIR"
    for file in sdk-1.0.0.*; do
        if [ -f "$file" ] && [[ ! "$file" =~ \.(md5|sha1|asc)$ ]]; then
            expected_md5=$(cat "${file}.md5")
            actual_md5=$(md5sum "$file" | cut -d' ' -f1)
            expected_sha1=$(cat "${file}.sha1")
            actual_sha1=$(sha1sum "$file" | cut -d' ' -f1)
            
            if [ "$expected_md5" = "$actual_md5" ]; then
                print_status "✅ MD5 checksum verified for $file"
            else
                print_error "❌ MD5 checksum mismatch for $file"
            fi
            
            if [ "$expected_sha1" = "$actual_sha1" ]; then
                print_status "✅ SHA1 checksum verified for $file"
            else
                print_error "❌ SHA1 checksum mismatch for $file"
            fi
        fi
    done
    cd - > /dev/null
    
    # Create zip bundle
    BUNDLE_NAME="sdk-1.0.0-maven-central-unsigned.zip"
    cd "$BUNDLE_DIR"
    zip -r "../$BUNDLE_NAME" .
    cd ..
    
    print_status "✅ Unsigned Maven Central bundle created: $BUNDLE_NAME"
    print_status "Bundle contents:"
    unzip -l "$BUNDLE_NAME"
    
    print_status "🎉 Bundle generation completed!"
    print_status "This bundle includes:"
    print_status "  - Proper Maven directory structure"
    print_status "  - Correct MD5 and SHA1 checksums for all files"
    print_status "  - NO digital signatures (to avoid invalid signature errors)"
    print_status "  - Verified checksums"
    
    print_warning "Note: This bundle is unsigned. Maven Central may require signatures"
    print_warning "for production releases, but this should pass basic validation."
    
else
    print_error "Local Maven repository not found at: $LOCAL_MAVEN_REPO"
    print_error "Please run './gradlew :sdk:publishToMavenLocal' first"
    exit 1
fi
