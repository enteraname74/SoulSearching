package com.github.enteraname74.soulsearching.feature.elementpage.monthpage.domain

import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.soulsearching.domain.model.MonthMusicList
import com.github.enteraname74.soulsearching.domain.utils.Utils
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Handler for managing the SelectedFolderViewModel.
 */
class SelectedMonthViewModelHandler(
    private val coroutineScope: CoroutineScope,
    private val playlistRepository: PlaylistRepository,
    private val musicRepository: MusicRepository,
    private val musicPlaylistRepository: MusicPlaylistRepository,
    private val playbackManager: PlaybackManager
) : ViewModelHandler {
    private var _selectedMonth: StateFlow<MonthMusicList?> = MutableStateFlow(
        MonthMusicList()
    )

    private val _playlists = playlistRepository.getAllPlaylistsWithMusicsSortByNameAscAsFlow()
        .stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    private val _state = MutableStateFlow(SelectedMonthState())
    var state = combine(
        _state,
        _playlists
    ) { state, playlists ->
        state.copy(
            allPlaylists = playlists
        )
    }.stateIn(
        coroutineScope,
        SharingStarted.WhileSubscribed(5000),
        SelectedMonthState()
    )

    /**
     * Handles events of the selected folder screen.
     */
    fun onEvent(event: SelectedMonthEvent) {
        when (event) {
            is SelectedMonthEvent.AddMusicToPlaylists -> addMusicToPlaylists(
                musicId = event.musicId,
                selectedPlaylistsIds = event.selectedPlaylistsIds
            )

            is SelectedMonthEvent.DeleteMusic -> deleteMusicFromApp(musicId = event.musicId)
            is SelectedMonthEvent.SetAddToPlaylistBottomSheetVisibility -> showOrHideAddToPlaylistBottomSheet(
                isShown = event.isShown
            )

            is SelectedMonthEvent.SetDeleteMusicDialogVisibility -> showOrHideDeleteDialog(isShown = event.isShown)
            is SelectedMonthEvent.SetMusicBottomSheetVisibility -> showOrHideMusicBottomSheet(
                isShown = event.isShown
            )

            is SelectedMonthEvent.SetSelectedMonth -> setSelectedFolder(month = event.month)
            is SelectedMonthEvent.ToggleQuickAccessState -> toggleQuickAccessState(musicId = event.musicId)
            is SelectedMonthEvent.AddNbPlayed -> incrementNbPlayed(playlistId = event.playlistId)
        }
    }

    /**
     * Add a music to multiple playlists.
     */
    private fun addMusicToPlaylists(musicId: UUID, selectedPlaylistsIds: List<UUID>) {
        CoroutineScope(Dispatchers.IO).launch {
            for (selectedPlaylistId in selectedPlaylistsIds) {
                musicPlaylistRepository.insertMusicIntoPlaylist(
                    MusicPlaylist(
                        musicId = musicId,
                        playlistId = selectedPlaylistId
                    )
                )
            }
            val music = musicRepository.getMusicFromId(musicId = musicId)
            playbackManager.updateMusic(music = music)
        }
    }

    /**
     * Remove the selected music, from the MusicState, from the application
     */
    private fun deleteMusicFromApp(musicId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            musicRepository.delete(musicId = musicId)
            playbackManager.removeSongFromLists(musicId = musicId)
        }
    }

    /**
     * Toggle the quick access state of the selected music.
     */
    private fun toggleQuickAccessState(musicId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            musicRepository.toggleQuickAccessState(musicId = musicId)
        }
    }

    /**
     * Show or hide the delete dialog.
     */
    private fun showOrHideDeleteDialog(isShown: Boolean) {
        _state.update {
            it.copy(
                isDeleteMusicDialogShown = isShown
            )
        }
    }

    /**
     * Show or hide the add to playlist bottom sheet.
     */
    private fun showOrHideAddToPlaylistBottomSheet(isShown: Boolean) {
        _state.update {
            it.copy(
                isAddToPlaylistBottomSheetShown = isShown
            )
        }
    }

    /**
     * Show or hide the music bottom sheet.
     */
    private fun showOrHideMusicBottomSheet(isShown: Boolean) {
        _state.update {
            it.copy(
                isMusicBottomSheetShown = isShown
            )
        }
    }

    /**
     * Set the selected playlist.
     */
    private fun setSelectedFolder(month: String) {
        _selectedMonth = musicRepository
            .getAllMusicsSortByNameAscAsFlow()
            .map { allMusics ->
                val musics = allMusics.filter { Utils.getMonthAndYearOfDate(date = it.addedDate) == month  }
                MonthMusicList(
                    month = month,
                    musics = musics,
                    coverId = musics.firstOrNull { it.coverId != null }?.coverId
                )
            }
            .stateIn(
                coroutineScope, SharingStarted.WhileSubscribed(), MonthMusicList()
            )

        state = combine(
            _state,
            _selectedMonth,
            _playlists
        ) { state, monthMusics, playlists ->
            state.copy(
                monthMusicList = monthMusics,
                allPlaylists = playlists
            )
        }.stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(5000),
            SelectedMonthState()
        )

        _state.update {
            it.copy(
                monthMusicList = _selectedMonth.value
            )
        }
    }

    /**
     * Increment by one the number of time a playlist was played.
     */
    private fun incrementNbPlayed(playlistId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            playlistRepository.updateNbPlayed(
                newNbPlayed = playlistRepository.getNbPlayedOfPlaylist(playlistId) + 1,
                playlistId = playlistId
            )
        }
    }
}