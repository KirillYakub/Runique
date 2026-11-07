# Plan: Statistics page redesign

## Context

The Statistics page is currently five plain text cards in a fixed column. Its values load once, so a new run may not appear until the page is rebuilt. The new design restyles those metrics and adds two run charts: this week's distance per day and the pace trend over 8 weeks.

Design: [`122:2` 16 Statistics — day](https://www.figma.com/design/zBi5AaZMI2Aw7UaJ3Vnigl/Splitline-%E2%80%93-Fitness-Tracker-Concept?node-id=122-2)
Night reference: [`2:110` 04 Stats](https://www.figma.com/design/zBi5AaZMI2Aw7UaJ3Vnigl/Splitline-%E2%80%93-Fitness-Tracker-Concept?node-id=2-110)

### Data check
Everything in the frame can be built from what `RunEntity` already stores (`dateTimeUtc`, `distanceMeters`, `durationMillis`, `maxSpeedKm`). **No database migration is needed.** The older frame's heart-rate zones can't be built because the app stores no heart rate, and frame 16 has dropped them anyway.

### Decisions agreed with the user
| Topic | Decision |
|---|---|
| "+14% vs last week" | This week so far vs the same Monday-to-today span last week |
| Speed setting | Card and chart follow the Pace format setting; speed users see Average speed, a km/h line and a change like `+1.2 km/h` |
| Night mode | Built now from theme colour roles, with `04 Stats` as the reference |
| Font | Keep Poppins and the nearest existing type styles; long labels may wrap |
| No runs | Full layout with zeros, flat stubs and a hint in the pace card |
| Chart interaction | No tapping; bars and line animate in when the page opens; screen reader summaries |
| Tests | Unit tests for the statistics calculator |

### Choices to review (made in this plan, not yet agreed)
1. **Custom Canvas charts, no chart library.** There are only two simple charts, and this avoids a new dependency while matching the frame exactly.
2. **Average pace changes meaning.** It becomes total time ÷ total distance, which is what the frame's numbers use (7 h 52 ÷ 86.4 km = 5:28). Today's SQL averages each run's pace, so **users will see a slightly different number after this update**. Average speed changes the same way.
3. **The two pace figures differ on purpose.** The top card (5:28) is all-time. The chart headline (5:24) is the most recent week with runs.
4. **Weekly pace** is that week's time ÷ its distance. Weeks with no runs leave a gap in their slot, and the line connects the weeks either side.
5. **Few pace points:** one week with runs shows only a dot and no change text. None shows the hint.
6. **Week pill:** hidden when the comparison span last week has no distance, since no percentage can be calculated. Zero or negative uses neutral colours instead of green.
7. **Pace change text:** a slower trend reads `12 s slower` in neutral colours, and an equal one reads `No change`.
8. **Total time** uses hours only (`152 h 10 min`, or `52 min` under an hour). The `0d 0h 0m` format goes away.
9. **Fastest run** stays the highest recorded `maxSpeedKm`, as it is today. That's a peak speed, not the fastest whole-run average, despite the label.
10. **Runs with zero distance** count towards run totals but are left out of pace.
11. **The header scrolls with the content.** The frame has no toolbar, so `SplitlineToolbar` is removed from this page.

### Differences from Figma
| Figma | App | Why |
|---|---|---|
| Inter, sizes 26/24/22 | Poppins, nearest roles (28/24/22) | Agreed; `.sp` values are only allowed in `Poppins.kt` |
| Drawn status bar | System status bar | Real device UI |
| Thursday highlighted, but a bar on Saturday | Future days are always empty stubs | The frame's sample data shows a future run |
| Card border `#E6E9E2` | `outline` (`#E1E4DC`) | Theme colours are required; the difference is barely visible |
| Fixed 348-wide charts | Fill the card width | Works on any screen; the frame's chart is also 2 px wider than its card's inner area |

## Steps

### 1. Save this plan in the project
Copy this file to `.claude/plans/statistics-redesign.md`, next to the earlier feature plans.

### 2. Domain: `:statistics:domain`
- **`RunStatsEntry.kt`** (new): `startedAt: Instant`, `distanceMeters: Int`, `duration: Duration`, `maxSpeedKmh: Double`.
- **`WeekSummary.kt`** (new): `dailyDistanceMeters: List<Int>` (Monday first), `totalMeters`, `todayIndex`, `changePercent: Int?`.
- **`StatisticsValues.kt`**: add `runCount`, `thisWeek: WeekSummary`, `weeklyPaceSecondsPerKm: List<Double?>` (8 slots, oldest first). Replace `avgPacePerRun` with nullable `avgPaceSecondsPerKm`.
- **`StatisticsCalculator.kt`** (new): `object` with `calculate(entries, today: LocalDate, zone: ZoneId): StatisticsValues`. Follow the local-date grouping and Monday week start in `run/domain/.../goal/DailyGoalProgressCalculator.kt:17-22`. Features can't import each other, so that logic is reimplemented, not shared.
- **`StatisticsRepository.kt`**: replace the one-shot call with `fun getRunEntries(): Flow<List<RunStatsEntry>>`.
- **`build.gradle.kts`**: add `testImplementation(libs.junit)`.

### 3. Data: `:core:database` and `:statistics:data`
- **`entity/RunStatsRow.kt`** (new): plain class for the four columns, not a Room entity, so the schema version stays at 6.
- **`dao/StatisticsDao.kt`**: replace the five aggregate queries (used only by this repository) with one query filtered by `userId` that returns `Flow<List<RunStatsRow>>`.
- **`mappers/RunStatsRowMappers.kt`** (new): `toRunStatsEntry()`, parsing with `Instant.parse` as `RunEntityMappers.kt:19` does.
- **`StatisticsRepositoryImpl.kt`**: emit an empty list when there's no session, otherwise the mapped DAO flow, with `flowOn(Dispatchers.IO)`.

### 4. Shared formatters: `:core:presentation:ui`
In `RunDataFormatters.kt`, make `toDistanceLabel()` and `toSpeedLabel()` public (lines 81 and 88). The cards style values and units separately, so they need the bare labels.

### 5. Presentation state: `dashboard/`
- **`StatisticsDashboardState.kt`**: add a default for every field and `isLoading = true`. This resolves the existing `.claude/refactor-todo.md` line 6 entry, so remove it.
- **`model/`** (new, one file per class):
  - `StatValueUi(value, unit)`
  - `WeekActivityUi`: total, `changePercent`, bar fractions, `todayIndex`
  - `PaceTrendUi`: headline, point fractions (`1f` = fastest), range text, change amount, direction
  - `TrendDirection`: `IMPROVED`, `WORSENED`, `UNCHANGED`
- **`mappers/StatisticsValuesMappers.kt`**: convert values to the selected unit and pace format.
  - **Bar scale:** next multiple of 3 units above the peak, minimum 3. This reproduces the frame: 8.4 km on a 9 km axis gives a 112 of 120 px bar.
  - **Change amount:** calculated from the rounded displayed values, so `6:02 → 5:24` always reads `38 s`.
- **`util/`**: feature-only value formatters for pace, speed, distance and total time. `DurationFormatters.kt` changes to the hours format.
- **`StatisticsDashboardViewModel.kt`**: `combine` `getRunEntries()` with `getSettings()` and run the calculator using `LocalDate.now(zone)`, so new runs and settings changes appear immediately. `onAction` and `Event` are still not needed because the only actions are navigation, which the Root handles.

### 6. Icons
Fetch fresh asset URLs with `get_design_context` on `122:2`, since they expire. Download the route, clock, bolt, ruler and pace SVGs and convert them to vector drawables in `:statistics:presentation` `res/drawable`, because only this feature uses them. Bottom navigation icons already exist. The frame's `Week bars` and `Pace line` SVGs are not used as assets because they contain sample data.

### 7. Components: `dashboard/components/`
Constants are written inline, as the rules require. All cards share a surface background, 1 dp `outline` border, 20 dp corners and 16 dp padding.

| Component | Notes |
|---|---|
| `StatisticsCard.kt` (rewrite) | 32 dp `primaryContainer` icon tile with 10 dp corners, uppercase label, value + unit on the baseline |
| `StatisticsSectionHeader.kt` | Label and value + unit, with a slot on the right for the pill or "Last 8 weeks" |
| `WeekDistanceCard.kt` | Header, pill, chart, and 7 equal-width day labels so they sit under the bars at any width; today is bold in `onPrimaryContainer` |
| `WeekBarChart.kt` | 120 dp Canvas with 4 grid lines; bars are ⅔ of their slot; zero days are 4 dp `surfaceContainerHighest` stubs |
| `PaceTrendCard.kt` | Header, chart, footer range and change; shows the hint when there's no data |
| `PaceLineChart.kt` | 96 dp Canvas with 3 grid lines, `secondary` line, low-alpha fill to the bottom edge, 10 dp dot on the latest point; points span 27–75% of the height as in the frame |

- **Animation:** one `Animatable` from 0 to 1 per chart, read only in the draw phase. It starts at 1 under `LocalInspectionMode` so previews aren't blank.
- **Accessibility:** each chart gets a `contentDescription` summary string.
- **Bar corner radius and line colour:** read exact values from the SVGs. The frame's line looks darker than `SplitlineBlue`. If it differs, keep `secondary` and report it instead of changing the theme.

### 8. Screen: `StatisticsDashboardScreen.kt`
Remove the toolbar. Use a `verticalScroll` `Column` with 16 dp side padding and 16 dp gaps: header, 2 + 2 + 1 stat cards with 12 dp between columns, week card, pace card. Keep `SplitlineBottomBar`, the loading spinner and the Root navigation handling.

Type roles (size always comes from the role):

| Frame | Role |
|---|---|
| Title 26 | `headlineLarge` |
| Subtitle 13 | `labelMedium` |
| Label 11 | `labelSmall` |
| Card value 24 | `headlineMedium`, Bold |
| Card unit 14 | `bodyMedium`, SemiBold |
| Chart value 22 | `titleLarge` |
| Chart unit 13 | `labelMedium`, SemiBold |
| Pill and footer 12 | `bodySmall` |
| Day label 11 | `labelSmall`, Normal weight |

Colours: page `background`, cards `surface`, primary text `onSurface`, secondary text `onSurfaceVariant`, bars `primary`, positive pill `primaryContainer` / `onPrimaryContainer`.

Previews, all with sample data: day, night, no runs, speed in miles.

### 9. Strings: `statistics/presentation/.../strings.xml`
Update labels to the frame's wording: Total distance, Total time, Fastest run, Avg distance. Add: all-time subtitle as a plural (`1 run` / `12 runs`), This week, `%1$s vs last week`, Last 8 weeks, `%1$s faster`, `%1$s slower`, No change, no-pace hint, and the chart descriptions.

### 10. Tests: `StatisticsCalculatorTest`
Package `com.kiras.statistics.domain`, JUnit4, no fakes needed because the calculator is pure:
- Totals, averages and fastest run, including an empty list
- Average pace is total time ÷ total distance, not a per-run average
- A run near midnight lands on the correct local day in a non-UTC zone
- Monday and Sunday boundaries, and today's index
- Same-days comparison ignores last week's later days; no baseline gives `null`
- Eight trend slots in order, with `null` for empty weeks and runs older than 8 weeks excluded
- Zero-distance runs count towards totals but not pace

## Verification
1. Run `./gradlew :statistics:domain:test` and `./gradlew assembleDebug`.
2. Compare the day preview against frame `122:2`: layout, spacing and colours. Check each of the 5 icons for correct file, slot and size.
3. Run the app with real runs and check by hand:
   - Finishing a run updates Statistics without restarting, including when returning to the saved tab
   - Switching km/mi and pace/speed in Settings updates every card and chart
   - Day, Night and System themes all read correctly
   - A new account shows the no-runs layout without crashes
   - The screen scrolls fully, including on a small screen and at a large font size
   - TalkBack reads the chart summaries

## Known limitation
If the app stays open past midnight, or into a new week, "today" updates only when run or settings data next changes or the screen is rebuilt. Accepted to keep this simple.

## Not in scope
- Week/Month/Year filter and heart-rate zones from `04 Stats`
- A night frame in Figma
- Sharing card or chart code with the Home screen
- Commits; I'll commit only when you ask
