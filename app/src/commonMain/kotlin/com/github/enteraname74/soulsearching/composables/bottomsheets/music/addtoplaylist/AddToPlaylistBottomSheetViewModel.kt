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
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.feature.multiselection.MultiSelectionManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

class AddToPlaylistBottomSheetViewModel(
    private val commonPlaylistUseCase: CommonPlaylistUseCase,
    private val commonMusicPlaylistUseCase: CommonMusicPlaylistUseCase,
    private val loadingManager: LoadingManager,
    private val params: AddToPlaylistBottomSheetDestination,
    private val navScope: AddToPlaylistBottomSheetNavScope,
    private val multiSelectionManager: MultiSelectionManager,
) : ViewModel() {
    private val _dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)
    val dialogState: StateFlow<SoulDialog?> = _dialogState.asStateFlow()

    private val selectedPlaylistIds: MutableStateFlow<Set<Uuid>> = MutableStateFlow(emptySet())

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
                    loadingManager.withLoading {
                        if (playlistName.isNotBlank()) {
                            val newPlaylist = Playlist(name = playlistName)
                            commonPlaylistUseCase.upsert(playlist = newPlaylist)
                            addMusicsToPlaylist(
                                playlistIds = listOf(newPlaylist.playlistId),
                            )
                        }
                    }
                    _dialogState.value = null
                    multiSelectionManager.clearMultiSelection()
                    navScope.onSave()
                }
            }
        )
    }

    private suspend fun addMusicsToPlaylist(
        playlistIds: List<Uuid>
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

    fun toggleSelection(playlistId: Uuid) {
        selectedPlaylistIds.value = if (selectedPlaylistIds.value.contains(playlistId)) {
            selectedPlaylistIds.value - playlistId
        } else {
            selectedPlaylistIds.value + playlistId
        }
    }

    fun confirm() {
        viewModelScope.launch {
            loadingManager.withLoading {
                addMusicsToPlaylist(selectedPlaylistIds.value.toList())
            }
            multiSelectionManager.clearMultiSelection()
            navScope.onSave()
        }
    }

    fun navigateBack() {
        navScope.navigateBack()
    }
}
