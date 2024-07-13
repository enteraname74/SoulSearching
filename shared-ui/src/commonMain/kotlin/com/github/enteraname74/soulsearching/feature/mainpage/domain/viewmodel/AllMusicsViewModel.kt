package com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import com.github.enteraname74.domain.model.SoulSearchingSettings
import com.github.enteraname74.domain.usecase.music.DeleteMusicUseCase
import com.github.enteraname74.domain.usecase.music.GetAllMusicsSortedUseCase
import com.github.enteraname74.domain.usecase.music.ToggleMusicFavoriteStatusUseCase
import com.github.enteraname74.domain.usecase.music.UpsertMusicUseCase
import com.github.enteraname74.domain.usecase.musicalbum.GetAlbumIdFromMusicIdUseCase
import com.github.enteraname74.domain.usecase.musicartist.GetArtistIdFromMusicIdUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsUseCase
import com.github.enteraname74.soulsearching.domain.events.MusicEvent
import com.github.enteraname74.soulsearching.domain.model.MonthMusicList
import com.github.enteraname74.soulsearching.domain.model.MusicFetcher
import com.github.enteraname74.soulsearching.domain.model.MusicFolder
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.domain.utils.Utils
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.MainPageState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*

/**
 * Handler for managing the AllMusicsViewModel.
 */
@Suppress("Deprecation")
abstract class AllMusicsViewModel(
    private val settings: SoulSearchingSettings,
    private val playbackManager: PlaybackManager,
    private val getAllMusicsSortedUseCase: GetAllMusicsSortedUseCase,
    getAllPlaylistWithMusicsUseCase: GetAllPlaylistWithMusicsUseCase,
    private val getArtistIdFromMusicIdUseCase: GetArtistIdFromMusicIdUseCase,
    private val getAlbumIdFromMusicIdUseCase: GetAlbumIdFromMusicIdUseCase,
    private val musicFetcher: MusicFetcher,
    private val deleteMusicUseCase: DeleteMusicUseCase,
    private val toggleMusicFavoriteStatusUseCase: ToggleMusicFavoriteStatusUseCase,
    private val upsertMusicUseCase: UpsertMusicUseCase,
) : ScreenModel {
    var currentPage by mutableStateOf<ElementEnum?>(null)
    private val _sortType = MutableStateFlow(
        settings.getInt(
            SoulSearchingSettings.SORT_MUSICS_TYPE_KEY, SortType.NAME
        )
    )
    private val _sortDirection = MutableStateFlow(
        settings.getInt(
            SoulSearchingSettings.SORT_MUSICS_DIRECTION_KEY, SortDirection.ASC
        )
    )

    @OptIn(ExperimentalMaterialApi::class)
    val searchDraggableState: SwipeableState<BottomSheetStates> =
        SwipeableState(initialValue = BottomSheetStates.COLLAPSED)


    @OptIn(ExperimentalCoroutinesApi::class)
    private val _musics = _sortDirection.flatMapLatest { sortDirection ->
        _sortType.flatMapLatest { sortType ->
            getAllMusicsSortedUseCase(
                sortDirection = sortDirection,
                sortType = sortType,
            )
        }
    }.stateIn(
        screenModelScope,
        SharingStarted.Eagerly,
        ArrayList()
    )


    private val _state = MutableStateFlow(MainPageState())

    val state = combine(
        _state,
        _musics,
        _sortType,
        _sortDirection,
        getAllPlaylistWithMusicsUseCase()
    ) { state, musics, sortType, sortDirection, playlists ->
        state.copy(
            musics = musics,
            sortType = sortType,
            sortDirection = sortDirection,
            allPlaylists = playlists,
            monthMusics = buildMonthMusics(allMusics = musics),
            folderMusics = buildMusicFolders(allMusics = musics)
        )
    }.stateIn(
        screenModelScope,
        SharingStarted.WhileSubscribed(5000),
        MainPageState()
    )

    /**
     * Build a list of music folders.
     */
    private fun buildMusicFolders(allMusics: List<Music>): List<MusicFolder> {
        return allMusics.groupBy { it.folder }.entries.map { (folder, musics) ->
            MusicFolder(
                path = folder,
                musics = musics,
                coverId = musics.firstOrNull { it.coverId != null }?.coverId
            )
        }
    }



    /**
     * Build a list of month musics.
     */
    private fun buildMonthMusics(allMusics: List<Music>): List<MonthMusicList> {
        return allMusics.groupBy { Utils.getMonthAndYearOfDate(date = it.addedDate) }.entries.map { (date, musics) ->
            MonthMusicList(
                month = date,
                musics = musics,
                coverId = musics.firstOrNull { it.coverId != null }?.coverId
            )
        }
    }

    /**
     * Retrieve the artist id of a music.
     */
    fun getArtistIdFromMusicId(musicId: UUID): UUID? {
        return runBlocking(context = Dispatchers.IO) {
            getArtistIdFromMusicIdUseCase(musicId)
        }
    }

    /**
     * Retrieve the album id of a music.
     */
    fun getAlbumIdFromMusicId(musicId: UUID): UUID? {
        return runBlocking(context = Dispatchers.IO) {
            getAlbumIdFromMusicIdUseCase(musicId)
        }
    }

    /**
     * Fetch all musics.
     */
    suspend fun fetchMusics(
        updateProgress: (Float) -> Unit,
        finishAction: () -> Unit
    ) {
        musicFetcher.fetchMusics(
            updateProgress = updateProgress,
            finishAction = finishAction
        )
    }

    /**
     * Check all musics and delete the one that does not exists (if the path of the music is not valid).
     */
    abstract fun checkAndDeleteMusicIfNotExist()

    /**
     * handle a music event.
     */
    fun onMusicEvent(event: MusicEvent) {
        when(event) {
            is MusicEvent.DeleteDialog -> showOrHideDeleteDialog(isShown = event.isShown)
            is MusicEvent.RemoveFromPlaylistDialog -> showOrHideRemoveFromPlaylistDialog(event)
            is MusicEvent.BottomSheet -> showOrHideMusicBottomSheet(event)
            is MusicEvent.AddToPlaylistBottomSheet -> showOrHideAddToPlaylistBottomSheet(event)
            is MusicEvent.SetSelectedMusic -> setSelectedMusic(event)
            is MusicEvent.DeleteMusic -> deleteMusicFromApp(musicId = event.musicId)
            is MusicEvent.SetSortType -> setSortType(newSortType = event.type)
            is MusicEvent.SetSortDirection -> setSortDirection(newSortDirection = event.direction)
            is MusicEvent.SetFavorite -> toggleFavoriteState(musicId = event.musicId)
            is MusicEvent.ToggleQuickAccessState -> toggleQuickAccessState(music = event.music)
        }
    }

    /**
     * Show or hide the delete dialog.
     */
    private fun showOrHideDeleteDialog(isShown: Boolean) {
        _state.update {
            it.copy(
                isDeleteDialogShown = isShown
            )
        }
    }

    /**
     * Show or hide the remove from playlist dialog.
     */
    private fun showOrHideRemoveFromPlaylistDialog(event: MusicEvent.RemoveFromPlaylistDialog) {
        _state.update {
            it.copy(
                isRemoveFromPlaylistDialogShown = event.isShown
            )
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
     * Define the selected music of the state.
     */
    private fun setSelectedMusic(event: MusicEvent.SetSelectedMusic) {
        _state.update {
            it.copy(
                selectedMusic = event.music
            )
        }
    }

    /**
     * Show or hide the music bottom sheet.
     */
    private fun showOrHideMusicBottomSheet(event: MusicEvent.BottomSheet) {
        _state.update {
            it.copy(
                isBottomSheetShown = event.isShown
            )
        }
    }

    /**
     * Show or hide the add to playlist bottom sheet.
     */
    private fun showOrHideAddToPlaylistBottomSheet(event: MusicEvent.AddToPlaylistBottomSheet) {
        _state.update {
            it.copy(
                isAddToPlaylistBottomSheetShown = event.isShown
            )
        }
    }

    /**
     * Set the sort type.
     */
    private fun setSortType(newSortType: Int) {
        _sortType.value = newSortType
        _state.update {
            it.copy(
                sortType = newSortType
            )
        }
        settings.setInt(
            key = SoulSearchingSettings.SORT_MUSICS_TYPE_KEY,
            value = newSortType
        )
    }

    /**
     * Set the sort direction.
     */
    private fun setSortDirection(newSortDirection: Int) {
        _sortDirection.value = newSortDirection
        _state.update {
            it.copy(
                sortDirection = newSortDirection
            )
        }
        settings.setInt(
            key = SoulSearchingSettings.SORT_MUSICS_DIRECTION_KEY,
            value = newSortDirection
        )
    }

    /**
     * Toggle the favorite status of a given music.
     */
    private fun toggleFavoriteState(musicId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            toggleMusicFavoriteStatusUseCase(musicId = musicId)
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
}