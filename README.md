# Essential Launcher

Essential Launcher is an Android home-screen project. Its current baseline is intentionally kept close to the imported upstream implementation; product styling, home-screen defaults, branding assets and optional system-surface companions will be customised in later updates.

## Build

GitHub Actions builds the Android package on pushes to `main` and on version tags. Required source dependencies are checked out automatically.

- App name: `Essential Launcher`
- Application ID: `com.axo.essentiallauncher`
- Version: configured in `launcher.properties`
- APK name: `EssentialLauncher<version>.apk`

For local development, initialise Git submodules, install the Android SDK components required by the project, run `python3 scripts/prepare_launcher.py`, then run the appropriate Gradle build task from the source directory.

## Third-party notices

This project includes third-party open-source software. Required copyright and licence notices are preserved in the source and summarised in [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).
