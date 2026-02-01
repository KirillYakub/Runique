package com.kiras.runique

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.kiras.auth.presentation.intro.IntroScreenRoot
import com.kiras.auth.presentation.login.LoginScreenRoot
import com.kiras.auth.presentation.register.RegisterScreenRoot
import com.kiras.run.presentation.active_run.ActiveRunScreenRoot
import com.kiras.run.presentation.active_run.service.ActiveRunService
import com.kiras.run.presentation.run_overview.RunOverviewScreenRoot

@Composable
fun NavigationRoot(
    navHostController: NavHostController,
    isLoggedIn: Boolean,
    onAnalyticsClick: () -> Unit
) {
    NavHost(
        navController = navHostController,
        startDestination = if(!isLoggedIn) "auth" else "run"
    ) {
        authGraph(navHostController)
        runGraph(navHostController, onAnalyticsClick)
    }
}

private fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation(
        startDestination = "intro",
        route = "auth"
    ) {
        composable("intro") {
            IntroScreenRoot(
                onSignUpClick = {
                    navController.navigate("register")
                },
                onSignInClick = {
                    navController.navigate("login")
                }
            )
        }
        composable("register") {
            RegisterScreenRoot(
                onSignInClick = {
                    navController.navigate("login") {
                        popUpTo("register") {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                },
                onSuccessfulRegistrationClick = {
                    navController.navigate("login")
                }
            )
        }
        composable("login") {
            LoginScreenRoot(
                onSignUpClick = {
                    navController.navigate("register") {
                        popUpTo("login") {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                },
                onLoginSuccess = {
                    navController.navigate("run") {
                        popUpTo("auth") {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}

private fun NavGraphBuilder.runGraph(
    navHostController: NavHostController,
    onAnalyticsClick: () -> Unit
) {
    navigation(
        startDestination = "run_overview",
        route = "run"
    ) {
        composable("run_overview") {
            RunOverviewScreenRoot(
                onStartRunClick = {
                    navHostController.navigate("active_run")
                },
                onLogoutClick = {
                    navHostController.navigate("auth") {
                        popUpTo("run") {
                            inclusive = true
                        }
                    }
                },
                onAnalyticsClick = onAnalyticsClick
            )
        }
        composable(
            route = "active_run",
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "runique://active_run"
                }
            )
        ) {
            val context = LocalContext.current
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
}