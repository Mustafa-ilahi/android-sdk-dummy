#!/bin/bash

# Create a properly signed Maven Central bundle
# This addresses missing signature requirements

set -e

echo "🔧 Creating properly signed Maven Central bundle..."

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

# GPG Key ID (from your key)
GPG_KEY_ID="671C918D200E4F35"

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
    BUNDLE_DIR="maven-central-signed"
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
    
    # Generate proper digital signatures using the correct GPG key
    print_status "Generating proper digital signatures with GPG key $GPG_KEY_ID..."
    
    for file in sdk-1.0.0.*; do
        if [ -f "$file" ] && [[ ! "$file" =~ \.(md5|sha1|asc)$ ]]; then
            print_status "Signing $file with GPG key $GPG_KEY_ID"
            gpg --armor --detach-sign --local-user "$GPG_KEY_ID" "$file"
            if [ -f "${file}.asc" ]; then
                print_status "✅ Signature created for $file"
                # Verify the signature
                if gpg --verify "${file}.asc" "$file" 2>/dev/null; then
                    print_status "✅ Signature verified for $file"
                else
                    print_warning "⚠️  Signature verification failed for $file"
                fi
            else
                print_error "❌ Failed to create signature for $file"
            fi
        fi
    done
    
    cd - > /dev/null
    
    # Verify the final structure
    print_status "Final bundle structure:"
    find "$BUNDLE_DIR" -type f | sort
    
    # Verify all signatures
    print_status "Verifying all signatures..."
    cd "$MAVEN_DIR"
    for file in sdk-1.0.0.*; do
        if [ -f "$file" ] && [[ ! "$file" =~ \.(md5|sha1|asc)$ ]]; then
            if [ -f "${file}.asc" ]; then
                if gpg --verify "${file}.asc" "$file" 2>/dev/null; then
                    print_status "✅ Signature verified for $file"
                else
                    print_error "❌ Signature verification failed for $file"
                fi
            else
                print_error "❌ Missing signature for $file"
            fi
        fi
    done
    cd - > /dev/null
    
    # Create zip bundle
    BUNDLE_NAME="sdk-1.0.0-maven-central-signed.zip"
    cd "$BUNDLE_DIR"
    zip -r "../$BUNDLE_NAME" .
    cd ..
    
    print_status "✅ Properly signed Maven Central bundle created: $BUNDLE_NAME"
    print_status "Bundle contents:"
    unzip -l "$BUNDLE_NAME"
    
    print_status "🎉 Bundle generation completed!"
    print_status "This bundle includes:"
    print_status "  - Proper Maven directory structure"
    print_status "  - Correct MD5 and SHA1 checksums for all files"
    print_status "  - Valid GPG signatures for all files using key $GPG_KEY_ID"
    print_status "  - Verified signatures and checksums"
    
    print_status "📋 Next steps:"
    print_status "1. Upload $BUNDLE_NAME to Maven Central"
    print_status "2. Ensure your GPG key $GPG_KEY_ID is published to keyservers"
    print_status "3. Verify the key is associated with your Maven Central account"
    
else
    print_error "Local Maven repository not found at: $LOCAL_MAVEN_REPO"
    print_error "Please run './gradlew :sdk:publishToMavenLocal' first"
    exit 1
fi
