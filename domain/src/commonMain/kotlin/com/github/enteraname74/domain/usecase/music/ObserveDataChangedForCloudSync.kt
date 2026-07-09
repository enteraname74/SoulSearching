package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.usecase.cloud.CloudBackgroundSyncJob
import com.github.enteraname74.domain.util.WorkDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ObserveDataChangedForCloudSync(
    private val syncMusicWithCloudUseCase: SyncMusicWithCloudUseCase,
    private val commonMusicUseCase: CommonMusicUseCase,
    private val cloudBackgroundSyncJob: CloudBackgroundSyncJob,
    workDispatcher: WorkDispatcher,
) {
    private val workScope = CoroutineScope(workDispatcher.dispatcher)
    private var job: Job? = null
    private val buffer: MutableStateFlow<Buffer> = MutableStateFlow(Buffer.Idle)

    operator fun invoke() {
        job?.cancel()
        job = workScope.launch {
            /*
            Checks for update request.
            If a request is already in progress, wait for it to be finished
             */
            launch {
                commonMusicUseCase.observeDataChanged().collectLatest {
                    println("CLUELESS -- DATA CHANGED")
                    if (syncMusicWithCloudUseCase.state.value is SyncMusicWithCloudUseCase.State.WorkingState) {
                        buffer.value = Buffer.Waiting
                    } else {
                        buffer.value = Buffer.Launch
                    }
                }
            }

            /*
            Checks for sync status.
            If a sync is ended or not running, and we have a waiting sync to be done,
            we can launch a new one.
             */
            launch {
                syncMusicWithCloudUseCase.state.collectLatest { syncState ->
                    if (syncState is SyncMusicWithCloudUseCase.State.EndState && buffer.value == Buffer.Waiting) {
                        println("CLUELESS -- Sync ended or not in progress, will launch a sync")
                        buffer.value = Buffer.Launch
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
                        Buffer.Waiting, Buffer.Idle -> {
                            //no-op
                        }
                        Buffer.Launch -> {
                            println("CLUELESS -- buffer ready, will launch")
                            cloudBackgroundSyncJob.launchIfPossible()
                            buffer.value = Buffer.Idle
                        }
                    }
                }
            }
        }

    }


    enum class Buffer {
        Waiting,
        Idle,
        Launch,
    }
}