# Navigation

- Use type-safe Navigation Compose routes: `@Serializable data object` / `@Serializable data class` (arguments as properties), never string routes.
- Each feature's presentation module owns:
  - its route objects (`<Feature>Route.kt`)
  - a nav-graph extension `fun NavGraphBuilder.<feature>Graph(navController, …callbacks)` in `navigation/`.
- Cross-feature navigation goes through lambdas passed to the feature graph. `:app`'s `NavigationRoot` connects the graphs, and features never import each other's routes.
- Screens never receive `NavController`. `XScreenRoot` only gets lambdas such as `onLoginSuccess`, `onBackClick`.
- Deep links (for example `runique://active_run`) are declared on the route's `composable<Route>(deepLinks = …)`.
