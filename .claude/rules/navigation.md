# Navigation

- Use type-safe Navigation Compose routes: `@Serializable data object` / `@Serializable data class` (arguments as properties), never string routes.
- Routes:
  - Each feature's presentation module defines its routes in `util/Routes.kt` (package `com.kiras.<feature>.presentation.util`): the graph route (`AuthGraphRoute`) and one route per screen (`LoginRoute`, `RegisterRoute`).
  - Routes are public so `:app` can use them.
- Graphs:
  - Features do not define `NavGraphBuilder` extensions or a `navigation/` package.
  - `app/NavigationRoot` holds the `NavHost` and every nested graph: `navigation<XGraphRoute>(startDestination = …) { composable<XRoute> { XScreenRoot(...) } }`, using the routes and `XScreenRoot`s exported by the features.
  - Cross-feature navigation is done in `NavigationRoot` by passing lambdas to `XScreenRoot`. Features never import each other's routes.
- Screens never receive `NavController`. `XScreenRoot` only gets lambdas such as `onLoginSuccess`, `onBackClick`.
- Deep links (for example `splitline://active_run`) are declared on `composable<Route>(deepLinks = …)` in `NavigationRoot`.
