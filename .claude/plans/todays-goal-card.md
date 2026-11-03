# Plan: "Today's goal" card and Daily goal bottom sheet

## Context

The Splitline home screen (`run_overview`) shows a header and the last run. We are adding a **Today's goal** card between `HomeHeader` and `LastRunCard`. The card shows today's progress toward a daily target (a ring and "3.2 / 5 km"), how much is left, a Mon–Sun week strip, and a streak chip. The **Edit** button opens a `ModalBottomSheet` where the user picks the metric, sets the target with a stepper or a preset chip, chooses goal days, and saves.

Designs (file `zBi5AaZMI2Aw7UaJ3Vnigl`):
- Card: node `47:14` — https://www.figma.com/design/zBi5AaZMI2Aw7UaJ3Vnigl/?node-id=47-14
- Bottom sheet: node `47:251` — https://www.figma.com/design/zBi5AaZMI2Aw7UaJ3Vnigl/?node-id=47-251

### Decisions agreed with the user
| Topic | Decision |
|---|---|
| Data source | Progress is calculated from **runs stored in Room** only. Metrics are **Distance** and **Active time** (the sum of run durations). The **Steps** segment and the **"Health Connect synced"** footer are dropped. |
| Persistence | A new Room table, one row per user. The domain interface goes in `:run:domain`; the implementation goes in a **new `:run:data` module**. |
| Week strip / streak | Based on whether the goal was met. The week runs Mon–Sun. A day the goal was met is a filled circle; today (not met yet) is a ring; a missed or upcoming goal day is an empty ring; a rest day is a small dot. The streak counts consecutive goal days on which the goal was met; rest days don't break it, and today only counts once it's met. The chip is hidden when the streak is 0. |
| No goal yet | An **empty-state card** with a "Set your daily goal" prompt that opens the sheet. |
| Stepper / presets | Distance: ±0.5 km, range 0.5–100 km, presets 3/5/8/10 km, default draft 5 km. Active time: ±5 min, range 5–300 min, presets 20/30/45/60 min, default draft 30 min. The default draft goal days are Mon–Fri. |

---

## Design analysis

### Card (`47:14`): 16dp padding, 20dp corners, `surface` (#171A1F), 12dp vertical gap
1. **Header** (space-between): "TODAY'S GOAL" (11sp, SemiBold, uppercase, 0.6sp tracking, `onSurfaceVariant`). Next to it, an **Edit** pill: `surfaceVariant` (#1F232A) background, fully rounded, 12×8dp padding, an 18dp edit icon and "Edit" (13sp, SemiBold, `primary`), with a 6dp gap.
2. **Progress row** (18dp gap):
   - **Goal ring**, 112dp: a track arc in `surfaceVariant`, a progress arc in `primary` with round caps starting at 12 o'clock, and the centred percentage (22sp, Bold, `onSurface`). It is drawn with `Canvas`, not an asset.
   - **Values column** (weight 1, 6dp gap):
     - "3.2" (28sp, Bold, `onSurface`) and "/ 5 km" (16sp, SemiBold, `onSurfaceVariant`), aligned on the baseline with a 4dp gap. For Active time, show "24 / 45 min".
     - "1.8 km to go today" (13sp, `onSurfaceVariant`). Once the goal is reached, show "Goal reached 🎉" instead (wording to confirm during implementation).
     - **Week strip**: 7 cells, 26dp wide and 22dp tall, drawn in Compose:
       - Goal met: a filled `primary` circle.
       - Today, not met: a ring with a `primary` stroke and a faint `primary` fill.
       - Missed or upcoming goal day: a ring with an `outline` stroke.
       - Rest day: a small 4dp dot in `outline`.
     - **Week labels** M T W T F S S (10sp, 26dp cells). Today's label is Bold and `primary`; the others are Regular and `onSurfaceVariant`.
3. **Footer**: a streak chip — `surfaceVariant` background, fully rounded, 10×6dp padding, "12-day streak" (12sp, SemiBold, `tertiary` (orange)). The Health Connect part is dropped, and the whole footer is hidden when the streak is 0.

Colors in the design all match existing roles in `DarkColorScheme` (`core/presentation/designsystem/.../Theme.kt`): #171A1F=`surface`, #1F232A=`surfaceVariant`, #C8F45A=`primary`, #F3F5F7=`onSurface`, #8B93A1≈`onSurfaceVariant`, #2A2F37≈`outline`, #F4A25A≈`tertiary`, #11140A≈`onPrimary`. **No new palette constants are needed.**

### Bottom sheet (`47:251`): `surface` background, 28dp top corners, padding 24dp sides / 12dp top / 28dp bottom, 20dp vertical gap
1. A drag handle (36×4dp, `outline`). Use `ModalBottomSheet`'s `dragHandle` slot with a custom handle.
2. Title "Daily goal" (22sp, Bold) and subtitle "What should count as a good day?" (13sp, `onSurfaceVariant`), with a 4dp gap.
3. **Segmented metric control**: container in `surfaceVariant`, 14dp corners, 4dp padding, 4dp gap. Each segment is 40dp tall with 10dp corners. The selected segment is `primary` with `onPrimary` text; the others are transparent with `onSurfaceVariant` text (14sp, SemiBold). There are **2 segments only** (Distance, Active time).
4. **Stepper** (space-between): 56dp square buttons with 18dp corners in `surfaceVariant` holding 22dp minus and plus icons. In the middle, the value "5.0" (56sp, ExtraBold, −1.5sp tracking) and the unit "km" (20sp, SemiBold, `onSurfaceVariant`), aligned on the baseline with a 6dp gap. Disable a button when the value hits a range limit.
5. **Preset chips** (8dp gap): the same chip style as the card. The selected chip (value equals a preset) is `primary` with `onPrimary` text; the others are `surfaceVariant` with `onSurface` text.
6. **Goal days**: a row with "Goal days" (15sp, SemiBold) and "Rest days keep your streak" (12sp, `onSurfaceVariant`). Below it, 7 toggles (40dp circles, space-between). On is `primary` with `onPrimary` text; off is `surfaceVariant` with `onSurfaceVariant` text (14sp, Bold).
7. **Actions** (10dp gap, each weight 1, 52dp tall, 16dp corners): **Cancel** (`surfaceVariant`, `onSurface`) and **Save goal** (`primary`, `onPrimary`). Save is disabled when no goal day is selected.

### Typography
The design uses Inter; the app uses **Poppins**. Keep Poppins, and per the typography rule **never set `fontSize` in composables**. Map the design sizes to roles in `core/presentation/designsystem/.../Poppins.kt`, adding the roles that are missing:

| Design | Role | Status |
|---|---|---|
| 56 ExtraBold, −1.5 tracking (stepper value) | `displayLarge` | **add** (Poppins Bold, since there is no ExtraBold font file) |
| 28 Bold (today's value) | `headlineLarge` | **add** |
| 22 Bold (sheet title, ring %) | `titleLarge` | **add** |
| 20 SemiBold (stepper unit) | `titleMedium` | **add** |
| 16 SemiBold ("/ 5 km") | `bodyLarge` + `fontWeight` override | exists |
| 15 SemiBold/Bold (Goal days, buttons) | `titleSmall` (15sp) | **add** |
| 14 SemiBold/Bold (segments, day toggles) | `bodyMedium` / `labelLarge` + weight | exists |
| 13 (subtitles, "Edit", "to go") | `labelMedium` (13sp) | **add** |
| 12 (chips, hint) | `bodySmall` + weight | exists |
| 10–11 (week labels, card title) | `labelSmall` (11sp, 0.6 tracking); use `labelSmall` for the week labels too | **add** |

### Assets
- Download from Figma (`get_design_context` asset URLs) and convert to vector drawables in `core/presentation/designsystem/src/main/res/drawable/`: **`edit.xml`** (node `47:90`), **`minus.xml`** (`47:266`), **`plus.xml`** (`47:272`). Add `EditIcon`, `MinusIcon` and `PlusIcon` to `.../designsystem/Icons.kt`, following the existing `ImageVector.vectorResource` getters.
- The ring (`47:96`), the week strip (`47:322`) and the ellipse (`47:132`) are **data-driven**, so they are drawn in Compose, not imported as assets.

---

## Implementation steps

### 1. Domain — `:run:domain` (pure Kotlin)
Create package `com.kiras.run.domain.goal`, one top-level declaration per file:
- `GoalMetric.kt`: `enum class GoalMetric { DISTANCE, ACTIVE_TIME }`
- `DailyGoal.kt`: `data class DailyGoal(val metric: GoalMetric, val target: Long, val goalDays: Set<DayOfWeek>)`. `target` is in meters for DISTANCE and seconds for ACTIVE_TIME.
- `DailyGoalRepository.kt`:
  - `fun getDailyGoal(): Flow<DailyGoal?>`
  - `suspend fun saveDailyGoal(goal: DailyGoal): EmptyResult<DataError.Local>`
- `DayGoalStatus.kt`: `enum class { MET, TODAY, MISSED, UPCOMING, REST }`
- `DailyGoalProgress.kt`: `data class(todayValue: Long, target: Long, week: List<DayGoalStatus> /* Mon..Sun */, streakDays: Int)`
- `DailyGoalProgressCalculator.kt`: a domain logic `object`, like `LocationDataCalculator`, with `fun calculate(goal: DailyGoal, runs: List<Run>, today: LocalDate, zone: ZoneId): DailyGoalProgress`.
  - Group runs by `dateTimeUtc.withZoneSameInstant(zone).toLocalDate()`. Sum `distanceMeters` or `duration.inWholeSeconds` depending on the metric.
  - Week: Monday of `today`'s week through Sunday. Each day's status:
    - REST if the day isn't in `goalDays`
    - MET if its sum ≥ target
    - TODAY if it is today
    - MISSED if it is in the past
    - UPCOMING otherwise
  - Streak: walk backward from today, skipping rest days. Today counts if met and is skipped if not. Stop at the first past goal day that wasn't met, or at the earliest run date.
  - Known limitation: past days are judged against the *current* goal; goal history is not stored.

### 2. Database — `:core:database`
- `entity/DailyGoalEntity.kt`: `@Entity(tableName = "daily_goals")` with `@PrimaryKey userId: String`, `metric: String`, `target: Long`, `goalDays: Int` (a bitmask, Mon = bit 0).
- `dao/DailyGoalDao.kt`:
  - `@Upsert suspend fun upsertDailyGoal(entity)`
  - `@Query("SELECT * FROM daily_goals WHERE userId = :userId") fun getDailyGoal(userId: String): Flow<DailyGoalEntity?>`
- `RunDatabase.kt`: add the entity, set `version = 4`, add `AutoMigration(from = 3, to = 4)`, and add `abstract val dailyGoalDao: DailyGoalDao`. Check that the exported schema `schemas/.../4.json` is generated.
- `di/CoreDatabaseModule.kt`: add `single { get<RunDatabase>().dailyGoalDao }`.

### 3. Data — new `:run:data` module
- `settings.gradle.kts`: add `include(":run:data")`.
- `run/data/build.gradle.kts`: copy `statistics/data/build.gradle.kts`. Plugins `splitline.android.library` and `splitline.android.room`, namespace `com.kiras.run.data`, dependencies coroutines, koin, `projects.core.database`, `projects.core.domain`, `projects.run.domain`.
- `mappers/DailyGoalEntityMappers.kt`: `DailyGoalEntity.toDailyGoal()` and `DailyGoal.toDailyGoalEntity(userId)`, including the bitmask ↔ `Set<DayOfWeek>` conversion.
- `DailyGoalRepositoryImpl.kt`: takes `DailyGoalDao` and `SessionStorage`.
  - `getDailyGoal()` uses the same `flow { emit(userId) }.flatMapLatest` pattern as `core/data/.../run/LocalRunRepository.kt:39`.
  - `saveDailyGoal` uses `withContext(Dispatchers.IO)`. It catches `SQLiteFullException` → `DataError.Local.DISC_FULL`, and returns `NO_ACTIVE_SESSION` when there is no user.
- `di/RunDataModule.kt`: `val runDataModule = module { singleOf(::DailyGoalRepositoryImpl).bind<DailyGoalRepository>() }`
- `app/build.gradle.kts`: add `implementation(projects.run.data)`. In `SplitlineApp.kt`, add `runDataModule` to `startKoin`.

### 4. Design system — `:core:presentation:designsystem`
- Add the icons and drawables (see **Assets**) and the typography roles (see **Typography**).
- No new shared components: everything below is used only by `run_overview`.

### 5. Presentation — `:run:presentation/.../run_overview`
- **`model/`** (one file each):
  - `DailyGoalUi.kt`: `todayValue: String`, `target: String`, `unit: UiText`, `remaining: UiText`, `progress: Float`, `percent: String`, `week: List<DayGoalStatus>`, `todayIndex: Int`, `streakDays: Int`.
  - `GoalDraftUi.kt`: `metric: GoalMetric`, `target: Long`, `goalDays: Set<DayOfWeek>`.
- **`mappers/DailyGoalMappers.kt`**: `DailyGoalProgress.toDailyGoalUi(metric)`. Number formatting goes in **`util/DailyGoalFormatters.kt`**, not in the mappers: km with 1 decimal, minutes, "to go" text, and the stepper display value.
- **`util/GoalStepperConfig.kt`**: a per-metric step, min, max, presets and default, from the decisions table.
- **`RunOverviewState`**: add
  - `dailyGoal: DailyGoalUi? = null`, where null means the empty state
  - `isGoalSheetOpen: Boolean = false`
  - `goalDraft: GoalDraftUi = GoalDraftUi(...)` with defaults
  - `isSavingGoal: Boolean = false`
- **`RunOverviewAction`**: add
  - `OnEditGoalClick`
  - `OnGoalSheetDismiss`
  - `data class OnGoalMetricChange(val metric: GoalMetric)`
  - `OnGoalIncrementClick`
  - `OnGoalDecrementClick`
  - `data class OnGoalPresetClick(val target: Long)`
  - `data class OnGoalDayToggle(val day: DayOfWeek)`
  - `OnSaveGoalClick`
- **`RunOverviewEvent.kt`** (new): `sealed interface RunOverviewEvent { data class Error(val error: UiText) }`. `RunOverviewScreenRoot` collects it with `ObserveAsEvents` and shows a Toast.
- **`RunOverviewViewModel`**: inject `DailyGoalRepository` and add the `Channel` and `events`.
  - In `init`, `combine(runRepository.getRuns(), dailyGoalRepository.getDailyGoal())`. Keep the existing runs/lastRun mapping, and set `dailyGoal = goal?.let { DailyGoalProgressCalculator.calculate(it, runs, LocalDate.now(), ZoneId.systemDefault()).toDailyGoalUi(it.metric) }`.
  - `OnEditGoalClick`: seed the draft from the saved goal (or the defaults) and open the sheet.
  - Metric change: reset the target to that metric's default.
  - Increment/decrement: `coerceIn(min, max)`.
  - Day toggle: add or remove the day.
  - Save: call the repository; on success close the sheet, on error send `Error(error.asUiText())`.
- **`di/RunPresentationModule.kt`**: no change, since `viewModelOf` resolves the new dependency.
- **`components/`** (each public composable in its own file, small private helpers next to it, as in `LastRunCard.kt`):
  - `DailyGoalCard.kt`: the card plus a private `DailyGoalEmptyContent`. The empty state keeps the same header and Edit pill, with the text "Set a daily goal to track your progress" and an `onSurfaceVariant` hint; the whole card is clickable.
  - `GoalRing.kt`: `Canvas` with `drawArc`. Optionally animate the sweep with `animateFloatAsState`.
  - `GoalWeekStrip.kt`: the circles and the labels row.
  - `GoalChip.kt`: a pill chip with `selected`. Used by the streak chip and the presets.
  - `DailyGoalBottomSheet.kt`: `ModalBottomSheet(containerColor = surface, shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp), dragHandle = …)`. On Cancel or Save, close the sheet by hiding `sheetState` first, then sending the action.
  - `GoalMetricSegmentedControl.kt`
  - `GoalStepper.kt`
  - `GoalDayToggles.kt`
  - Buttons: `SplitlineActionButton` (`core/presentation/designsystem/.../components/SplitlineActionButton.kt`) has a 100dp radius and medium weight, which doesn't match the 16dp, 52dp-tall design buttons. Use a local `Button` styled to the design inside the sheet instead of changing the shared component.
- **`RunOverviewScreen.kt`**: insert a new `item { DailyGoalCard(...) }` between the `HomeHeader` item and the last-run item. Render `DailyGoalBottomSheet` inside the root `Box` when `state.isGoalSheetOpen` is true. Update the preview with sample goal state.
- **`res/values/strings.xml`**: add
  - `todays_goal`, `edit`, `daily_goal`, `daily_goal_subtitle`, `active_time`, `goal_days`, `rest_days_keep_streak`, `cancel`, `save_goal`, `set_daily_goal`
  - `km_to_go_today` (`%1$s km to go today`), `min_to_go_today`, `goal_reached`, `day_streak` (plural `%d-day streak`), `unit_km`, `unit_min`
  - `cd_decrease`, `cd_increase`, `cd_edit_goal`
  - Weekday letters come from `DayOfWeek.getDisplayName(TextStyle.NARROW, locale)`.

### 6. Accessibility
- The Edit pill, stepper buttons and day toggles get a `contentDescription` or `Role`. Day toggles use `Modifier.toggleable(role = Role.Checkbox)`.
- Segments use `selectable(role = Role.Tab)`.
- The ring has `semantics { progressBarRangeInfo }`.
- Every touch target is at least 48dp. The 40dp day circles get a padding or `minimumInteractiveComponentSize`.

---

## Files touched (summary)
- **New module** `run/data/` (build file, `DailyGoalRepositoryImpl`, mappers, `di/RunDataModule.kt`). Also `settings.gradle.kts`, `app/build.gradle.kts`, `app/.../SplitlineApp.kt`.
- `run/domain/src/main/java/com/kiras/run/domain/goal/*` (new)
- `core/database/.../entity/DailyGoalEntity.kt`, `dao/DailyGoalDao.kt` (new); `RunDatabase.kt`, `di/CoreDatabaseModule.kt` (edited)
- `core/presentation/designsystem/.../Poppins.kt`, `Icons.kt`, `res/drawable/{edit,minus,plus}.xml`
- `run/presentation/.../run_overview/`: `RunOverviewScreen.kt`, `RunOverviewState.kt`, `RunOverviewAction.kt`, `RunOverviewViewModel.kt`, new `RunOverviewEvent.kt`, `components/*`, `model/*`, `mappers/DailyGoalMappers.kt`, `util/DailyGoalFormatters.kt`, `util/GoalStepperConfig.kt`, `res/values/strings.xml`

## Verification
1. `./gradlew assembleDebug`: everything compiles, and the Room schema `4.json` is exported and the auto-migration is generated.
2. Upgrade-install over a build with DB v3: the app opens with no migration crash, and existing runs are still there.
3. Fresh user: the empty-state card is shown → Edit → the sheet opens with 5 km and Mon–Fri → Save → the card shows "0.0 / 5 km" and 0%.
4. Complete a short run, or seed one: the ring, values, "to go" text and today's week circle update live through the Flow.
5. Stepper limits disable the −/+ buttons; tapping a preset highlights its chip; switching to Active time resets the value to 30 min and shows min units; Save is disabled when all days are toggled off.
6. Streak: seed runs on past days (for example with a debug upsert) and check that rest days don't break the streak and a missed goal day resets it.
7. Compare previews and the device against the Figma screenshots of nodes `47:14` and `47:251`. Expected differences: the Poppins font, the removed Steps segment and the removed Health Connect footer.
8. Optional (tests aren't mandatory): add `DailyGoalProgressCalculatorTest` in `run/domain/src/test`, covering week statuses, the streak across rest days, and a run near midnight in a non-UTC zone.
