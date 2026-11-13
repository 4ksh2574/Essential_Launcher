# AX Home

**AX Home** is an Android home-screen app built around a native launcher foundation. The product identity, application ID and release packaging are maintained independently from the underlying launcher implementation.

## Build

GitHub Actions builds the Android package on pushes to `main` and on version tags.

- App name: `AX Home`
- Application ID: `com.axo.axhome`
- Version: configured in `launcher.properties`
- APK name: `AXHome<version>.apk`

For local development, install the Android SDK components required by the project, run `python3 scripts/prepare_launcher.py`, then run the appropriate Gradle build task from `launcher-core/`.

## Third-party notices

This repository contains adapted open-source components. Required copyright, attribution and licence notices are preserved in the source and summarised in [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).
