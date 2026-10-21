# Domain layer

- Pure Kotlin: no Android, Room, Koin or Compose imports.
- Contains:
  - models: plain `data class`es
  - repository / data-source / service interfaces
  - use cases
  - domain logic classes (for example `RunningTracker`, `UserDataValidator`)
- Errors:
  - Fallible operations return `Result<D, E>` or `EmptyResult<E>` from `com.kiras.core.domain.util`, using `DataError.Local` / `DataError.Network`.
  - Add new error cases to these enums rather than throwing.
- Use cases:
  - Put them in package `<feature>.domain.usecase`. Name them `VerbNounUseCase` (`GetRunsUseCase`, `DeleteRunUseCase`), each with one public `operator fun invoke(...)` (`suspend` when needed).
  - ViewModels depend only on use cases, never on repositories directly, even for plain pass-through calls.
  - Domain modules have no Koin module. Register use cases with `factoryOf(::XUseCase)` in the feature's presentation Koin module.
- Group by topic in subpackages (`run/`, `location/`, `util/`, `usecase/`). Use `typealias` for ID types (`RunId`).
