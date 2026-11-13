# EssentialLauncher

An Android home-screen launcher project targeting Android 17 (API 37).

## Build on GitHub

Every push to `main`, pull request targeting `main`, or manual workflow run triggers the **Android APK** workflow.

1. Open the repository's **Actions** tab.
2. Select the latest **Android APK** run.
3. When it succeeds, open its **Artifacts** section.
4. Download `EssentialLauncher-debug` and extract the APK from the ZIP.

The debug APK is for testing and is not a signed Play Store release.

## Project status

This is an early launcher scaffold. It registers as an Android Home app, lists launchable apps, launches selected apps, and includes a basic searchable app drawer.

Pixel Launcher parity is the project goal, not a claim about the current build. Google-only integrations and privileged system surfaces may not be available to third-party launchers. The implementation will be expanded and tested incrementally.

## Technical baseline

- Kotlin
- Jetpack Compose
- Android Gradle Plugin
- Minimum Android version: 10 (API 29)
- Target SDK: 37
