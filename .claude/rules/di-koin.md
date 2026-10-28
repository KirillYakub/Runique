# Dependency injection (Koin)

- Each Gradle module that needs bindings (every module except `:domain` modules) has exactly one Koin module, in `di/<Feature><Layer>Module.kt`, named `val <feature><Layer>Module = module { }` (for example `runPresentationModule`, `runDataModule`, `coreDatabaseModule`).
- Bindings:
  - implementations: `singleOf(::XRepositoryImpl).bind<XRepository>()`
  - use cases (only when one exists, see domain-layer rule): `factoryOf(::XUseCase)`, in the feature's presentation module
  - domain classes that hold state (`RunningTracker`): `singleOf(::RunningTracker)`, also in the feature's presentation module, separate from the ViewModels
  - ViewModels: `viewModelOf(::XViewModel)`
  - DAOs: `single { get<RunDatabase>().xDao }`
- Register data-layer classes in their own data module's Koin module, never in presentation.
- `appModule` (in `:app`) only provides app-wide objects (`SharedPreferences`, the application `CoroutineScope`, `MainViewModel`).
- Every module is added to `startKoin { modules(...) }` in `RuniqueApp`.
- In composables, get ViewModels with `org.koin.compose.viewmodel.koinViewModel` only.
