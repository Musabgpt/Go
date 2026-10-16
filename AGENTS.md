# C4 Calculator — Android Project

## Stack
- Kotlin, Jetpack Compose, Material 3, Room, Hilt (DI), Gradle Kotlin DSL
- minSdk 24, targetSdk 34, compileSdk 34
- Architecture: MVVM + Repository pattern
- Offline-first, no internet permissions

## Commands (Termux)
- Build debug APK: `./gradlew assembleDebug`
- Build release APK: `./gradlew assembleRelease`
- Run unit tests: `./gradlew test`
- Run lint: `./gradlew lintDebug`
- Clean: `./gradlew clean`

## Key paths
- Source: `app/src/main/java/com/c4/calculator/`
- Tests: `app/src/test/java/com/c4/calculator/`
- CI workflow: `.github/workflows/android-ci.yml`

## Git
- Feature branch: `feature/advanced-calculator`
- Push: `git push origin feature/advanced-calculator`
