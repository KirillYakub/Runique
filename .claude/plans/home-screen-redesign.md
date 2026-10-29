# Home screen redesign (`run_overview` → Figma "01 Home")

Figma: https://www.figma.com/design/zBi5AaZMI2Aw7UaJ3Vnigl/Splitline-%E2%80%93-Fitness-Tracker-Concept?node-id=1-2

Work goes step by step: implement one step, then stop and wait for the next command.

## Context
`RunOverviewScreen` (`run/presentation/.../run_overview/`) is the app's home screen. Right now it is a
`SplitlineToolbar` (logo + Analytics/Logout menu), a `LazyColumn` of `RunListItem`s and a round
`SplitlineFloatingActionButton`. We redesign it to match the Figma Home frame. Before implementing,
pull exact colors, typography and spacing with `get_design_context` on these nodes:
header `1:7`, Last run `1:28`, Start run `1:81`.

Out of scope: the `Weekly goal` card (`1:13`), the `Coach summary` card (`1:53`) and the bottom navigation (`1:59`).

## Step 1: Header with date, username and account button
Figma node `1:7`: a small date line ("Tuesday, 21 Oct"), a large greeting ("Good morning, Kyrylo")
and a 44dp circular avatar with the user's initial.

1.1 **Store the username (database)**
- `UserEntity`: add a `username: String` column.
- `RunDatabase`: bump `version` from 2 to 3 and add `Migration(2, 3)`
  (`ALTER TABLE users ADD COLUMN username TEXT NOT NULL DEFAULT ''`), registered in
  `coreDatabaseModule` with `.addMigrations(...)`.

1.2 **Register screen collects the username**
- `RegisterState`: add a `username: TextFieldState`. Add a field to `RegisterScreen` using
  `SplitlineTextField`, plus a string in `auth/presentation` `strings.xml`.
- `AuthRepository.register(...)` and `RoomAuthRepository` take `username` and save it into `UserEntity`.

1.3 **Read the username from the session and the database**
- `:core:domain`: add a model `user/User(userId, email, username)` and an interface
  `user/UserRepository` with `suspend fun getUser(userId: String): Result<User, DataError.Local>`.
- `:core:database`: implement `RoomUserRepository` (uses `UserDao.getUserById`, `withContext(Dispatchers.IO)`),
  add mappers `mappers/UserEntityMappers.kt`, and bind it in `coreDatabaseModule` with
  `singleOf(::RoomUserRepository).bind<UserRepository>()`.
- `RunOverviewViewModel`: in `init`, call `sessionStorage.get()?.userId`, then `userRepository.getUser(...)`.
  Put `username` in `RunOverviewState`. Users can only reach this screen after entering a name,
  so there is no empty-username fallback.

1.4 **Header UI**
- Add `RunOverviewState` fields `username` and `isAccountMenuOpen`.
- The date and greeting are formatted in `run/presentation/.../util/`. The greeting depends on the time of day
  (morning, afternoon or evening), and its strings go in `run/presentation` `strings.xml`.
- Add a local component `run_overview/components/HomeHeader.kt`: the date, the greeting and a circular
  account button. The button is a circle with the first letter of `username` inside, taken directly in the
  composable. Style it from `MaterialTheme.colorScheme`.
- Clicking the account button opens a `DropdownMenu` with **Analytics** and **Logout**.
  Logout keeps the current flow (`OnLogOutClick` → `sessionStorage.set(null)` + `onLogoutClick()`).
  Analytics keeps `OnAnalyticsClick` → `onAnalyticsClick()`.
- Actions: `OnAccountClick` and `OnAccountMenuDismiss`. Keep `OnLogOutClick` and `OnAnalyticsClick`.
- Remove `SplitlineToolbar` and the `topAppBar`/`scrollBehavior` from `RunOverviewScreen`.
  The header becomes the first item of the scrollable content.

## Step 2: Skip "Weekly goal". Add the "Last run" card directly below the header
Figma node `1:28`.

- Add `RunOverviewState.lastRun: RunUi?`, set to the first item of `runs`
  (`getRuns()` is already sorted newest first; check this). If there are no runs, keep the existing
  "No completed runs found" text instead of the card.
- Add a local component `run_overview/components/LastRunCard.kt`:
  - Top row: the "Last run" title and a relative date such as "Yesterday · 07:12". Add a formatter in
    `util/` for Today/Yesterday/date + time.
  - Map: reuse the existing `mapPictureUrl` static map image, clipped to a rounded 340×120 area.
    Move `MapImage` out of `RunListItem.kt` into its own component file so both can use it.
  - A 3-column stats row: distance (`km`), pace (`min/km`) and duration (`time`), using the existing
    `RunUi` fields. Values use large bold text and labels use small `onSurfaceVariant` text.
  - A **"See all runs"** label (text button) at the bottom of the card.
- **All runs page:**
  - It is not a new navigation destination. It is an overlay shown on top of the home content in the same
    `RunOverviewScreen`.
  - State: `isAllRunsOpen`. Actions: `OnSeeAllRunsClick` and `OnAllRunsBackClick`.
  - The overlay is full screen (`AnimatedVisibility`, sliding in) with a back arrow and the existing
    `LazyColumn` of `RunListItem`s. Long-press to delete still works.
  - `BackHandler(enabled = state.isAllRunsOpen)` sends `OnAllRunsBackClick`, so the system back gesture
    closes the overlay instead of leaving home.

## Step 3: "Start run" button as in the design
Figma node `1:81`: a 160×60 lime pill with a play icon and "Start run", floating at the bottom end with a soft glow.

- Add a local component `run_overview/components/StartRunButton.kt`: a primary-color pill with an icon and
  text in `onPrimary`, a rounded shape and shadow from the design.
- Reuse the play/start icon from the design system if one exists. Otherwise add one to
  `core/presentation/designsystem` icons.
- Put it in `SplitlineScaffold.floatingActionButton` in place of `SplitlineFloatingActionButton(RunIcon)`.
  It still sends `OnStartClick`.
- Hide it while the All runs overlay is open (decide during implementation).

## Step 4: Bottom navigation, skipped
Do not implement the bottom navigation bar (`1:59`).

## Files touched (summary)
- `run/presentation/.../run_overview/`: `RunOverviewScreen.kt`, `RunOverviewState.kt`, `RunOverviewAction.kt`,
  `RunOverviewViewModel.kt`, plus `components/HomeHeader.kt`, `LastRunCard.kt`, `StartRunButton.kt`, `MapImage.kt`;
  the feature's `util/` formatters; `res/values/strings.xml`.
- `core/domain`: `user/User.kt`, `user/UserRepository.kt`.
- `core/database`: `UserEntity.kt`, `RunDatabase.kt` (v3 + migration), `RoomUserRepository.kt`,
  `mappers/UserEntityMappers.kt`, `di/CoreDatabaseModule.kt`.
- `auth`: `AuthRepository`, `RoomAuthRepository`, and the `register/` state, action, viewmodel and screen.

## Verification
- Build: `./gradlew assembleDebug`.
- Register a new user with a username. Home shows "Good …, <username>" and the initial in the avatar.
- The account menu has Analytics, which opens analytics, and Logout, which goes back to auth.
- The Last run card shows the newest run. "See all runs" opens the overlay, and system back closes it.
- "Start run" opens the active run screen.
- Compare the screen with the Figma screenshot of node `1:2`.
