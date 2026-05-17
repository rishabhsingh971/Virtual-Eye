# AGENTS.md

## Cursor Cloud specific instructions

### Project overview

This is **Virtual Eye**, a native Android application (Java, Gradle) that assists visually impaired people with OCR, currency recognition, and text translation. It uses AGP 3.3.0, Gradle 4.10.1, and targets Android SDK 28 (min SDK 21).

### Environment prerequisites

- **JDK 11** is required (AGP 3.3.0 is incompatible with JDK 17+). Set `JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64`.
- **Android SDK** is installed at `/opt/android-sdk` with platform 28 and build-tools 28.0.3. The SDK command-line tools (`sdkmanager`) require JDK 21 — use `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64` only when running `sdkmanager`.
- `local.properties` must contain `sdk.dir=/opt/android-sdk` (not committed; created during setup).
- Environment variables (`JAVA_HOME`, `ANDROID_SDK_ROOT`, `ANDROID_HOME`) are configured in `~/.bashrc`.

### Build, test, and lint commands

| Task | Command |
|---|---|
| Build debug APK | `./gradlew assembleDebug` |
| Run unit tests | `./gradlew test` |
| Run lint | `./gradlew lint` |
| Clean build | `./gradlew clean` |

### Important caveats

- **Lint exits non-zero** due to 16 pre-existing lint errors in the codebase (48 total issues). The lint tool itself runs correctly; the errors are in the existing code (e.g., `MissingPermission`, `MissingSuperCall`, `ValidFragment`).
- **No emulator/device available** in the Cloud VM, so instrumented (Espresso) tests and manual app testing cannot run. Only JVM unit tests and the build can be verified.
- The Gradle wrapper (`gradlew`) auto-downloads Gradle 4.10.1 on first run.
- `jcenter()` repository is used in `build.gradle` — it is in read-only mode but still resolves existing artifacts.
