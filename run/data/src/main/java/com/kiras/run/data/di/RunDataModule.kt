package com.kiras.run.data.di

import com.kiras.core.domain.run.SyncRunScheduler
import com.kiras.run.data.CreateRunWorker
import com.kiras.run.data.DeleteRunWorker
import com.kiras.run.data.FetchRunsWorker
import com.kiras.run.data.SyncRunWorkerScheduler
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val runDataModule = module {
    workerOf(::CreateRunWorker)
    workerOf(::DeleteRunWorker)
    workerOf(::FetchRunsWorker)

    singleOf(::SyncRunWorkerScheduler) bind SyncRunScheduler::class
}