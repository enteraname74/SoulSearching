package com.github.enteraname74.soulsearching.features.playback.progressjob

import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class PlaybackProgressJob(
    private val settings: SoulSearchingSettings,
    private val callback: PlaybackProgressJobCallbacks,
) {
    /**
     * Used to update frequently the current position in the duration
     * of the played music.
     */
    private var durationJob: Job? = null

    private val _state: MutableStateFlow<Int> = MutableStateFlow(0)
    val state: StateFlow<Int> = _state.asStateFlow()

    /**
     * Launch a duration job, used for updating the UI to indicate the current position
     * in the played music.
     */
    fun launchDurationJobIfNecessary() {
        setPosition(pos = callback.getMusicPosition())
        if (durationJob != null) return
        durationJob = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                delay(DELAY_BEFORE_SENDING_VALUE)
                val position = callback.getMusicPosition()
                _state.value = position
                settings.set(
                    key = SoulSearchingSettingsKeys.Player.PLAYER_MUSIC_POSITION_KEY.key,
                    value = position,
                )
            }
        }
    }

    fun setPosition(pos: Int) {
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