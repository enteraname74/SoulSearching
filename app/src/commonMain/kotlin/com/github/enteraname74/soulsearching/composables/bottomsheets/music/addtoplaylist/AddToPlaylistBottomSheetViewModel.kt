package com.github.enteraname74.soulsearching.composables.bottomsheets.music.addtoplaylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.usecase.musicplaylist.CommonMusicPlaylistUseCase
import com.github.enteraname74.domain.usecase.playlist.CommonPlaylistUseCase
import com.github.enteraname74.soulsearching.composables.dialog.CreatePlaylistDialog
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class AddToPlaylistBottomSheetViewModel(
    private val commonPlaylistUseCase: CommonPlaylistUseCase,
    private val commonMusicPlaylistUseCase: CommonMusicPlaylistUseCase,
    private val params: AddToPlaylistBottomSheetDestination,
    private val navScope: AddToPlaylistBottomSheetNavScope,
) : ViewModel() {
    private val _dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)
    val dialogState: StateFlow<SoulDialog?> = _dialogState.asStateFlow()

    private val selectedPlaylistIds: MutableStateFlow<Set<UUID>> = MutableStateFlow(emptySet())

    private val playlistsWithMusics: Flow<List<PlaylistWithMusics>> =
        commonPlaylistUseCase.getAllWithMusics().map { playlist ->
            // If we have only one music, we will show only the playlists that don't already have the music
            if (params.selectedMusicIds.size == 1) {
                playlist.filter {
                    it.musics.none { music ->
                        music.musicId == params.selectedMusicIds.first()
                    }
                }
            } else {
                playlist
            }
        }

    val state: StateFlow<AddToPlaylistBottomSheetState> = combine(
        _dialogState,
        selectedPlaylistIds,
        playlistsWithMusics,
    ) { dialogState, selectedPlaylistIds, playlistsWithMusics ->
        AddToPlaylistBottomSheetState(
            dialogState = dialogState,
            selectedPlaylistIds = selectedPlaylistIds,
            playlistsWithMusics = playlistsWithMusics,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AddToPlaylistBottomSheetState()
    )

    fun showCreatePlaylistDialog() {
        _dialogState.value = CreatePlaylistDialog(
            onDismiss = { _dialogState.value = null },
            onConfirm = { playlistName ->
                viewModelScope.launch {
                    if (playlistName.isNotBlank()) {
                        val newPlaylist = Playlist(name = playlistName)
                        commonPlaylistUseCase.upsert(playlist = newPlaylist)
                        addMusicsToPlaylist(
                            playlistIds = listOf(newPlaylist.playlistId),
                        )
                    }

                    _dialogState.value = null
                    navScope.close()
                }
            }
        )
    }

    private suspend fun addMusicsToPlaylist(
        playlistIds: List<UUID>
    ) {
        params.selectedMusicIds.forEach { musicId ->
            playlistIds.forEach { playlistId ->
                commonMusicPlaylistUseCase.upsert(
                    MusicPlaylist(
                        musicId = musicId,
                        playlistId = playlistId,
                    )
                )
            }
        }
    }

    fun toggleSelection(playlistId: UUID) {
        selectedPlaylistIds.value = if (selectedPlaylistIds.value.contains(playlistId)) {
            selectedPlaylistIds.value - playlistId
        } else {
            selectedPlaylistIds.value + playlistId
        }
    }

    fun confirm() {
        viewModelScope.launch {
            addMusicsToPlaylist(selectedPlaylistIds.value.toList())
            navScope.close()
        }
    }

    fun close() {
        navScope.close()
    }
}