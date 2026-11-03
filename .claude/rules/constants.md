# Constants

- A class that needs a constant declares it as `const val` in a `private companion object` inside that class:
  `private companion object { const val MAP_PICTURES_DIR = "run_maps" }`. Inside a private companion, don't repeat `private` on each constant.
- If the companion object must be public because it has members used outside the class (for example `ActiveRunService.createStartIntent`, `ValidatePasswordUseCase.MIN_PASSWORD_LENGTH`), keep it public. Mark every constant that isn't used outside the class `private const val`.
- A class has at most one companion object. Put all its constants there; never add a second holder object or a top-level constant for that class.
- `object` declarations can't have a companion object. Declare their constants as `private const val` directly in the object body (for example `DailyGoalProgressCalculator.DAYS_IN_WEEK`).
- Do not declare named constants at the top level of a file, including composable files (for example `private val RingStrokeWidth = 12.dp`). Write the value inline where it's used.
- Never pass a constant in as a default constructor parameter. Use it directly in the class body (see the code-style rule).
- Design-system tokens are not constants in this sense and stay top-level and public: `Color.kt`, `Typography` in `Poppins.kt`, `DarkColorScheme`, `Icons.kt`.
