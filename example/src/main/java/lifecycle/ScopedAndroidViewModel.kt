package com.traviswyatt.sensortag.lifecycle

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class ScopedAndroidViewModel(
    application: Application,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default
) : AndroidViewModel(application), CoroutineScope {

    protected val job = Job()
    override val coroutineContext: CoroutineContext = job + coroutineDispatcher

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}
