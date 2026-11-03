# Compose component placement

- A composable used by more than one feature goes in the design system: `core/presentation/designsystem/src/main/java/com/kiras/core/presentation/designsystem/components`.
- A composable used only inside one feature goes in that feature's local `components` package, next to the screen that uses it (for example `run/presentation/.../run_overview/components`, `statistics/presentation/.../components`).
- Do not put reusable component composables in screen files. A screen file holds only the screen (Root/Screen) composables and their previews.
- When a local component starts being used by a second feature, move it to the design system.
