# Domain layer

- Pure Kotlin: no Android, Room, Koin or Compose imports.
- Contains:
  - models: plain `data class`es
  - repository / data-source / service interfaces
  - use cases (only when needed)
  - domain logic classes (for example `RunningTracker`, `LocationDataCalculator`)
- Errors:
  - Fallible operations return `Result<D, E>` or `EmptyResult<E>` from `com.kiras.core.domain.util`, using `DataError.Local` / `DataError.Network`.
  - Add new error cases to these enums rather than throwing.
- Use cases:
  - Do not create a use case unless it is necessary (it holds real logic or combines several repositories/sources). For plain pass-through calls the ViewModel calls the repository directly.
  - If it is unclear whether a use case is needed, ask the user before creating one.
  - When one exists, put it in package `<feature>.domain.usecase`. Name it `VerbNounUseCase` (`GetRunsUseCase`, `DeleteRunUseCase`), with one `operator fun invoke(...)` (`suspend` when needed).
  - Domain modules have no Koin module. Register use cases with `factoryOf(::XUseCase)` in the feature's presentation Koin module.
- Group by topic in subpackages (`run/`, `location/`, `util/`, `usecase/`). Use `typealias` for ID types (`RunId`).
