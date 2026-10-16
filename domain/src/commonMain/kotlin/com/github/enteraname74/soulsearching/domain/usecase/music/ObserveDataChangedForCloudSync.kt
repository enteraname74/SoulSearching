package com.github.enteraname74.soulsearching.domain.usecase.music

import com.github.enteraname74.soulsearching.domain.repository.CloudPreferencesRepository
import com.github.enteraname74.soulsearching.domain.repository.SettingsRepository
import com.github.enteraname74.soulsearching.domain.usecase.cloud.CloudBackgroundSyncJob
import com.github.enteraname74.soulsearching.domain.util.WorkDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

@OptIn(FlowPreview::class)
class ObserveDataChangedForCloudSync(
    private val syncDataWithCloudUseCase: SyncDataWithCloudUseCase,
    private val commonMusicUseCase: CommonMusicUseCase,
    private val cloudBackgroundSyncJob: CloudBackgroundSyncJob,
    private val settingsRepository: SettingsRepository,
    private val cloudPreferencesRepository: CloudPreferencesRepository,
    workDispatcher: WorkDispatcher,
) {
    private val workScope = CoroutineScope(workDispatcher.dispatcher)
    private var job: Job? = null
    private val buffer = MutableStateFlow<Buffer>(Buffer.Idle)
    private var delayJob: Job? = null

    operator fun invoke() {
        job?.cancel()
        syncStatsListener()
        job = workScope.launch {
            launch {
                commonMusicUseCase.observeDataChanged().collect { request(Scope.Full) }
            }
            launch {
                settingsRepository.observeCloudSettings()
                    .drop(1)
                    .debounce(500.milliseconds)
                    .collect {
                        val checkpoint = cloudPreferencesRepository.getLastSettingsSyncMillis()
                        if (settingsRepository.getCloudSettingsUpdatedAfter(checkpoint) != null) {
                            request(Scope.Settings)
                        }
                    }
            }
            launch {
                syncDataWithCloudUseCase.state.collect { syncState ->
                    val waiting = buffer.value as? Buffer.Waiting
                    if (syncState is SyncDataWithCloudUseCase.State.EndState && waiting != null) {
                        buffer.value = Buffer.Launch(waiting.scope)
                    }
                }
            }
            launch {
                buffer.collect { bufferState ->
                    if (bufferState is Buffer.Launch) {
                        cloudBackgroundSyncJob.launchIfPossible(
                            syncStats = bufferState.scope == Scope.FullWithStats,
                            onlySettings = bufferState.scope == Scope.Settings,
                        )
                        if (buffer.value == bufferState) buffer.value = Buffer.Idle
                    }
                }
            }
        }
    }

    private fun request(scope: Scope) {
        val current = buffer.value
        if (current == Buffer.Blocked) return

        if (syncDataWithCloudUseCase.state.value is SyncDataWithCloudUseCase.State.WorkingState) {
            buffer.value = Buffer.Waiting(
                if (current is Buffer.Waiting) higherPriority(current.scope, scope) else scope
            )
            return
        }

        when (current) {
            Buffer.Blocked -> Unit
            is Buffer.Waiting -> buffer.value = Buffer.Waiting(higherPriority(current.scope, scope))
            is Buffer.Launch -> buffer.value = Buffer.Launch(higherPriority(current.scope, scope))
            Buffer.Idle -> {
                buffer.value = if (syncDataWithCloudUseCase.state.value is SyncDataWithCloudUseCase.State.WorkingState) {
                    Buffer.Waiting(scope)
                } else {
                    Buffer.Launch(scope)
                }
            }
        }
    }

    private fun syncStatsListener() {
        delayJob?.cancel()
        delayJob = workScope.launch {
            while (true) {
                delay(5.minutes)
                request(Scope.FullWithStats)
            }
        }
    }

    fun setBufferIfNotBlocked(newValue: Buffer) {
        if (buffer.value != Buffer.Blocked) buffer.value = newValue
    }

    suspend fun skipUpdate(block: suspend () -> Unit) {
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

    enum class Scope(val priority: Int) {
        Settings(0),
        Full(1),
        FullWithStats(2),
    }

    private fun higherPriority(first: Scope, second: Scope): Scope =
        if (first.priority >= second.priority) first else second

    sealed interface Buffer {
        data class Waiting(val scope: Scope) : Buffer
        data object Blocked : Buffer
        data object Idle : Buffer
        data class Launch(val scope: Scope) : Buffer
    }
}
