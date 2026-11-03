# Plan: Settings page

## Context
Splitline needs a Settings screen, opened from a new **Settings** tab in the bottom bar. It lives in a new module and lets the user choose distance and pace units, run-tracking behaviour (countdown, keep screen on, GPS accuracy) and the app theme (System / Night / Day). The settings have to actually take effect across the app: every distance and pace display uses the chosen units, the active-run screen runs the countdown and keeps the screen on, location tracking uses the chosen GPS accuracy, and the whole app switches between dark and light colors.

Design: https://www.figma.com/design/zBi5AaZMI2Aw7UaJ3Vnigl/?node-id=60-3 (Settings). The light palette comes from the "Day theme" frames, e.g. node `13:2` ("01 Day Home").

### Decisions agreed with the user
| Topic | Decision |
|---|---|
| Rows included | **Units:** Distance (km / mi), Pace (min/km / km/h). **Run tracking:** Countdown, Keep screen on, GPS accuracy. **Appearance:** Theme. Plus the title and a version footer ("Splitline <versionName>"). |
| Rows excluded | Auto-pause, Voice feedback, the whole "Goals & reminders" section (Daily goal, Evening reminder, Reminder time, Streak alerts). No Watch tab. |
| Storage | Room, **per user**: a `user_settings` table filtered by `userId`. |
| Units scope | **Everywhere:** active run, last run card, all runs list, statistics, and the daily goal card and sheet (miles convert; the goal is still stored in meters). |
| Pickers | Countdown and GPS accuracy open a **bottom sheet with a radio list**. Countdown: Off / 3 s / 5 s / 10 s. GPS: High / Balanced / Low. |
| Theme default | **System** for new users and on logged-out screens (intro / login / register). After login, the user's saved theme applies. |
| Defaults | km, min/km, countdown 3 s, keep screen on **off**, GPS **High**, theme **System**. |

---

## Design analysis (node `60:3`)
- **Screen:** `background`. Content padding 20dp sides, 8dp top, 28dp bottom; 24dp gap between blocks.
- **Title:** "Settings", 26sp Bold, −0.3 tracking. Use `headlineLarge` (28sp), the closest existing role.
- **Section:** an uppercase label (11sp SemiBold, 0.6 tracking, `onSurfaceVariant`, which is `labelSmall`), then a 10dp gap and a **card** (`surface`, 20dp corners).
- **Row:** padding 16×14dp, 12dp gap.
  - Left side: title (15sp SemiBold, `titleSmall`) and an optional subtitle (13sp, `labelMedium`, `onSurfaceVariant`) with a 2dp gap.
  - Rows are separated by 1dp dividers in `outline`.
- **Trailing controls:**
  - **Compact segmented control** (Distance, Pace): container `surfaceVariant`, 12dp corners, 3dp padding, 2dp gap. Options have 12×7dp padding and 9dp corners. The selected one is `primary` with `onPrimary` text; the others have `onSurfaceVariant` text (13sp SemiBold).
  - **Value + chevron** (Countdown "3 s", GPS "High"): value 14sp `onSurfaceVariant`, then a 20dp chevron with a 4dp gap. The whole row is clickable.
  - **Switch** (Keep screen on), 52×32: on = `primary` track with a dark thumb; off = `surfaceVariant` track with a grey thumb. Use M3 `Switch` with custom `SwitchDefaults.colors`.
  - **Theme row:** a column (title + subtitle "System follows your phone settings"), then a **full-width** segmented control (System / Night / Day, equal weights) with a 12dp gap.
- **Footer:** "Splitline 1.0.0", 12sp `onSurfaceVariant`, centered.
- **Assets** (download at implementation time via `get_design_context`):
  - `chevron` (node `60:47`) → `chevron_right.xml`
  - settings tab icon (`60:145`) → `settings.xml`
  - Both go in `core/presentation/designsystem/res/drawable/`, with `ChevronRightIcon` and `SettingsIcon` added to `Icons.kt`. The existing `arrow_right.xml` is a different shape, so it's not reused.

### Day (light) palette, from node `13:2`
| Role | Night (current) | Day |
|---|---|---|
| background | `#0E1013` | `#F5F6F2` |
| surface | `#171A1F` | `#FFFFFF` |
| surfaceVariant | `#1F232A` | `#ECEEE8` |
| onSurface / onBackground | `#F3F5F7` | `#15181C` |
| onSurfaceVariant | `#959697` | `#6B7280` |
| outline | `#2A3038` | `#E1E4DC` |
| primary (fills) / onPrimary | `#C8F45A` / `#0E1013` | `#C8F45A` / `#11140A` |
| primaryContainer (nav pill) | `#2C3517` | `#E6F5C0` |
| onPrimaryContainer (**accent text**) | `#C8F45A` | `#4E7A0A` |
| tertiary (streak orange) | `#F4A340` | `#C2620E` |

Accent text and icons (Edit, "See all runs", the selected bottom-bar label, links) can't stay on `primary`: lime text on white is unreadable. They move to **`onPrimaryContainer`**. In the dark theme that's the same lime, so Night looks unchanged.

---

## Implementation steps

### 1. Session flow — `:core:domain` + `:core:data`
- `SessionStorage` gains `fun observe(): Flow<AuthInfo?>`. `EncryptedSessionStorage` backs it with a `MutableStateFlow`, seeded on first read and updated in `set()`.
- Why: settings (and the theme) must switch when the user logs in or out. Right now `sessionStorage.get()` is a one-shot call.

### 2. Settings domain — `:core:domain`, package `settings/`
Settings live in core because `:run`, `:statistics` and `:app` all read them, and features can't depend on each other.
- Enums, one file each:
  - `DistanceUnit(val metersPerUnit: Double)` with `KILOMETERS(1000.0)` and `MILES(1609.344)`
  - `PaceFormat { PACE, SPEED }`
  - `CountdownDuration(val seconds: Int)` with `OFF(0)`, `THREE(3)`, `FIVE(5)`, `TEN(10)`
  - `GpsAccuracy { HIGH, BALANCED, LOW }`
  - `AppTheme { SYSTEM, NIGHT, DAY }`
- `UserSettings` data class, with every default from the decisions table.
- `SettingsRepository`:
  - `fun getSettings(): Flow<UserSettings>` — emits defaults when nobody is logged in
  - `suspend fun saveSettings(settings: UserSettings): EmptyResult<DataError.Local>`

### 3. Database — `:core:database`
- `entity/UserSettingsEntity.kt`: `user_settings` table. `userId` is the primary key, with a foreign key to `users` (cascade delete). Columns: `distanceUnit`, `paceFormat`, `gpsAccuracy` and `theme` as `String`; `countdownSeconds: Int`; `keepScreenOn: Boolean`.
- `dao/UserSettingsDao.kt`:
  - `@Upsert`
  - `getSettings(userId): Flow<UserSettingsEntity?>`
- `RunDatabase`: add the entity, set `version = 5`, add `AutoMigration(4, 5)`, add `userSettingsDao`. Provide the DAO in `coreDatabaseModule`.
- `SettingsRepositoryImpl`, in `:core:database` next to `UserRepositoryImpl`:
  - `getSettings()`: `sessionStorage.observe().map { it?.userId }.distinctUntilChanged().flatMapLatest { … }`, mapped with `?: UserSettings()`.
  - `saveSettings`: `withContext(IO)`, catching `SQLiteFullException`.
  - Mappers go in `mappers/UserSettingsEntityMappers.kt`.
  - Bind it in `coreDatabaseModule` with `singleOf(::SettingsRepositoryImpl).bind<SettingsRepository>()`.

### 4. Theme — `:core:presentation:designsystem` + `:app`
- `Color.kt`: add the Day palette constants (`SplitlineDay*`).
- `Theme.kt`: add `LightColorScheme`. Set `onPrimaryContainer` in both schemes.
- `SplitlineTheme(isDarkTheme: Boolean = isSystemInDarkTheme(), content)` picks the scheme and sets `isAppearanceLightStatusBars = !isDarkTheme`.
- Make components theme-safe:
  - `Poppins.kt`: remove the hardcoded `color` from `bodySmall` (`SplitlineGray`) and `headlineMedium` (`SplitlineWhite`).
  - `SplitlineActionButton` and `SplitlineToolbar`: replace direct palette constants with scheme roles.
  - Audit every `colorScheme.primary` used as **text or icon tint** and switch it to `onPrimaryContainer`. Fills stay `primary`. Files include `SplitlineBottomBar`, `LastRunCard`, `RunListItem`, `DailyGoalCard`, `GoalWeekStrip`, `LoginScreen`, `RegisterScreen`, `PasswordRequirement` and the text fields' cursor colors.
  - `PolylineColorCalculator` (speed colors on the map) stays as is.
- `:app`:
  - `MainViewModel` injects `SettingsRepository` and exposes `theme: AppTheme` in `MainState`.
  - `MainActivity` calls `SplitlineTheme(isDarkTheme = when(theme) { SYSTEM -> isSystemInDarkTheme(); NIGHT -> true; DAY -> false })`.
- **Known gaps, not in scope:**
  - Day cards in Figma have a 1dp `#E6E9E2` border.
  - The Google map style on the active-run screen stays dark.
  - Both can follow later.

### 5. Units everywhere — `:core:presentation:ui` + call sites
- `RunDataFormatters.kt` gets unit-aware formatters that replace the km-only ones:
  - `Int.toFormattedDistance(unit)` (meters in)
  - `Duration.toFormattedPace(distanceMeters, unit)` → `"5:21 / km"` or `"8:36 / mi"`
  - `Double.toFormattedSpeed(unit)` (km/h in) → `"km/h"` or `"mph"`
  - `toFormattedPaceOrSpeed(…, paceFormat)` returns pace or speed
  - The unit strings go in `:core:presentation:ui` `strings.xml`.
- Call sites receive `UserSettings` from their ViewModel, each of which injects `SettingsRepository` and combines it with its existing flow:
  - **Run overview:** `RunOverviewViewModel` → `RunMappers.toRunUi(settings)`, used by `LastRunCard` and `RunListItem`. When `paceFormat = SPEED`, the "Pace" label becomes "Speed".
  - **Active run:** `ActiveRunViewModel` → `RunDataCard` takes the unit and pace format.
  - **Statistics:** `StatisticsDashboardViewModel` → `StatisticsValuesMappers`. Total and average distance, fastest run speed, and average pace (convert min/km → min/mi).
  - **Daily goal** (`run/presentation/util/DailyGoalFormatters.kt` and `GoalStepperConfig.kt`):
    - The card, remaining text and sheet show mi when selected.
    - `toStepperConfig(metric, distanceUnit)` gives mile presets 2/3/5/6 mi, step 0.5 mi, range 0.5–60 mi.
    - Stepping snaps to the nearest 0.5 in the display unit, then converts back to meters. The goal is still stored in meters.
    - `DailyGoalCard.kt` and `RunOverviewScreen.kt` were changed on disk recently, so re-read them before editing.

### 6. Run tracking behaviour — `:run`
- **GPS accuracy:**
  - `LocationObserver.observeLocation(interval, accuracy: GpsAccuracy)`.
  - `AndroidLocationObserver` maps HIGH → `PRIORITY_HIGH_ACCURACY`, 1 s; BALANCED → `PRIORITY_BALANCED_POWER_ACCURACY`, 2 s; LOW → `PRIORITY_LOW_POWER`, 5 s.
  - `RunningTracker` injects `SettingsRepository` and `flatMapLatest`s on `gpsAccuracy` when observing location.
- **Keep screen on:**
  - `ActiveRunState.keepScreenOn` comes from settings.
  - `ActiveRunScreen` uses `DisposableEffect(state.keepScreenOn) { view.keepScreenOn = … ; onDispose { view.keepScreenOn = false } }`.
- **Countdown:**
  - `ActiveRunState.countdownRemaining: Int? = null`.
  - On the **first** `OnToggleRunClick` (when `!hasStartedRunning`) and countdown ≠ OFF, the ViewModel ticks the value down once per second and then starts tracking as today. Resuming after a pause has no countdown.
  - New `components/CountdownOverlay.kt`: a full-screen scrim with a big number (`displayLarge`).
  - New action `OnCancelCountdownClick`, triggered by Back or a tap on the overlay.

### 7. Settings module — `:settings:presentation` (new)
- **Gradle:**
  - `settings.gradle.kts`: `include(":settings:presentation")`.
  - Build file copied from `statistics/presentation/build.gradle.kts` (`splitline.android.feature.ui`, serialization, navigation compose).
  - Namespace `com.kiras.settings.presentation`.
  - `app/build.gradle.kts`: add `implementation(projects.settings.presentation)`.
- **Routes:** `util/Routes.kt` → `@Serializable data object SettingsRoute`.
- **Screen** (MVI, package `settings/`):
  - `SettingsState`: `settings: UserSettings`, `versionName: String`, `openPicker: SettingsPicker?`. `SettingsPicker { COUNTDOWN, GPS_ACCURACY }` gets its own file.
  - `SettingsAction`:
    - `OnDistanceUnitChange`, `OnPaceFormatChange`, `OnKeepScreenOnToggle`, `OnThemeChange`
    - `OnCountdownClick`, `OnGpsAccuracyClick`, `OnCountdownSelect`, `OnGpsAccuracySelect`, `OnPickerDismiss`
    - `OnHomeClick`, `OnStatisticsClick`
  - `SettingsEvent.Error(UiText)`, shown as a Toast.
  - `SettingsViewModel` collects `getSettings()`. Each change calls `saveSettings(settings.copy(…))`.
  - `SettingsScreenRoot(onHomeClick, onStatisticsClick)` reads `versionName` from `PackageManager` via `LocalContext`.
- **Components** (`settings/components/`, local to this feature):
  - `SettingsSection` (label + card with dividers)
  - `SettingsRow` (title, subtitle, trailing slot)
  - `SettingsSegmentedControl` (compact or `fillMaxWidth`). It's different from `GoalMetricSegmentedControl` (3dp vs 4dp padding, compact sizing), so it stays local.
  - `SettingsValue` (value + chevron)
  - `SettingsSwitch`
  - `SettingsOptionSheet<T>` (`ModalBottomSheet` + radio list, same sheet styling as `DailyGoalBottomSheet`)
- **DI:** `di/SettingsPresentationModule.kt` with `viewModelOf(::SettingsViewModel)`, registered in `SplitlineApp`.
- **Strings:** section labels, row titles and subtitles, option labels (Off / 3 s / 5 s / 10 s, High / Balanced / Low, System / Night / Day, km / mi, min/km / km/h), `version_footer` = "Splitline %1$s".

### 8. Bottom bar + navigation
- `BottomBarTab` gains `SETTINGS`. `SplitlineBottomBar` gets a third weighted item: `SettingsIcon`, with the label `R.string.settings` in the design-system strings.
- `RunOverviewScreen`, `StatisticsDashboardScreen` and `SettingsScreen` each handle all three tabs. New actions are `OnSettingsClick` (run overview, statistics) and `OnStatisticsClick` / `OnHomeClick` (settings). Each `XScreenRoot` gets an `onSettingsClick` lambda.
- `NavigationRoot`: `composable<SettingsRoute> { SettingsScreenRoot(onHomeClick = { navigateToTab(RunOverviewRoute) }, onStatisticsClick = { navigateToTab(StatisticsDashboardRoute) }) }`. Wire `onSettingsClick = { navHostController.navigateToTab(SettingsRoute) }` for Home and Stats.

---

## Files touched (summary)
- **New module:** `settings/presentation/` (build file, `util/Routes.kt`, `settings/*`, `settings/components/*`, `di/`, `res/values/strings.xml`). Plus `settings.gradle.kts`, `app/build.gradle.kts`, `SplitlineApp.kt`, `NavigationRoot.kt`, `MainActivity.kt`, `MainViewModel.kt`, `MainState.kt`.
- `core/domain/.../settings/*` (new); `SessionStorage.kt`.
- `core/data/.../auth/EncryptedSessionStorage.kt`.
- `core/database/`: `entity/UserSettingsEntity.kt`, `dao/UserSettingsDao.kt`, `SettingsRepositoryImpl.kt`, `mappers/UserSettingsEntityMappers.kt` (new); `RunDatabase.kt`, `di/CoreDatabaseModule.kt`.
- `core/presentation/designsystem/`: `Color.kt`, `Theme.kt`, `Poppins.kt`, `Icons.kt`, `res/drawable/{settings,chevron_right}.xml`, `components/{SplitlineBottomBar,SplitlineActionButton,SplitlineToolbar}.kt`, `components/util/BottomBarTab.kt`, strings.
- `core/presentation/ui/RunDataFormatters.kt` + strings.
- `run/domain/{LocationObserver,RunningTracker}.kt`; `run/location/AndroidLocationObserver.kt`.
- `run/presentation/`: `active_run/*` (state, action, VM, screen, `components/RunDataCard.kt`, new `CountdownOverlay.kt`); `run_overview/*` (VM, screen, `mappers/RunMappers.kt`, `components/{LastRunCard,RunListItem,DailyGoalCard,DailyGoalBottomSheet,GoalWeekStrip}.kt`); `util/{DailyGoalFormatters,GoalStepperConfig}.kt`.
- `statistics/presentation/`: `StatisticsDashboardScreen.kt`, `StatisticsDashboardViewModel.kt`, `StatisticsDashboardAction.kt`, `mappers/StatisticsValuesMappers.kt`.
- `auth/presentation/`: accent color fixes in `LoginScreen`, `RegisterScreen`, `PasswordRequirement`.

## Verification
1. `./gradlew assembleDebug`: the build succeeds, `schemas/.../5.json` is exported, and the 4 → 5 auto-migration is generated.
2. Upgrade-install over the current emulator build (DB v4, test user `goaltest@example.com`). The app starts, the saved daily goal is still there, and Settings shows the defaults.
3. **Navigation:** Home → Settings → Stats → Home through the bottom bar. The selected tab highlights correctly, and Back from a tab follows the existing `navigateToTab` behaviour.
4. **Units:**
   - Switch to mi: the last run, all runs, statistics, the daily goal card ("3.1 / x mi") and the sheet (mile presets, 0.5 mi steps) all update immediately.
   - Switch pace to km/h: "Pace" becomes "Speed" with km/h (mph with miles).
5. **Run tracking:**
   - Countdown 3 s: Start Run shows 3-2-1, then tracking starts. Pause and resume: no countdown. Countdown Off: starts immediately.
   - Keep screen on: with it on, the active-run screen doesn't dim past the device timeout. With it off, it does.
   - GPS Low/High: check with logcat or the debugger that the `LocationRequest` priority and interval change.
6. **Theme:**
   - Day: every screen (home, stats, settings, active run, sheets, auth) uses the light palette and stays readable. Accent text is dark green and status-bar icons are dark.
   - System follows the emulator's dark-mode toggle.
   - Logging out shows the auth screens in System.
7. **Per user:** register a second account. It starts with the defaults, and the first account's settings are unaffected.
8. Compare the Settings screen at 1.0× and 2.0× font scale against Figma node `60:3`. Expected differences: the Poppins font and the excluded rows.
