# Repository Guidelines

## Project Structure & Module Organization
- app: Android application module.
  - app/src/main/java: Kotlin sources (entry: `MainActivity.kt`).
  - app/src/main/res: Resources (drawables, mipmaps, values, etc.).
  - app/src/main/AndroidManifest.xml: App manifest.
  - app/src/test: JVM unit tests (JUnit).
  - app/src/androidTest: Instrumented tests (AndroidX Test).
  - app/proguard-rules.pro: R8/ProGuard rules for release.
- test-resources: Android library module.
  - app/src/main/java: Kotlin sources.
  - app/src/main/AndroidManifest.xml: App manifest.

## Build, Test, and Development Commands
- Build debug APK: `./gradlew :app:assembleDebug`
- Install on device/emulator: `./gradlew :app:installDebug`
- Run unit tests: `./gradlew :app:testDebugUnitTest`
- Run instrumented tests: `./gradlew :app:connectedAndroidTest` (device/emulator required)
- Lint checks: `./gradlew :app:lint` (or variant `lintDebug`)

## Coding Style & Naming Conventions
- Language: Kotlin + Jetpack Compose.
- Architecture: MVVM (Model-View-ViewModel).
- .editorconfig: spaces, indent_size=2, `end_of_line=lf`, `charset=utf-8`, trim trailing whitespace, insert final newline.
- Naming: Classes/Composables `PascalCase` (e.g., `MainActivity`, `Greeting`), functions/vars `lowerCamelCase`, constants `UPPER_SNAKE_CASE`, packages lowercase.
- Compose: Favor small, stateless composables; keep theming in `ui/theme`; avoid hardcoded strings/colors.
- Note: `.editorconfig` includes ktlint keys (e.g., allowing trailing commas, compose function naming exceptions). Add ktlint/spotless if you want automated enforcement.

## Testing Guidelines
- Frameworks: JUnit (unit), AndroidX Test + Instrumentation (device).
- Locations: Unit tests in `app/src/test`, instrumented tests in `app/src/androidTest`.
- Conventions: `ClassNameTest` with methods like `method_behavesAsExpected()`.
- Expectation: Add/maintain tests for new features and bug fixes.

## Commit & Pull Request Guidelines
- Commits: Conventional-style prefixes (`feat:`, `fix:`, `refactor:`, `test:`); keep changes focused.
- Branches: Short, purpose-driven (e.g., `feat/sms-permission-flow`).
- PRs: Clear description, linked issues (e.g., `Fixes #123`), screenshots/GIFs for UI changes; ensure build, tests, and lint pass.

## Repo Hygiene & Attributes
- .gitattributes: normalize text to LF, `.bat` as CRLF; mark images/JARs as binary.
- .gitignore: ignores Gradle, build outputs, IDE files, logs, keystores, and macOS artifacts.
- Do not commit secrets/signing keys; keep local paths in `local.properties`.
