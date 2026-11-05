# Plan: Tracker map redesign (Active run screen)

## Context

`ActiveRunScreen` shows a toolbar, a full-screen `TrackerMap` with a `RunDataCard` floating on top of it, and a FAB that toggles start/pause. Pausing opens a "Running is paused" dialog with Resume / Finish.

The new design is "Map B · Night parks — Live run":
- Node `85:802`: https://www.figma.com/design/zBi5AaZMI2Aw7UaJ3Vnigl/?node-id=85-802

It shows a map, chips over it, a large Distance figure, a row of stat tiles, a heart-rate zone card, and Stop / Pause / Lap controls. We adapt it so that the **map fills the screen** and all details live in an **always-visible, draggable bottom sheet** on top of it. The run can only be **started** and **stopped (stop always saves)**. There is no pause.

### Decisions agreed with the user
| Topic | Decision |
|---|---|
| Layout | The map fills the screen. Details sit in a persistent sheet (`BottomSheetScaffold`) that can't be dismissed. Collapsed, it shows Distance + the Start/Stop button. Dragged up, it also shows the stat tiles. |
| Removed from design | The Heart-rate zone card (including the "Split · last km" and "Auto-pause" row), the **Pause** button, the **Lap / Add** button, and the hint text "Hold stop to finish · Lap marks a split". |
| Stop | A tap opens a **"Finish run?" confirm dialog**. Confirming finishes and saves the run (existing snapshot → `upsertRun` flow). Cancelling closes the dialog. Tracking keeps running while the dialog is open. |
| 3rd stat tile | **Max speed** replaces HEART. It is computed live with `LocationDataCalculator.getMaxSpeedKmh`. |
| Chips | Only **"Tracking in background"** is kept. The GPS accuracy chip is dropped. |
| Toolbar | Removed. Back is handled with the system back gesture through `BackHandler`. |

---

## Design analysis (`85:802`)

The design's colors map onto existing `DarkColorScheme` roles. Use `MaterialTheme.colorScheme` only, so Day theme works automatically:

| Design | Role |
|---|---|
| #0E1013 page / sheet | `background` |
| #171A1F tiles | `surface` |
| #1F232A Stop button | `surfaceVariant` |
| #C8F45A Start button / accent | `primary` (icon `onPrimary`) |
| white values | `onSurface` |
| #8B93A1 labels, chip text | `onSurfaceVariant` |
| red stop square | `error` |

**No new palette constants are needed.**

Typography (per the typography rule, no `.sp` in composables; pick the nearest role in `Poppins.kt`):
- Section labels "DISTANCE", "TIME"… (11sp, SemiBold, uppercase, 0.6 tracking) → `labelSmall`.
- Distance value "3.86" (64sp, ExtraBold) → `displayLarge`. The unit "km" (24sp, SemiBold, `onSurfaceVariant`) → `headlineMedium`. They sit on the same baseline with a 6dp gap.
- Tile value "20:41" (22sp, Bold) → `titleLarge`.
- Chip text (12sp, SemiBold) → `labelMedium`.

Spacing:
- The sheet content has 20dp horizontal padding, 16dp top padding and 16dp gaps between sections.
- Tiles are `weight(1f)` each, with a 10dp gap, 12dp padding, 16dp corners and a 4dp gap between label and value.
- Main button: 96dp, 32dp corners. Stop button: 72dp, 24dp corners, 26dp icon.
- Chip: fully rounded, 10×6dp padding, `background` fill.

Map assets in the design (`Map base`, `Route`, street labels) are a mock-up of the live Google map and **are not downloaded**. The real `TrackerMap` + `SplitlinePolylines` already draw them. The Start/Stop icons reuse `StartIcon` / `StopIcon` from the design system.

---

## Screen structure

```
BottomSheetScaffold (sheetPeekHeight ≈ handle + Distance + controls)
├─ content: Box(fillMaxSize)
│   ├─ TrackerMap(fillMaxSize, contentPadding = bottom peek height)
│   └─ TrackingInBackgroundChip (TopEnd, statusBarsPadding, 20dp)  — when state.shouldTrack
└─ sheetContent: ActiveRunSheetContent
    ├─ DistanceHeader        "DISTANCE" + 3.86 km
    ├─ RunControlButton      Start (not started) | Stop (running)
    └─ Row of RunStatTile    TIME · PACE/SPEED · MAX SPEED
```

The controls sit **above** the tiles, unlike the design order, so Start/Stop stays visible while the sheet is collapsed.

---

## Changes

### 1. `active_run/ActiveRunAction.kt`
- Replace `OnToggleRunClick` with `OnStartRunClick` and `OnStopRunClick`.
- Replace `OnResumeRunClick` with `OnDismissFinishDialog`.
- Keep `OnFinishRunClick`, `OnCancelCountdownClick`, `OnBackRunClick`, the permission actions and `OnRunProcessed`.

### 2. `active_run/ActiveRunState.kt`
- Add `showFinishDialog: Boolean = false` and `maxSpeedKmh: Double = 0.0`.

### 3. `active_run/ActiveRunViewModel.kt`
- `OnStartRunClick`: keep the current countdown logic from the `OnToggleRunClick` branch: cancel a running countdown, start the countdown when it's configured, or otherwise set `hasStartedRunning = true, shouldTrack = true`. It does nothing when the run is already tracking.
- `OnStopRunClick`: `state = state.copy(showFinishDialog = true)`. Tracking is **not** stopped.
- `OnDismissFinishDialog`: `showFinishDialog = false`, unless `isSavingRun`.
- `OnFinishRunClick`: as today, plus `showFinishDialog` stays `true` so the dialog's Finish button shows the loading state.
- `OnBackRunClick`: if `hasStartedRunning`, show the finish dialog (instead of pausing). Otherwise do nothing; the Root navigates back.
- In the `runningTracker.runData` collector, also set `maxSpeedKmh = LocationDataCalculator.getMaxSpeedKmh(it.locations)`.
- Init: if `ActiveRunService.isServiceActive` but the tracker isn't tracking (a paused run left over from the old flow), set `shouldTrack = true` so the run resumes. There is no paused state anymore.

### 4. `active_run/ActiveRunScreen.kt`
- Replace `SplitlineScaffold` (toolbar + FAB) with Material3 `BottomSheetScaffold`:
  - `rememberBottomSheetScaffoldState(rememberStandardBottomSheetState(skipHiddenState = true))`
  - `sheetContainerColor = background`, `sheetDragHandle = { BottomSheetDefaults.DragHandle() }`, `sheetPeekHeight` measured from the collapsed content (`onGloballyPositioned`), or a fixed estimate if measuring proves fiddly.
- Add `BackHandler { onAction(ActiveRunAction.OnBackRunClick) }`. The Root keeps calling `onBack()` when the run hasn't started.
- Remove the "Running is paused" dialog. Add a finish dialog with `SplitlineDialog` when `state.showFinishDialog`:
  - Title "Finish run?" and description "Your run will be saved."
  - Primary: `SplitlineActionButton("Finish", isLoading = state.isSavingRun)` → `OnFinishRunClick`.
  - Secondary: `SplitlineOutlinedActionButton("Keep running")` → `OnDismissFinishDialog`.
  - `onDismiss` → `OnDismissFinishDialog`.
- Keep unchanged: the permission launcher and rationale dialog, the service toggle effects, `keepScreenOn`, `CountdownOverlay`, and the `onSnapshot` JPEG compression.
- Update the preview to show a running state with sample `runData`.

### 5. `active_run/maps/TrackerMap.kt`
- Add `contentPadding: PaddingValues = PaddingValues()` and pass it to `GoogleMap(contentPadding = …)`. This keeps the camera centre, the runner marker and the Google logo above the collapsed sheet.
- Snapshot behaviour (`isRunFinished`, 300dp/alpha 0) stays as is.

### 6. New local components in `active_run/components/` (one top-level composable per file, each with a private `@Preview` in `SplitlineTheme`)
- `ActiveRunSheetContent.kt`: the column described above. It takes `state`-derived values (distance, elapsed time, pace/speed text, max speed text, `hasStartedRunning`/`shouldTrack`) and `onStartClick` / `onStopClick`. It formats values with the existing formatters in `core/presentation/ui/RunDataFormatters.kt`: `toFormattedDistance`, `formatted()`, `toFormattedPaceOrSpeed`, `toFormattedSpeed`.
  - Distance needs the number and the unit as separate texts. Add a feature-local helper in `run/presentation/.../util/` if the existing `toFormattedDistance` can't be split. Don't edit mapper files.
- `RunStatTile.kt`: label + value tile (`surface`, 16dp corners).
- `RunControlButton.kt`: the 96dp `primary` Start button (`StartIcon`, `onPrimary`), and the 72dp `surfaceVariant` Stop button (`StopIcon`, `error` tint). Both get `contentDescription` from strings.
- `TrackingChip.kt`: the "Tracking in background" pill.

### 7. Remove `active_run/components/RunDataCard.kt`
Its content is replaced by the sheet. Confirm that nothing else references it before deleting.

### 8. Strings (`run/presentation/src/main/res/values/strings.xml`)
- Add: `time`, `max_speed`, `tracking_in_background`, `stop_run`, `finish_run_title`, `finish_run_description`, `keep_running`.
- Remove unused: `pause_run`, `running_is_paused`, `do_you_want_to_resume_or_finish_the_workout`, `resume`, and `active_run` if the toolbar title was its only use. Grep each before removing.

### 9. Other places to check
- `ActiveRunService`: check whether its notification has a pause action or relies on `isTracking == false`. If it does, remove it so there is no way to pause.

---

## Verification
1. `./gradlew :run:presentation:compileDebugKotlin` and `:app:assembleDebug`.
2. Run on a device or emulator (mock location route):
   - Open Active run. The map fills the screen and the sheet is collapsed with Distance + Start. Drag the sheet up: the Time / Pace / Max speed tiles appear. The sheet can't be swiped away.
   - Tap Start (with the countdown on and off). The "Tracking in background" chip appears and the stats update live, including Max speed.
   - Tap Stop. The confirm dialog appears and tracking continues. "Keep running" closes it.
   - Tap Stop → Finish. The button shows loading, the run is saved, and you land on the overview with the map snapshot.
   - Use system back before starting: you leave the screen. Use it after starting: the finish dialog appears.
   - Switch the app theme between Day and Night. The sheet, tiles, chip and map style all follow the theme.
3. Compare with the Figma screenshot of `85:802`: spacing, corner radii, colors and type roles (the heart-rate card and Pause/Lap buttons are intentionally absent).
