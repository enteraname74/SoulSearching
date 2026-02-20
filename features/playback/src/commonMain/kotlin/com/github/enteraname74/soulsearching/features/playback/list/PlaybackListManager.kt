package com.github.enteraname74.soulsearching.features.playback.list

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Deprecated("Use PlayerRepository instead")
internal class PlaybackListManager(
    private val playbackCallback: PlaybackListCallbacks,
    private val settings: SoulSearchingSettings,
) {
    private val _state: MutableStateFlow<PlaybackListState> =
        MutableStateFlow(PlaybackListState.NoData)
    val state: StateFlow<PlaybackListState> = _state.asStateFlow()

    private suspend fun <R> withDataState(block: suspend PlaybackListState.Data.() -> R): R? =
        (_state.value as? PlaybackListState.Data)?.let {
            block(it)
        }

    /**
     * Updates the played list after a reorder in it.
     */
    suspend fun updatePlayedListAfterReorder(
        newList: List<Music>
    ) {
        withDataState {
            _state.value = this.copy(
                playedList = newList,
            )
            settings.saveCurrentMusicInformation(
                currentMusicIndex = newList.indexOf(currentMusic),
                currentMusicPosition = playbackCallback.getMusicPosition(),
            )
        }
    }
}