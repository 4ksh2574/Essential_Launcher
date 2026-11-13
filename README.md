# Essential Launcher

Essential Launcher is built on the **Lawnchair 16** launcher engine. The small Compose prototype previously in this repository has been retired; the project now tracks the upstream launcher source as a pinned Git submodule so upstream's Android 16 launcher, app drawer, settings, widget support, and Quickstep integration can be customised without carrying a second launcher implementation.

## Source and attribution

- Upstream project: [Lawnchair](https://github.com/LawnchairLauncher/lawnchair)
- Pinned source revision: [`222ddef`](https://github.com/LawnchairLauncher/lawnchair/commit/222ddef8094081ff590b3553ac71cb8ac318c663)
- License: Apache License 2.0. The upstream license and copyright notices are retained in `launcher-core/LICENSE.txt`.
- The `launcher-core` directory is a Git submodule. Initialise it with `git submodule update --init --recursive` when cloning locally.

Essential Launcher keeps the upstream implementation and package namespaces intact where they are needed for compatibility. The build overlay changes the installed app identity and user-facing launcher name; it does not remove upstream copyright or license notices.

## Build

GitHub Actions builds the APK on pushes to `main` and on version tags. The pinned upstream revision and its required SystemUI submodule are checked out automatically.

- App name: `Essential Launcher`
- Application ID: `com.axo.essentiallauncher`
- Version: configured in `launcher.properties`
- APK name: `EssentialLauncher<version>.apk`

For a local build, initialise submodules, install Android SDK components required by the pinned Lawnchair source, run `python3 scripts/prepare_launcher.py`, then run `./gradlew assembleLawnWithQuickstepGithubDebug` from `launcher-core`.

## Customisation

The first import intentionally keeps the upstream launcher behaviour and layout close to stock Lawnchair 16. Product styling, home-screen defaults, branding assets, optional system-surface companions, and other Essential-specific changes will be layered on top in later commits.
