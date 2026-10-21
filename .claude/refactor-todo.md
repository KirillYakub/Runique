# Refactor TODO

Code that does not match `.claude/rules` yet. Each item is a separate task, done on request.

- **Use cases:** add them for `auth`, `run`, `analytics`, and change the ViewModels to use them.
- **Renames:**
  - `AuthRepositoryImpl` → `RoomAuthRepository`
  - `LocalRunRepository` → technology-based name
  - `RunOverviewActions` → `RunOverviewAction`
  - `AnalyticsAction.onBackClick` → `OnBackClick`
  - `AnalyticsDashboardScreenRoot.kt` → `AnalyticsDashboardScreen.kt`
- **Mappers:**
  - `mappers/RunEntity.kt` → `mappers/RunEntityMappers.kt`
  - `run_overview/mapper/RunMapper.kt` → `mappers/RunMappers.kt`
  - `AnalyticsDashboardMapper.kt` → `mappers/`, with `toFormattedTotalTime` moved to `util/`
  - `AuthInfoMapper.kt`, `LocationMappers.kt` → `mappers/`
- **Visibility:** make implementations, mappers, models and local components `internal`, and make `XScreen` and previews `private`.
- **Components:**
  - `PasswordRequirement` (RegisterScreen.kt) → `register/components/`
  - `RuniqueLogoVertical` (IntroScreen.kt) → `intro/components/`
- **Shared helper:** move `textAsFlow`, which LoginViewModel imports from `register`, to a shared util.
- **DI:**
  - Merge `runViewModelModule` + `activeRunViewModelModule` → `runPresentationModule`.
  - Rename the modules to the `<feature><Layer>Module` pattern.
  - Use a single `koinViewModel` import.
- **Navigation:** migrate `NavigationRoot` string routes to `@Serializable` routes and per-feature graphs.
- **Strings:** create `auth/presentation` `strings.xml` and move the auth strings out of designsystem.
- **Tests:** move `FakeUserDao`/`FakeSessionStorage` out of `AuthRepositoryImplTest` into a `fakes/` package.
- **Build:**
  - Remove the `runique.android.room` plugin from `:analytics:data`.
  - Check whether `:run:location` needs Compose.
  - `:run:data` is empty; ask before deleting it.
- **Test packages:** `com.kiras.data` → `com.kiras.auth.data` in `auth/data` test/androidTest.
