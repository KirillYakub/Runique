# Plan: Splits in the active run sheet

## Context

During a run, the user can only see totals: distance, time, average pace and max speed. They can't see how each kilometre went or where on the route it ended.

This feature adds:
- a collapsible **Splits** card in `ActiveRunSheetContent`, with one bar per completed km
- a numbered **split point on the Google Map** at the end of each km

Design: Figma node `85:2047` ([link](https://www.figma.com/design/zBi5AaZMI2Aw7UaJ3Vnigl/Splitline-%E2%80%93-Fitness-Tracker-Concept?node-id=85-2047)).

### Decisions agreed with the user
| Topic | Decision |
|---|---|
| Order in the sheet | Distance → `RunStatTile` row → Splits card → run button |
| Collapsed sheet | The peek height covers the whole column, so the button is always visible. Toggling splits resizes the sheet. |
| Run button | Keep the existing Start/Stop button (Stop opens "Finish run?"). No pause is added, and the button only moves. |
| Default state | Splits card is **collapsed** when the screen opens |
| Rows shown | **Completed** splits only, with an empty-state hint before the first one |
| Distance unit | Follows the setting: a split every km or every mile, header `min/km` or `min/mi` |
| Pace format | Follows the setting: `5:34` under `min/km`, or `11.2 km/h` under `km/h` (`mph` for miles) |
| Map point | Numbered dot that matches the row number |
| Map snapshot | Split points **are included** in the saved snapshot |

### Choices to review (made in this plan, not yet agreed)
1. **Drag handle removed and swipe disabled.** With everything in the peek height, there is nothing left to drag up. The toggle is the only thing that resizes the sheet.
2. **At most about 5 rows visible.** Longer runs scroll inside the card and jump to the newest split, so the button stays on screen.
3. **Bar length formula.** The Figma values are illustrative and don't follow a plain ratio (fastest 5:08 is 100%, 5:34 is 78%). This plan uses `1 - 2.5 × (time - fastest) / fastest`, with a minimum of 15%. That gives 79% for 5:34, which is close to the design and makes differences visible.
4. **Only one fastest split.** If two splits tie, the earliest one is highlighted.

### Out of scope
Splits are not saved with the run or shown in run history. They exist only while the run is active, apart from the points in the map snapshot.

---

## Design mapping (`85:2047`)

Colours come from `MaterialTheme.colorScheme`, so no new palette constants are needed and Night theme works automatically.

| Design | Value | Role |
|---|---|---|
| Card background | `#FFFFFF` | `surface` |
| Card border | `#E6E9E2`, 1dp | `surfaceContainerHighest` |
| Bar track | `#ECEEE8` | `surfaceVariant` |
| Bar fill | `#3B8FD9` | `secondary` (`#5AB0F4`, the closest role, slightly lighter than the design) |
| Fastest bar | `#C8F45A` | `primary` |
| Title, split value | `#15181C` | `onSurface` |
| Split number, unit label | `#6B7280` | `onSurfaceVariant` |
| Fastest value (bold) | `#4E7A0A` | `onPrimaryContainer` |

**Typography.** The design uses Inter, but the app theme is Poppins. Per the typography rule there is no `.sp` in composables:
- Title (17sp SemiBold) → `titleSmall`, the nearest role at 15sp.
- Number, unit label and value (13sp) → `labelMedium`, with `fontWeight` overridden to Normal, or Bold for the fastest value.

**Layout.**
- Card: 20dp corners, 16dp horizontal padding, 10dp gap between rows.
- Row: 12dp gaps, number column, bar with `weight(1f)` instead of the design's fixed 250px, then the value.
- Bar: 10dp high and fully rounded.
- The number column must be wider than the design's 14dp so `10`+ fits. The value column has a minimum width, right-aligned, so bars line up.

**Assets.** The Figma SVGs are just mock-ups of bars with fixed widths, so they aren't downloaded. Bars are drawn in Compose from live data.

**Additions not in the design:** the expand/collapse chevron, using the existing `KeyboardArrowDownIcon` / `KeyboardArrowUpIcon`, and the empty-state hint.

---

## Changes

### Domain: `:run:domain`

**New `RunSplit.kt`:** data class with `number`, `duration` and `endLocation` (`Location`).

**`LocationDataCalculator.kt`:** add `getSplits(locations, metersPerSplit)`, returning `List<RunSplit>`.
- Walk each segment's consecutive points, accumulating distance with `Location.distanceTo`. As in `getTotalDistanceMeters`, no distance is counted between segments.
- When a pair of points crosses a split boundary, interpolate the exact position and timestamp along that pair, so the map point sits where the km actually ended and not at the next GPS fix.
- One pair can cross more than one boundary after a GPS jump, so handle that case with a loop.
- Each split's duration is its boundary timestamp minus the previous one. The first split starts from the first recorded timestamp.
- The method takes a plain `Double` so the domain stays independent of formatting. Callers pass `distanceUnit.metersPerUnit`.

### Presentation state: `active_run/`

**`model/RunSplitUi.kt` (new):** `number`, `valueText`, `barFraction`, `isFastest`, `endLocation`.

**`mappers/RunSplitMappers.kt` (new):** `List<RunSplit>.toRunSplitUis(unit, paceFormat)`, which finds the fastest split and calculates `barFraction` using the formula above. The formula's values stay inline, per the constants rule.

**`util/ActiveRunFormatters.kt`:** add
- a split value formatter. Pace needs a bare `m:ss`, because the existing `toFormattedPacePerUnit` adds `/ km`. Speed reuses `toFormattedAvgSpeed`.
- a header label function returning the string resource for the unit and pace format.

**`ActiveRunState.kt`:** add `splits: List<RunSplitUi> = emptyList()` and `isSplitsExpanded: Boolean = false`.

**`ActiveRunAction.kt`:** add `data object OnSplitsToggle`.

**`ActiveRunViewModel.kt`:**
- Handle `OnSplitsToggle`.
- Rebuild `splits` in the `runData` collector, next to `maxSpeedKmh`.
- Also rebuild in the settings collector, because changing the unit or format must update existing splits and settings can arrive after run data.
- `finishRun()` already resets `RunData`, so splits clear on their own.

### UI: `active_run/components/`

**`SplitsCard.kt` (new):** card with the header and the expandable content.
- **Header:** title, unit label and chevron. The whole row is clickable with `Role.Button`, has a minimum height of 48dp, and has a show/hide `contentDescription` for accessibility.
- **Animation:** `AnimatedContent` keyed on `isSplitsExpanded`, with a fade and a clipping `SizeTransform`, so the card height animates. Also add `animateContentSize` on the list, because `AnimatedContent` only runs on toggle and a newly added row would otherwise make the height jump.
- **List:** `LazyColumn` keyed by split number, with a capped height of about 5 rows. Hoist the list state above `AnimatedContent` so it survives toggling, and scroll to the newest item when `splits.size` changes.
- **Empty state:** "Your first split appears after 1 km" (or "1 mi").
- Private `@Preview`s for collapsed, expanded and empty.

**`SplitRow.kt` (new):** number, bar and value. Merge semantics so TalkBack reads a row as one item. Private `@Preview`.

**`ActiveRunSheetContent.kt`:**
- Reorder to Distance → tiles → `SplitsCard` → `RunControlButton`.
- Add parameters `splits`, `isSplitsExpanded` and `onSplitsToggle`.
- Remove the inner peek `Column` and move `onSizeChanged` to the outer `Column`, so the measurement includes everything.
- Add top padding to replace the removed drag handle.
- Update the preview with sample splits.

### Screen: `ActiveRunScreen.kt`
- Pass the new state and action into the sheet content.
- Set `sheetPeekHeight` directly from the measured height. Remove the `44.dp + navigationBarHeight` addition, because the outer column already includes the navigation bar padding. Keeping it would leave a gap.
- Set `sheetSwipeEnabled = false` and `sheetDragHandle = null`.
- Pass `state.splits` to `TrackerMap` and update the preview.

### Map: `active_run/maps/`

**`SplitMarker.kt` (new):** about 22dp circle with the number in `labelSmall`, fill `background`, 2dp `secondary` border. Size and contrast must keep it distinct from the 16dp start and runner dots. Private `@Preview`.

**`TrackerMap.kt`:**
- Add a `splits` parameter.
- Inside `GoogleMap`, loop over splits with `key(split.number)`, using `rememberUpdatedMarkerState` and a `MarkerComposable` keyed on number and colours, as the existing markers do, with a centred anchor.
- Add before the runner marker, or set `zIndex`, so the runner stays on top.
- Do **not** gate on `isRunFinished`, so the points appear in the snapshot as agreed.

### Strings: `run/presentation/src/main/res/values/strings.xml`
Add: `splits`, the header labels (`min/km`, `min/mi`, `km/h`, `mph`), `show_splits`, `hide_splits`, and `splits_empty` with a unit argument.

### Result
```
BottomSheetScaffold (peek = full measured column, no handle, no swipe)
├─ content: TrackerMap + numbered split points
└─ sheetContent: ActiveRunSheetContent
    ├─ Distance header
    ├─ RunStatTile row
    ├─ SplitsCard (header + chevron, AnimatedContent list)
    └─ RunControlButton
```

---

## Risks to check during implementation
- **Map jitter:** `sheetPeekHeight` also drives the map's `contentPadding`, so padding changes on every frame of the animation. If the map stutters, update the map padding only when the animation ends.
- **Small screens:** an expanded sheet is about 500dp tall. On short screens, in landscape or at large font sizes, the button could be cut off. If so, make the sheet column scrollable.
- **Rounding:** `getTotalDistanceMeters` rounds per segment, so the first split can appear just before or after the display reads `1.00`. This is a difference of about a metre and is acceptable.

---

## Verification
1. Build: `./gradlew :run:domain:compileKotlin :run:presentation:compileDebugKotlin :app:assembleDebug`.
2. Run on an emulator with a mock GPS route longer than 6 km and check:
   - **Layout:** the order is Distance → tiles → Splits → button, and the button is visible when collapsed and expanded.
   - **Toggle:** the card starts collapsed, the chevron animates it open and closed, and the map stays stable.
   - **Empty state:** the hint shows before the first km.
   - **Data:** a row and a numbered map point appear at each km, the numbers match, and the point sits on the route at the boundary.
   - **Long runs:** after the 6th split the list scrolls to the newest row, and the sheet doesn't grow.
   - **Fastest:** exactly one row is highlighted, and it moves when a faster split finishes.
   - **Settings:** miles and speed format update the rows, labels, thresholds and map points.
   - **Stop and start:** finishing the run saves it, and split points appear in the snapshot. A new run starts with no splits.
   - **Background:** leave the screen during a run and return. Splits made while away are shown, because they come from `RunningTracker` data.
   - **Themes:** check Day and Night against the Figma screenshot.
   - **Accessibility:** TalkBack reads the toggle and rows properly.
3. Tests are not mandatory under the testing rule. `getSplits` is pure and easy to unit test if wanted (boundaries, multiple segments, GPS jumps).

---

## After approval
Save this plan to `Splitline/.claude/plans/active-run-splits.md`, next to the other feature plans. Do not start implementation until the user asks for it.
