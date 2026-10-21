# Data layer

- Name implementations of domain interfaces `<Technology><Interface>`: `RoomLocalRunDataSource`, `RoomAnalyticsRepository`, `EncryptedSessionStorage`, `AndroidLocationObserver`. Do not use `Impl` or `Default` suffixes/prefixes.
- Mark implementations `internal`. Expose them only through the module's Koin module bound to the domain interface.
- Room:
  - Entities go in `:core:database` `entity/` (`XEntity`), DAOs in `dao/` (`XDao`), the database in `RunDatabase`.
  - Every user-owned query filters by `userId`.
- Mappers:
  - Put them in package `mappers` and name the file after the source model: `mappers/RunEntityMappers.kt`, `mappers/AuthInfoMappers.kt`.
  - Write them as top-level `internal` extension functions `toY()`, with both directions in the same file.
- Catch specific exceptions (for example `SQLiteFullException`) and return `Result.Error(DataError.X)`. Never let them reach presentation, and never catch a generic `Exception`.
- I/O runs in `withContext(Dispatchers.IO)`.
- Independent queries run in parallel with `async`.
- Work that must finish after the screen closes runs on the injected application `CoroutineScope`.
- Constants go in a `private companion object`.
