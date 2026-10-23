# Refactor TODO

Code that does not match `.claude/rules` yet. Each item is a separate task, done on request.

- **`LocalRunRepository` name:** the data-layer rule asks for `<Technology>X`, but this class coordinates `LocalRunDataSource` plus file storage for map pictures, so it doesn't wrap a single technology. Decide on a name or add an exception to `data-layer.md`.
- **`AnalyticsDashboardState` defaults:** its fields have no defaults (the ViewModel uses a nullable state while loading). The MVI rule wants defaults for every field, plus an `isLoading` flag.
- **Example unit tests don't compile:** most modules have an `ExampleUnitTest` but no `testImplementation(libs.junit)`, so `testDebugUnitTest` fails for them. This predates the refactor. Either add JUnit to the convention plugins or decide the example files can go.
