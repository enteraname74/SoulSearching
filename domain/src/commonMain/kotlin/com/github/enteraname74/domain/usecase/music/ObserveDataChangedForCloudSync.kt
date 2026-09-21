package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.usecase.cloud.CloudBackgroundSyncJob
import com.github.enteraname74.domain.util.WorkDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class ObserveDataChangedForCloudSync(
    private val syncDataWithCloudUseCase: SyncDataWithCloudUseCase,
    private val commonMusicUseCase: CommonMusicUseCase,
    private val cloudBackgroundSyncJob: CloudBackgroundSyncJob,
    workDispatcher: WorkDispatcher,
) {
    private val workScope = CoroutineScope(workDispatcher.dispatcher)
    private var job: Job? = null
    private val buffer: MutableStateFlow<Buffer> = MutableStateFlow(Buffer.Idle)

    private val syncStats: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var delayJob: Job? = null

    private fun tryToLaunchOrWait() {
        if (syncDataWithCloudUseCase.state.value is SyncDataWithCloudUseCase.State.WorkingState) {
            setBufferIfNotBlocked(Buffer.Waiting)
        } else {
            setBufferIfNotBlocked(Buffer.Launch(syncStats.value))
        }
    }

    operator fun invoke() {
        job?.cancel()
        syncStatsListener()
        job = workScope.launch {
            /*
            Checks for update request.
            If a request is already in progress, wait for it to be finished
             */
            launch {
                commonMusicUseCase.observeDataChanged().collectLatest {
                    tryToLaunchOrWait()
                }
            }

            /*
            Checks for sync status.
            If a sync is ended or not running, and we have a waiting sync to be done,
            we can launch a new one.
             */
            launch {
                syncDataWithCloudUseCase.state.collectLatest { syncState ->
                    if (syncState is SyncDataWithCloudUseCase.State.EndState && buffer.value == Buffer.Waiting) {
                        setBufferIfNotBlocked(Buffer.Launch(syncStats.value))
                    }
                }
            }

            /*
            Checks for buffer state.
            It's this part that is responsible for launching a new sync request when ready
             */
            launch {
                buffer.collectLatest { bufferState ->
                    when (bufferState) {
                        Buffer.Waiting, Buffer.Idle, Buffer.Blocked -> {
                            //no-op
                        }
                        is Buffer.Launch -> {
                            cloudBackgroundSyncJob.launchIfPossible(syncStats = bufferState.syncStats)
                            syncStats.value = false
                            setBufferIfNotBlocked(Buffer.Idle)
                        }
                    }
                }
            }
        }
    }

    private fun syncStatsListener() {
        delayJob?.cancel()
        delayJob = workScope.launch {
            while (true) {
                delay(5.minutes)
                syncStats.value = true
                tryToLaunchOrWait()
            }
        }
    }

    fun setBufferIfNotBlocked(newValue: Buffer) {
        if (buffer.value != Buffer.Blocked) {
            buffer.value = newValue
        }
    }

    suspend fun skipUpdate(
        block: suspend () -> Unit
    ) {
        buffer.value = Buffer.Blocked
        try {
            block()
        } finally {
            buffer.value = Buffer.Idle
        }
    }

    fun cancel() {
        job?.cancel()
        job = null

        delayJob?.cancel()
        delayJob = null
    }

    sealed interface Buffer {
        data object Waiting : Buffer
        data object Blocked : Buffer
        data object Idle : Buffer
        data class Launch(val syncStats: Boolean) : Buffer
    }
}
