package com.github.enteraname74.soulsearching.feature.elementpage.monthpage.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.usecase.music.DeleteMusicUseCase
import com.github.enteraname74.domain.usecase.music.GetAllMusicUseCase
import com.github.enteraname74.domain.usecase.music.GetMusicUseCase
import com.github.enteraname74.domain.usecase.music.UpsertMusicUseCase
import com.github.enteraname74.domain.usecase.musicplaylist.UpsertMusicIntoPlaylistUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsUseCase
import com.github.enteraname74.soulsearching.domain.model.MonthMusicList
import com.github.enteraname74.soulsearching.domain.utils.Utils
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class SelectedMonthViewModel(
    private val playbackManager: PlaybackManager,
    private val getAllPlaylistWithMusicsUseCase: GetAllPlaylistWithMusicsUseCase,
    private val getAllMusicUseCase: GetAllMusicUseCase,
    private val upsertMusicIntoPlaylistUseCase: UpsertMusicIntoPlaylistUseCase,
    private val getMusicUseCase: GetMusicUseCase,
    private val deleteMusicUseCase: DeleteMusicUseCase,
    private val upsertMusicUseCase: UpsertMusicUseCase,
) : ScreenModel {
    private var _selectedMonth: StateFlow<MonthMusicList?> = MutableStateFlow(
        MonthMusicList()
    )

    private val _state = MutableStateFlow(SelectedMonthState())
    var state = combine(
        _state,
        getAllPlaylistWithMusicsUseCase(),
    ) { state, playlists ->
        state.copy(
            allPlaylists = playlists
        )
    }.stateIn(
        screenModelScope,
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
            is SelectedMonthEvent.ToggleQuickAccessState -> toggleQuickAccessState(music = event.music)
            is SelectedMonthEvent.AddNbPlayed -> incrementNbPlayed(playlistId = event.playlistId)
        }
    }

    /**
     * Add a music to multiple playlists.
     */
    private fun addMusicToPlaylists(musicId: UUID, selectedPlaylistsIds: List<UUID>) {
        CoroutineScope(Dispatchers.IO).launch {
            for (selectedPlaylistId in selectedPlaylistsIds) {
                upsertMusicIntoPlaylistUseCase(
                    MusicPlaylist(
                        musicId = musicId,
                        playlistId = selectedPlaylistId
                    )
                )
            }
            getMusicUseCase(musicId = musicId).first()?.let { music ->
                playbackManager.updateMusic(music = music)
            }
        }
    }

    /**
     * Remove the selected music, from the MusicState, from the application
     */
    private fun deleteMusicFromApp(musicId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            deleteMusicUseCase(musicId = musicId)
            playbackManager.removeSongFromLists(musicId = musicId)
        }
    }

    /**
     * Toggle the quick access state of the selected music.
     */
    private fun toggleQuickAccessState(music: Music) {
        CoroutineScope(Dispatchers.IO).launch {
            upsertMusicUseCase(
                music = music.copy(
                    isInQuickAccess = !music.isInQuickAccess,
                )
            )
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
        _selectedMonth = getAllMusicUseCase()
            .map { allMusics ->
                val musics = allMusics.filter { Utils.getMonthAndYearOfDate(date = it.addedDate) == month  }
                MonthMusicList(
                    month = month,
                    musics = musics,
                    coverId = musics.firstOrNull { it.coverId != null }?.coverId
                )
            }
            .stateIn(
                screenModelScope, SharingStarted.WhileSubscribed(), MonthMusicList()
            )

        state = combine(
            _state,
            _selectedMonth,
            getAllPlaylistWithMusicsUseCase()
        ) { state, monthMusics, playlists ->
            state.copy(
                monthMusicList = monthMusics,
                allPlaylists = playlists
            )
        }.stateIn(
            screenModelScope,
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
    private fun incrementNbPlayed(playlistId: UUID) {}
}