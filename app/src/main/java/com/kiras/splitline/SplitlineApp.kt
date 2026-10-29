package com.kiras.splitline

import android.app.Application
import com.kiras.auth.data.di.authDataModule
import com.kiras.auth.presentation.di.authPresentationModule
import com.kiras.core.data.di.coreDataModule
import com.kiras.core.database.di.coreDatabaseModule
import com.kiras.run.location.di.runLocationModule
import com.kiras.run.presentation.di.runPresentationModule
import com.kiras.splitline.di.appModule
import com.kiras.statistics.data.di.statisticsDataModule
import com.kiras.statistics.presentation.di.statisticsPresentationModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class SplitlineApp : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        startKoin {
            androidLogger()
            androidContext(this@SplitlineApp)
            modules(
                appModule,
                coreDataModule,
                coreDatabaseModule,
                authDataModule,
                authPresentationModule,
                runLocationModule,
                runPresentationModule,
                statisticsDataModule,
                statisticsPresentationModule
            )
        }
    }
}
