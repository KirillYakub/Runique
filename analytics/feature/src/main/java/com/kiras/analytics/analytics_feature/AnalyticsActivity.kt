package com.kiras.analytics.analytics_feature

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.splitcompat.SplitCompat
import com.kiras.analytics.data.di.analyticsModule
import com.kiras.analytics.presentation.AnalyticsDashboardScreenRoot
import com.kiras.analytics.presentation.di.analyticsPresentationModule
import com.kiras.core.presentation.designsystem.RuniqueTheme
import org.koin.core.context.loadKoinModules

class AnalyticsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadKoinModules(listOf(analyticsModule, analyticsPresentationModule))
        SplitCompat.installActivity(this)

        setContent {
            RuniqueTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "analytics"
                ) {
                    composable("analytics") {
                        AnalyticsDashboardScreenRoot(
                            onBackClick = {
                                finish()
                            }
                        )
                    }
                }
            }
        }
    }
}