# Code style

- Comments:
  - Do not write comments or KDoc in code.
  - Add them only when the user explicitly asks for them.
- Visibility:
  - Do not use `internal`. Classes, functions and properties are public by default, and the `public` modifier is never written explicitly.
  - Use `private` where a rule asks for it (for example `XScreen`, previews, `private set`, `private companion object`) or for members that are only used inside their class or file.
- Constructors:
  - Do not give constructor parameters default values that only pass a constant through (for example `class PasswordHasher(private val iterations: Int = DEFAULT_ITERATIONS)`).
  - If a `private const` exists for the value, use it directly in the class body instead of taking it as a constructor parameter.
