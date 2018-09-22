package com.traviswyatt.sensortag.lifecycle

import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class ScopedViewModel(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel(), CoroutineScope {

    protected val job = Job()
    override val coroutineContext: CoroutineContext = job + coroutineDispatcher

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}
