# Alterna SDK Publishing Guide

This guide explains how to publish the Alterna Android SDK to various repositories for distribution.

## Prerequisites

1. **GitHub Account**: For GitHub Packages
2. **Sonatype Account**: For Maven Central (optional)
3. **GPG Key**: For signing releases (Maven Central only)

## Publishing Options

### 1. GitHub Packages (Recommended for Private/Internal Use)

GitHub Packages is ideal for private or internal distribution.

#### Setup

1. Create a GitHub Personal Access Token:
   - Go to GitHub Settings → Developer settings → Personal access tokens
   - Generate a new token with `write:packages` and `read:packages` permissions

2. Configure credentials in `gradle.properties`:
   ```properties
   gpr.user=your_github_username
   gpr.key=your_github_token
   ```

#### Publishing

```bash
# Using the publishing script
./publish.sh
# Select option 1

# Or manually
./gradlew :sdk:publishReleasePublicationToGitHubPackagesRepository
```

### 2. Maven Central (For Public Distribution)

Maven Central is the standard repository for public Android libraries.

#### Setup

1. Create a Sonatype account at https://s01.oss.sonatype.org/
2. Generate a GPG key for signing:
   ```bash
   gpg --gen-key
   gpg --list-secret-keys --keyid-format LONG
   ```
3. Upload your public key to a keyserver:
   ```bash
   gpg --keyserver hkp://pool.sks-keyservers.net --send-keys YOUR_KEY_ID
   ```

4. Configure credentials in `gradle.properties`:
   ```properties
   ossrhUsername=your_sonatype_username
   ossrhPassword=your_sonatype_password
   signing.keyId=your_gpg_key_id
   signing.password=your_gpg_password
   signing.secretKeyRingFile=path/to/your/secret.gpg
   ```

#### Publishing

```bash
# Using the publishing script
./publish.sh
# Select option 2

# Or manually
./gradlew :sdk:publishReleasePublicationToMavenCentralRepository
```

### 3. Local AAR Distribution

For direct distribution or testing.

#### Generate AAR

```bash
# Using the publishing script
./publish.sh
# Select option 3

# Or manually
./gradlew :sdk:assembleRelease
```

The AAR file will be generated at: `sdk/build/outputs/aar/sdk-release.aar`

## Version Management

### Updating Version

1. Update version in `sdk/build.gradle`:
   ```gradle
   defaultConfig {
       versionCode 2
       versionName "1.0.1"
   }
   ```

2. Update version in publishing configuration:
   ```gradle
   version = '1.0.1'
   ```

3. Update version in `publish.gradle` if using separate file

### Semantic Versioning

Follow semantic versioning (MAJOR.MINOR.PATCH):
- **MAJOR**: Breaking changes
- **MINOR**: New features, backward compatible
- **PATCH**: Bug fixes, backward compatible

## Publishing Workflow

### 1. Pre-Publishing Checklist

- [ ] Update version numbers
- [ ] Update CHANGELOG.md
- [ ] Run tests: `./gradlew test`
- [ ] Build release: `./gradlew :sdk:assembleRelease`
- [ ] Test the AAR in a sample project
- [ ] Update documentation if needed

### 2. Publishing Process

1. **Clean and Build**:
   ```bash
   ./gradlew clean
   ./gradlew :sdk:assembleRelease
   ```

2. **Publish** (choose one):
   ```bash
   # GitHub Packages
   ./gradlew :sdk:publishReleasePublicationToGitHubPackagesRepository
   
   # Maven Central
   ./gradlew :sdk:publishReleasePublicationToMavenCentralRepository
   
   # Or use the script
   ./publish.sh
   ```

3. **Verify Publication**:
   - Check the repository for your package
   - Test integration in a sample project

### 3. Post-Publishing

- [ ] Create Git tag: `git tag v1.0.0 && git push origin v1.0.0`
- [ ] Update integration guide with new version
- [ ] Announce release to users
- [ ] Monitor for issues

## Troubleshooting

### Common Issues

1. **Authentication Failed**:
   - Verify credentials in `gradle.properties`
   - Check token permissions
   - Ensure environment variables are set correctly

2. **Build Failures**:
   - Check for lint errors: `./gradlew :sdk:lint`
   - Verify all dependencies are available
   - Check ProGuard rules

3. **Publishing Failures**:
   - Verify repository URLs
   - Check network connectivity
   - Ensure version is unique

### Debug Commands

```bash
# Check build configuration
./gradlew :sdk:dependencies

# Run lint checks
./gradlew :sdk:lint

# Check publishing configuration
./gradlew :sdk:publishReleasePublicationToGitHubPackagesRepository --info

# Generate sources and javadoc
./gradlew :sdk:sourcesJar :sdk:javadocJar
```

## Integration Testing

After publishing, test the integration:

1. **Create Test Project**:
   ```gradle
   dependencies {
       implementation 'com.alterna:sdk:1.0.0'
   }
   ```

2. **Test Basic Functionality**:
   ```kotlin
   val sdk = SDK.getInstance()
   sdk.initialize(context, callback)
   sdk.startOnboarding(context)
   ```

3. **Verify ProGuard Rules**:
   - Build with ProGuard enabled
   - Test all SDK functionality

## Security Considerations

1. **Credentials**:
   - Never commit credentials to version control
   - Use environment variables or secure credential storage
   - Rotate tokens regularly

2. **Signing**:
   - Always sign releases for Maven Central
   - Keep signing keys secure
   - Use hardware security modules when possible

3. **Dependencies**:
   - Regularly update dependencies
   - Scan for vulnerabilities
   - Use dependency verification

## Automation

### CI/CD Integration

Example GitHub Actions workflow:

```yaml
name: Publish SDK

on:
  push:
    tags:
      - 'v*'

jobs:
  publish:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK
        uses: actions/setup-java@v2
        with:
          java-version: '11'
      - name: Publish to GitHub Packages
        run: ./gradlew :sdk:publishReleasePublicationToGitHubPackagesRepository
        env:
          ORG_GRADLE_PROJECT_gpr_user: ${{ secrets.GITHUB_USERNAME }}
          ORG_GRADLE_PROJECT_gpr_key: ${{ secrets.GITHUB_TOKEN }}
```

## Support

For publishing issues:
- Check the troubleshooting section above
- Review Gradle and Maven documentation
- Contact the development team

## License

This publishing guide is part of the Alterna SDK project and follows the same license terms.
