package com.kiras.splitline

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.kiras.auth.presentation.intro.IntroScreenRoot
import com.kiras.auth.presentation.login.LoginScreenRoot
import com.kiras.auth.presentation.register.RegisterScreenRoot
import com.kiras.auth.presentation.util.AuthGraphRoute
import com.kiras.auth.presentation.util.IntroRoute
import com.kiras.auth.presentation.util.LoginRoute
import com.kiras.auth.presentation.util.RegisterRoute
import com.kiras.run.presentation.active_run.ActiveRunScreenRoot
import com.kiras.run.presentation.active_run.service.ActiveRunService
import com.kiras.run.presentation.run_overview.RunOverviewScreenRoot
import com.kiras.run.presentation.util.ActiveRunRoute
import com.kiras.run.presentation.util.RunGraphRoute
import com.kiras.run.presentation.util.RunOverviewRoute
import com.kiras.statistics.presentation.dashboard.StatisticsDashboardScreenRoot
import com.kiras.statistics.presentation.util.StatisticsDashboardRoute

@Composable
fun NavigationRoot(
    navHostController: NavHostController,
    isLoggedIn: Boolean
) {
    val context = LocalContext.current
    val onAuthSuccess = {
        navHostController.navigate(RunGraphRoute) {
            popUpTo<AuthGraphRoute> {
                inclusive = true
            }
        }
    }

    NavHost(
        navController = navHostController,
        startDestination = if(isLoggedIn) RunGraphRoute else AuthGraphRoute
    ) {
        navigation<AuthGraphRoute>(
            startDestination = IntroRoute
        ) {
            composable<IntroRoute> {
                IntroScreenRoot(
                    onSignUpClick = {
                        navHostController.navigate(RegisterRoute)
                    },
                    onSignInClick = {
                        navHostController.navigate(LoginRoute)
                    }
                )
            }
            composable<RegisterRoute> {
                RegisterScreenRoot(
                    onSignInClick = {
                        navHostController.navigate(LoginRoute) {
                            popUpTo<RegisterRoute> {
                                inclusive = true
                                saveState = true
                            }
                            restoreState = true
                        }
                    },
                    onSuccessfulRegistrationClick = onAuthSuccess
                )
            }
            composable<LoginRoute> {
                LoginScreenRoot(
                    onSignUpClick = {
                        navHostController.navigate(RegisterRoute) {
                            popUpTo<LoginRoute> {
                                inclusive = true
                                saveState = true
                            }
                            restoreState = true
                        }
                    },
                    onLoginSuccess = onAuthSuccess
                )
            }
        }
        navigation<RunGraphRoute>(
            startDestination = RunOverviewRoute
        ) {
            composable<RunOverviewRoute> {
                RunOverviewScreenRoot(
                    onStartRunClick = {
                        navHostController.navigate(ActiveRunRoute)
                    },
                    onLogoutClick = {
                        navHostController.navigate(AuthGraphRoute) {
                            popUpTo<RunGraphRoute> {
                                inclusive = true
                            }
                        }
                    },
                    onStatisticsClick = {
                        navHostController.navigateToTab(StatisticsDashboardRoute)
                    }
                )
            }
            composable<ActiveRunRoute>(
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "splitline://active_run"
                    }
                )
            ) {
                ActiveRunScreenRoot(
                    onBack = {
                        navHostController.navigateUp()
                    },
                    onFinish = {
                        navHostController.navigateUp()
                    },
                    onServiceToggle = { shouldServiceRun ->
                        if(shouldServiceRun) {
                            context.startService(
                                ActiveRunService.createStartIntent(
                                    context = context,
                                    activityClass = MainActivity::class.java
                                )
                            )
                        }
                        else {
                            context.startService(
                                ActiveRunService.createStopIntent(context)
                            )
                        }
                    }
                )
            }
        }
        composable<StatisticsDashboardRoute> {
            StatisticsDashboardScreenRoot(
                onHomeClick = {
                    navHostController.navigateToTab(RunOverviewRoute)
                }
            )
        }
    }
}

private fun NavHostController.navigateToTab(route: Any) {
    navigate(route) {
        popUpTo<RunOverviewRoute> {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
