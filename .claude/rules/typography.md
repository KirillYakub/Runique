# Typography

- Do not set static text sizes in composables (for example `fontSize = 13.sp`, or a `TextUnit` parameter like `valueFontSize = 32.sp`).
- Always style text with `MaterialTheme.typography.<role>` via the `style` parameter (`style = MaterialTheme.typography.bodySmall`).
- Overriding `fontWeight`, `color` or `textAlign` on top of a typography style is allowed; the size always comes from the style.
- To make a variant, use `MaterialTheme.typography.<role>.copy(...)` without changing `fontSize`.
- Components that let callers change text size take a `TextStyle` parameter, not a `TextUnit`.
- `.sp` values are only defined in the design system `Typography` (`core/presentation/designsystem/.../Poppins.kt`). If no role fits, use the nearest existing one or add a role there.
