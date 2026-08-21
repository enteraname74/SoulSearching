package com.github.enteraname74.soulsearching.features.playback.progressJob

import com.github.enteraname74.domain.repository.PlayerRepository
import com.github.enteraname74.domain.util.WorkDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

internal class PlaybackProgressJob(
    private val playerRepository: PlayerRepository,
    private val callback: PlaybackProgressJobCallbacks,
    private val workDispatcher: WorkDispatcher,
) {
    /**
     * Used to update frequently the current position in the duration
     * of the played music.
     */
    private var durationJob: Job? = null

    private val _state: MutableStateFlow<Duration> = MutableStateFlow(Duration.ZERO)
    val state: StateFlow<Duration> = _state.asStateFlow()

    /**
     * Launch a duration job, used for updating the UI to indicate the current position
     * in the played music.
     */
    suspend fun launchDurationJobIfNecessary() {
        setPosition(pos = callback.getPlayerProgress())
        if (durationJob != null) return
        durationJob = CoroutineScope(workDispatcher.dispatcher).launch {
            while (true) {
                delay(DELAY_BEFORE_SENDING_VALUE.milliseconds)
                val position = callback.getPlayerProgress()

                _state.value = position
                playerRepository.setProgress(position)
            }
        }
    }

    fun setPosition(pos: Duration) {
        _state.value = pos
    }

    /**
     * Release the duration job.
     */
    fun releaseDurationJob() {
        durationJob?.cancel()
        durationJob = null
    }

    companion object {
        private const val DELAY_BEFORE_SENDING_VALUE: Long = 200L
    }
}

interface PlaybackProgressJobCallbacks {
    suspend fun isPlaying(): Boolean
    suspend fun getPlayerProgress(): Duration
}
