# Module structure

- Features (`auth`, `run`, `analytics`, …) are split into `:<feature>:domain`, `:<feature>:data`, `:<feature>:presentation`. Extra technical modules sit beside them (for example `:run:location`).
- Shared code lives in `:core:domain`, `:core:data`, `:core:database`, `:core:presentation:ui`, `:core:presentation:designsystem`.
- `:app` depends on every module and only wires them together: Koin setup in `RuniqueApp`, all navigation graphs in `NavigationRoot` (see navigation rule), `MainActivity`.
- Convention plugins (from `build-logic`):
  - `:domain` → `runique.jvm.library` (pure Kotlin, no Android)
  - `:data`, `:database`, `:location` → `runique.android.library` (+ `runique.android.room` for every module that uses `RunDatabase` or its DAOs, since they need Room on the classpath)
  - `:presentation` → `runique.android.feature.ui`
  - core UI → `runique.android.library.compose`
- Allowed dependencies:
  - `domain` → `:core:domain` only
  - `data` → its own `domain`, `:core:domain`, `:core:database`
  - `presentation` → its own `domain`, `:core:domain`, `:core:presentation:ui`, `:core:presentation:designsystem`
  - `presentation` never depends on `data`, and features never depend on other features.
- Add dependencies through `gradle/libs.versions.toml` and reference modules with `projects.*` accessors. Test libraries use `testImplementation`/`androidTestImplementation`, never `implementation`.
- Package root is `com.kiras.<feature>.<layer>`, also in `test`/`androidTest`.
