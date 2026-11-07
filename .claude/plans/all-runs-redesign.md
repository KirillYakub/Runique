# Plan: All Runs page redesign

## Context

`AllRunsPage` still has the old look: a generic toolbar and a loose text grid with a calendar row. The new design gives each run a compact card with a map, a date header, six stat tiles and an expandable km-by-km Splits panel.

Design:
- Splits hidden: [`114:3`](https://www.figma.com/design/zBi5AaZMI2Aw7UaJ3Vnigl/Splitline-%E2%80%93-Fitness-Tracker-Concept?node-id=114-3)
- Splits open: [`114:857`](https://www.figma.com/design/zBi5AaZMI2Aw7UaJ3Vnigl/Splitline-%E2%80%93-Fitness-Tracker-Concept?node-id=114-857)

### Data check
The header, map and all six stats can be built from what `Run` already stores. **Splits can't:** `RunEntity` holds only totals, and route points are discarded when a run is saved. This plan therefore also saves splits to the database.

### Decisions agreed with the user
| Topic | Decision |
|---|---|
| Splits data | Save splits with each new run (new table, database v5 → v6) |
| Runs saved before this update | No Splits button; the card ends after the stats |
| Delete | Keep long-press → "Delete" menu; no visible control is added |
| Stat grid | Always six tiles, whatever the Pace format setting says |

### Choices to review (made in this plan, not yet agreed)
1. **Both km and mile splits are saved for every run.** They can't be recalculated later, so saving only the current unit would leave splits in the wrong unit after a settings change. This adds a few small rows per run.
2. **Several cards can be open at once.** The open state stays in the ViewModel, so cards are still open when the page is reopened.
3. **The Splits panel shows every row without scrolling**, as in Figma, so long runs make tall cards.
4. **Final partial row:** shown as in Figma (`0.42`, lighter bar), skipped if under 10 m. It can't be "fastest". Its value is the actual time (`2:16`), and its bar length uses its pace.
5. **Header subtitle:** "12 runs · 86.4 km this month" counts both figures for the current calendar month only. The list below still shows all runs.
6. **Top bar is pinned.** It no longer hides on scroll, because it's a new custom bar.

### Differences from Figma
- Poppins instead of Inter, using the theme's type roles (typography rule).
- Pace reads `5:21 / km` rather than `5:21 /km`, matching the existing shared formatter.
- Route colours in the map are a Figma mock-up. The map stays the saved snapshot image, with the app's own dots and split points.

---

## Design mapping

| Element | Figma | Theme role / value |
|---|---|---|
| Page | `#F5F6F2`-style background | `background` |
| Card | white, `#E6E9E2` 1dp border | `surface`, `surfaceContainerHighest`, 20dp corners, 16dp padding, 14dp gaps. Radius estimated from the screenshot; confirm against node `114:15` when building. |
| Stat tile | `#F5F6F2`, 14dp corners, 12dp padding, 8dp between tiles | `background` |
| Stat label | 11sp uppercase `#6B7280` | `labelSmall`, `onSurfaceVariant` |
| Stat value | 16sp Bold `#15181C` | `titleSmall` + Bold, `onSurface` |
| Run icon box | 44dp, 12dp corners, `#E6F5C0`, `#B5D46A` border | `primaryContainer`, `onPrimaryContainer` at 35% alpha (no matching role for the border) |
| Date / time | 17sp SemiBold; 13sp `#6B7280` | `titleSmall`; `labelMedium`, `onSurfaceVariant` |
| Title / subtitle | 24sp Bold; 13sp | `headlineMedium` + Bold; `labelMedium` |
| Button, closed | 46dp, 14dp corners, white, `#E6E9E2` border, 18dp chevron | `surface`, `surfaceContainerHighest`, `onSurface` |
| Button, open | `#E6F5C0`, `#B5D46A` border, `#4E7A0A` text | `primaryContainer`, border as run icon, `onPrimaryContainer` |
| Splits panel | `#F5F6F2`, 16dp corners, 14dp padding, 10dp gaps | `background` |
| Bar track | `#E3E6DE` | `surfaceContainerHighest` |
| Bar fill / fastest / partial | `#3B8FD9` / `#C8F45A` / `#9FC3E6` | `secondary` / `primary` / `secondary` at 50% alpha |
| Summary | 12sp; fastest SemiBold `#4E7A0A` | `bodySmall`; `onPrimaryContainer` + SemiBold |

**Icons:** `ArrowLeftIcon`, `KeyboardArrowDownIcon` and `KeyboardArrowUpIcon` already exist. The run icon is a navigation arrow not in `Icons.kt`, so add it from node `114:270` as a vector drawable and an `Icons.kt` entry, following the pattern there.

---

## Changes

### 1. Domain

**`core/domain/.../run/RecordedSplit.kt` (new):** `unit: DistanceUnit`, `number`, `distanceMeters: Double`, `duration`. It lives in core because `Run` does. The existing `RunSplit` in `:run:domain` stays as the live model for the active run screen.

**`core/domain/.../run/Run.kt`:** add `splits: List<RecordedSplit> = emptyList()`.

**`run/domain/.../LocationDataCalculator.kt`:** add `getRecordedSplits(locations)`. For each `DistanceUnit`, it reuses `getSplits` and adds the final partial split: leftover distance is route total minus completed splits, and its duration runs to the last timestamp. Skip it under 10 m.

### 2. Database: `:core:database`

- **`entity/RunSplitEntity.kt` (new):** `runId`, `distanceUnit`, `number`, `distanceMeters`, `durationMillis`. Composite primary key on `runId`, `distanceUnit`, `number`; foreign key to `RunEntity` with `CASCADE`; index on `runId`.
- **`entity/RunWithSplits.kt` (new):** `@Embedded` run + `@Relation` splits.
- **`RunDatabase.kt`:** add the entity, set `version = 6`, add `AutoMigration(from = 5, to = 6)`. Commit the generated `schemas/.../6.json`.
- **`dao/RunDao.kt`:** `getRunsForUser` becomes `@Transaction` and returns `RunWithSplits`. Add a `@Transaction` method that upserts the run and its splits together, so a run can't be saved without them.
- **`mappers/RunEntityMappers.kt`:** map both directions, sorted by number.
- **`RoomLocalRunDataSource.kt`:** use the new DAO methods.

Existing saves and deletes stay safe: `@Upsert` doesn't delete rows, so it won't trigger the cascade, and deleting a run removes its splits. `LocalRunRepository` needs no change, because it passes `Run` through.

### 3. Saving: `ActiveRunViewModel.finishRun`
Set `splits = LocationDataCalculator.getRecordedSplits(locations)` when building `Run`. `locations` is captured before `runningTracker.finishRun()` resets the data, so it's still available.

### 4. Shared split code in `:run:presentation`
Both screens now need the row and bar logic, and the rules forbid importing from another screen's package:
- **Move `active_run/components/SplitRow.kt` to `components/SplitRow.kt`** (feature level). It takes plain values: `label`, `barFraction`, `valueText`, `isFastest`, `isPartial`, `trackColor`, `paceFormat`. The number column grows to 28dp so `0.42` fits, and the gap becomes 10dp on both screens. Update `SplitsCard` to match.
- **New `util/SplitFormatters.kt`:** move `toSplitValueText` and `toSplitsLabelRes` here from `ActiveRunFormatters.kt`, and `toBarFraction` from `RunSplitMappers.kt`. `toSplitValueText` takes distance so partial speeds are correct.

### 5. UI model: `run_overview/`

- **`model/RunSplitRowUi.kt` (new):** `label`, `valueText`, `barFraction`, `isFastest`, `isPartial`.
- **`model/RunSplitsUi.kt` (new):** `rows`, `labelRes`, `averageText`, `fastestNumber`, `fastestValueText` (the last two nullable for runs under one full split).
- **`model/RunUi.kt`:**
  - add `dateTitle` ("Thu, 5 Nov"), `startTime` ("15:23"), `shortDuration` ("45:03" or "1:02:15"), `preciseDistance` ("8.42 km"), `averagePace` (always set) and `splits: RunSplitsUi?` (null hides the button)
  - remove `dateTime`, which nothing uses after this
  - keep `pace` as is, because `LastRunCard` depends on it
- **`mappers/RunMappers.kt`:** build these fields, filtering splits by `settings.distanceUnit`. Formatters go in `util/`, per the rules.
- **`core/presentation/ui/.../RunDataFormatters.kt`:** add a two-decimal distance formatter next to `toFormattedDistance`, using the existing `roundToDecimals` so `10.1 km` stays as in Figma.

### 6. State and actions

- **`RunOverviewState.kt`:** add `expandedRunIds: Set<String> = emptySet()`, `monthRunCount: Int = 0`, `monthDistance: String = ""`.
- **`RunOverviewAction.kt`:** add `data class OnRunSplitsToggle(val runId: String)`.
- **`RunOverviewViewModel.kt`:** handle the toggle, and calculate the month figures in the existing `combine`, using the device's time zone.

### 7. Composables: `run_overview/components/`

- **`AllRunsTopBar.kt` (new):** back `IconButton`, title and subtitle, 16dp horizontal and 8dp vertical padding, `statusBarsPadding`. It replaces `SplitlineToolbar` here, because that has no subtitle slot and a fixed title style. `SplitlineToolbar` itself is unchanged.
- **`AllRunsPage.kt`:** use the new top bar, drop `scrollBehavior` and take the new parameters. Side padding is 16dp and the gap between cards is 16dp.
- **`RunListItem.kt` (rewrite):** `MapImage` at 160dp high → header → two rows of three tiles → Splits panel → button.
  - Keep `combinedClickable` and the Delete `DropdownMenu`, and add `onLongClickLabel` for TalkBack.
  - Remove `RunningDateSection`, `DataGrid`, `RunningTimeSection` and `DataGridCell`; nothing else uses them.
  - Tiles are private composables in this file. Values can wrap to two lines instead of being cut off, and tiles in a row share a height through `IntrinsicSize.Min`.
- **`RunSplitsPanel.kt` (new):** header, `SplitRow`s in a plain `Column` (it's already inside a `LazyColumn`), then the Avg / Fastest summary.
- **`RunSplitsButton.kt` (new):** the open and closed styles, with `Role.Button`.
- **Animation:** `AnimatedContent` with a clipping `SizeTransform`, as in `SplitsCard`.

### 8. Screen: `RunOverviewScreen.kt`
Pass the new state and `OnRunSplitsToggle` into `AllRunsPage`.

### 9. Strings: `run/presentation/.../strings.xml`
- **Add:** `elevation`, `go_back`, `show_splits_button` ("Splits"), `delete_run_hint`, `run_start_and_total_time` ("%1$s · Total time %2$s"), `elevation_gain_value` ("+%1$s"), `splits_summary_average` ("Avg %1$s"), `splits_summary_fastest` ("Fastest: %1$s %2$d · %3$s"), and a plural `all_runs_month_summary`. `hide_splits` already exists.
- **Change:** `avg_speed` from "Ø Speed" to "Avg speed".
- **Remove:** `total_running_time` and `total_elevation`, which are unused after this.

### Result
```
AllRunsPage
├─ AllRunsTopBar           back · "All runs" · "12 runs · 86.4 km this month"
└─ LazyColumn of RunListItem
    ├─ MapImage
    ├─ Run icon · "Thu, 5 Nov" · "15:23 · Total time 45:03"
    ├─ TIME · DISTANCE · PACE
    ├─ AVG SPEED · MAX SPEED · ELEVATION
    ├─ RunSplitsPanel      only when open
    └─ RunSplitsButton     only when the run has splits
```

---

## Risks to check during implementation
- **Migration:** v5 → v6 runs on real user data. Test by installing over an existing build, not on a clean install.
- **Narrow screens:** on a 360dp phone a tile has about 68dp for its value, so Poppins `5:21 / km` or `10.9 km/h` may wrap. If two-line tiles look poor, switch the value to `labelLarge` + Bold.
- **`SplitRow` move:** this touches the active run screen, so recheck that its splits card still looks right.
- **Scrolling:** `AnimatedContent` changing a tall item's height inside `LazyColumn` can make the list jump. Check with the card partly off-screen.

---

## Verification
1. Build: `./gradlew :core:database:compileDebugKotlin :run:presentation:compileDebugKotlin :app:assembleDebug`, and confirm `6.json` is generated.
2. Install **over an existing build that has saved runs**, then check:
   - **Old runs:** still listed with all stats, no Splits button, no crash.
   - **New run:** after a mock GPS route of 8–9 km, the card shows full rows plus the partial row, and exactly one fastest row. The summary is correct.
   - **Layout:** compare Day theme with screenshots of `114:3` and `114:857`, then check Night theme.
   - **Header:** the subtitle counts only this month's runs, and the date and time are in local time.
   - **Toggle:** opening and closing animates, the button changes style, several cards can stay open, and the list doesn't jump.
   - **Settings:** switching km/mi changes the rows, labels and thresholds on existing runs. Pace format switches row values between time and speed, and the grid stays at six tiles.
   - **Delete:** long-press still deletes, and the run's split rows are gone from the database (check with App Inspection).
   - **Regressions:** the Last Run card is unchanged, and the active run splits card still looks right.
   - **Accessibility:** TalkBack reads the back button, tiles, Splits button and long-press action.
   - **Narrow screen:** check a 360dp device.
3. Tests are not mandatory under the testing rule. `getRecordedSplits` and the migration are the most useful ones to add if wanted.

---

## After approval
Save this plan to `Splitline/.claude/plans/all-runs-redesign.md`. Do not start implementation until the user asks for it.
