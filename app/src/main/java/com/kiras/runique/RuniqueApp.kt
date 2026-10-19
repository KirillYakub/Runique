package com.kiras.runique

import android.app.Application
import com.kiras.analytics.data.di.analyticsModule
import com.kiras.analytics.presentation.di.analyticsPresentationModule
import com.kiras.auth.data.di.authDataModule
import com.kiras.auth.presentation.di.authViewModelModule
import com.kiras.core.data.di.coreDataModule
import com.kiras.core.database.di.databaseModule
import com.kiras.run.data.di.runDataModule
import com.kiras.run.location.di.locationModule
import com.kiras.run.presentation.active_run.di.activeRunViewModelModule
import com.kiras.run.presentation.run_overview.di.runViewModelModule
import com.kiras.runique.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class RuniqueApp : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        startKoin {
            androidLogger()
            androidContext(this@RuniqueApp)
            modules(
                authDataModule,
                authViewModelModule,
                appModule,
                coreDataModule,
                runViewModelModule,
                activeRunViewModelModule,
                locationModule,
                databaseModule,
                runDataModule,
                analyticsModule,
                analyticsPresentationModule
            )
        }
    }
}