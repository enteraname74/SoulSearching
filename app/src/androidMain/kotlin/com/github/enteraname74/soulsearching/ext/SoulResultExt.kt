package com.github.enteraname74.soulsearching.ext

import androidx.work.ListenableWorker
import com.github.enteraname74.domain.model.SoulResult

fun SoulResult<*>.toWorkerResult(): ListenableWorker.Result =
    when (this) {
        is SoulResult.Error<*> -> ListenableWorker.Result.failure()
        is SoulResult.Success<*> -> ListenableWorker.Result.success()
    }