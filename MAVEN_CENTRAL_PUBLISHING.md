# Publishing Alterna SDK to Maven Central

This guide explains how to publish the Alterna Android SDK to Maven Central using the [Central Publisher Portal](https://central.sonatype.org/publish/publish-portal-guide/).

## Prerequisites

### 1. Sonatype Account Setup
- Create an account at [Central Portal](https://central.sonatype.org/)
- Verify your namespace ownership (com.alterna)
- Generate API tokens for authentication

### 2. GPG Signing Setup
- Install GPG on your system
- Generate a GPG key pair
- Upload your public key to a keyserver
- Configure signing in your project

### 3. Project Configuration
- All required metadata in POM files
- Proper versioning scheme
- Sources and Javadoc JARs

## Configuration

### Environment Variables
Set the following environment variables or add them to `gradle.properties`:

```bash
# Sonatype OSSRH credentials
export ossrhUsername=your_sonatype_username
export ossrhPassword=your_sonatype_password

# GPG signing configuration
export signingKeyId=your_gpg_key_id
export signingSecretKeyRingFile=path_to_your_secring_gpg
export signingPassword=your_gpg_passphrase
```

### Gradle Properties
Your `gradle.properties` should contain:

```properties
# OSSRH credentials (Maven Central)
ossrhUsername=your_sonatype_username
ossrhPassword=your_sonatype_password

# GPG signing
signing.keyId=your_gpg_key_id
signing.password=your_gpg_passphrase
signing.secretKeyRingFile=path_to_your_secring_gpg
```

## Publishing Process

### Method 1: Automated Script (Recommended)
Use the provided publishing script:

```bash
./publish-to-central.sh
```

### Method 2: Manual Gradle Commands
Execute the following commands in sequence:

```bash
# Clean and build
./gradlew clean
./gradlew :sdk:assembleRelease

# Generate artifacts
./gradlew :sdk:sourcesJar
./gradlew :sdk:javadocJar

# Publish to staging
./gradlew :sdk:publishReleasePublicationToSonatypeRepository

# Close and release
./gradlew closeAndReleaseSonatypeStagingRepository
```

## Verification

After publishing, verify your artifact is available:

1. **Staging Repository**: Check [Sonatype Nexus](https://s01.oss.sonatype.org/)
2. **Maven Central**: Search [Maven Central Search](https://central.sonatype.com/)
3. **Direct URL**: `https://repo1.maven.org/maven2/com/alterna/sdk/`

## Troubleshooting

### Common Issues

1. **Authentication Failed**
   - Verify your OSSRH credentials
   - Check API token permissions

2. **GPG Signing Issues**
   - Ensure GPG key is properly configured
   - Verify key is uploaded to keyserver

3. **Validation Errors**
   - Check POM metadata completeness
   - Ensure all required JARs are included

4. **Namespace Verification**
   - Verify domain ownership
   - Complete namespace verification process

### Validation Requirements

Maven Central requires:
- ✅ GPG-signed artifacts
- ✅ Sources JAR
- ✅ Javadoc JAR
- ✅ Complete POM metadata
- ✅ Proper licensing information
- ✅ SCM information

## Project Structure

```
sdk/
├── build.gradle          # Publishing configuration
├── src/main/
│   ├── AndroidManifest.xml
│   ├── java/com/alterna/sdk/
│   └── res/
└── consumer-rules.pro
```

## Artifact Information

- **Group ID**: `com.alterna`
- **Artifact ID**: `sdk`
- **Version**: `1.0.0`
- **Packaging**: `aar`

## Support

For issues with Maven Central publishing:
- [Central Publisher Portal Guide](https://central.sonatype.org/publish/publish-portal-guide/)
- [Sonatype Support](https://central.sonatype.org/support/)
- [Gradle Publishing Plugin Documentation](https://docs.gradle.org/current/userguide/publishing_maven.html)
