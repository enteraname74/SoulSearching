package com.github.enteraname74.soulsearching.feature.addtoplaylist

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.domain.model.MusicPlaylist
import com.github.enteraname74.soulsearching.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.soulsearching.domain.usecase.musicplaylist.CommonMusicPlaylistUseCase
import com.github.enteraname74.soulsearching.viewholder.SoulViewModelHolderV2
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class AddToPlaylistViewHolder(
    private val commonMusicPlaylistUseCase: CommonMusicPlaylistUseCase,
    private val commonMusicUseCase: CommonMusicUseCase,
    private val loadingManager: LoadingManager,
    private val params: AddToPlaylistDestination,
) : SoulViewModelHolderV2<AddToPlaylistNavScope, AddToPlaylistState>() {
    private val search: MutableStateFlow<String> = MutableStateFlow("")
    private val musicsFlow = search.flatMapLatest { search ->
        // For smooth UI
        if (search.isNotEmpty()) {
            delay(300.milliseconds)
        }
        commonMusicUseCase.availableSongsForPlaylist(
            playlistId = params.playlistId,
            search = search,
        )
    }

    override fun getInitialState(): AddToPlaylistState =
        AddToPlaylistState(
            musics = musicsFlow,
            selectedMusicIds = emptySet(),
            onSearch = { search.value = it },
            onToggleSelection = ::onToggleSelection,
            onSave = ::onSave,
            navigateBack = { navigate { navigateBack() } },
        )

    private fun onToggleSelection(musicId: Uuid) {
        updateState {
            copy(
                selectedMusicIds = if (selectedMusicIds.contains(musicId)) {
                    selectedMusicIds - musicId
                } else {
                    selectedMusicIds + musicId
                }
            )
        }
    }

    private fun onSave() {
        loadingManager.withLoadingOnScope(viewModelScope) {
            if (currentState.selectedMusicIds.isNotEmpty()) {
                commonMusicPlaylistUseCase.upsertAll(
                    musicPlaylists = currentState.selectedMusicIds.map { musicId ->
                        MusicPlaylist(
                            musicId = musicId,
                            playlistId = params.playlistId,
                        )
                    }
                )
            }
            navigate { navigateBack() }
        }
    }

    @Composable
    override fun Content(state: AddToPlaylistState) {
        AddToPlaylistScreen(state)
    }
}