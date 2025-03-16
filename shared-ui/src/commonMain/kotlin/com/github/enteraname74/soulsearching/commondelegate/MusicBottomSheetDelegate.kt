package com.github.enteraname74.soulsearching.commondelegate

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.usecase.music.DeleteMusicUseCase
import com.github.enteraname74.domain.usecase.music.ToggleMusicQuickAccessStateUseCase
import com.github.enteraname74.domain.usecase.playlist.AddMusicsToPlaylistUseCase
import com.github.enteraname74.domain.usecase.playlist.RemoveMusicsFromPlaylistUseCase
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.AddToPlaylistBottomSheet
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.MusicBottomSheet
import com.github.enteraname74.soulsearching.composables.dialog.DeleteMusicDialog
import com.github.enteraname74.soulsearching.composables.dialog.RemoveMusicFromPlaylistDialog
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionManagerImpl
import com.github.enteraname74.soulsearching.domain.model.types.MusicBottomSheetState
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface MusicBottomSheetDelegate {
    fun showMusicBottomSheet(
        selectedMusic: Music,
        currentPlaylist: Playlist? = null,
    )
}

class MusicBottomSheetDelegateImpl(
    private val deleteMusicUseCase: DeleteMusicUseCase,
    private val addMusicsToPlaylistUseCase: AddMusicsToPlaylistUseCase,
    private val removeMusicsFromPlaylistUseCase: RemoveMusicsFromPlaylistUseCase,
    private val toggleMusicQuickAccessStateUseCase: ToggleMusicQuickAccessStateUseCase,
    private val playbackManager: PlaybackManager,
    private val loadingManager: LoadingManager,
    private val feedbackPopUpManager: FeedbackPopUpManager,
) : MusicBottomSheetDelegate {

    private var setDialogState: (SoulDialog?) -> Unit = {}
    private var setBottomSheetState: (SoulBottomSheet?) -> Unit = {}
    private var setAddToPlaylistBottomSheetState: (AddToPlaylistBottomSheet?) -> Unit = {}
    private var onModifyMusic: (music: Music) -> Unit = {}
    private var getAllPlaylistsWithMusics: () -> List<PlaylistWithMusics> = { emptyList() }
    private var musicBottomSheetState: MusicBottomSheetState = MusicBottomSheetState.NORMAL
    private var multiSelectionManagerImpl: MultiSelectionManagerImpl? = null

    fun initDelegate(
        setDialogState: (SoulDialog?) -> Unit,
        setBottomSheetState: (SoulBottomSheet?) -> Unit,
        setAddToPlaylistBottomSheetState: (AddToPlaylistBottomSheet?) -> Unit,
        getAllPlaylistsWithMusics: () -> List<PlaylistWithMusics>,
        onModifyMusic: (music: Music) -> Unit,
        musicBottomSheetState: MusicBottomSheetState = MusicBottomSheetState.NORMAL,
        multiSelectionManagerImpl: MultiSelectionManagerImpl
    ) {
        this.setDialogState = setDialogState
        this.setBottomSheetState = setBottomSheetState
        this.setAddToPlaylistBottomSheetState = setAddToPlaylistBottomSheetState
        this.getAllPlaylistsWithMusics = getAllPlaylistsWithMusics
        this.onModifyMusic = onModifyMusic
        this.musicBottomSheetState = musicBottomSheetState
        this.multiSelectionManagerImpl = multiSelectionManagerImpl
    }

    private fun showDeleteMusicDialog(musicToDelete: Music) {
        setDialogState(
            DeleteMusicDialog(
                musicToDelete = musicToDelete,
                onDelete = {
                    setDialogState(null)
                    // We make sure to close the bottom sheet after deleting the selected music.
                    setBottomSheetState(null)

                    loadingManager.withLoadingOnIO {
                        val result: SoulResult<Unit> = deleteMusicUseCase(music = musicToDelete)
                        feedbackPopUpManager.showResultErrorIfAny(result = result)
                        multiSelectionManagerImpl?.clearMultiSelection()
                    }
                },
                onClose = { setDialogState(null) }
            )
        )
    }

    private fun removeMusicFromPlaylistDialog(
        musicToRemove: Music,
        currentPlaylist: Playlist,
    ) {
        setDialogState(
            RemoveMusicFromPlaylistDialog(
                onConfirm = {
                    setDialogState(null)
                    // We make sure to close the bottom sheet after removing the selected music from the playlist.
                    setBottomSheetState(null)

                    loadingManager.withLoadingOnIO {
                        val result: SoulResult<Unit> = removeMusicsFromPlaylistUseCase(
                            playlistId = currentPlaylist.playlistId,
                            musicIds = listOf(musicToRemove.musicId),
                        )
                        feedbackPopUpManager.showResultErrorIfAny(result)
                        multiSelectionManagerImpl?.clearMultiSelection()
                    }
                },
                onClose = { setDialogState(null) }
            )
        )
    }

    private fun showAddToPlaylistsBottomSheet(musicToAdd: Music) {
        setAddToPlaylistBottomSheetState(
            AddToPlaylistBottomSheet(
                onClose = { setAddToPlaylistBottomSheetState(null) },
                addMusicToSelectedPlaylists = { selectedPlaylists ->
                    multiSelectionManagerImpl?.clearMultiSelection()
                    setAddToPlaylistBottomSheetState(null)
                    setBottomSheetState(null)

                    loadingManager.withLoadingOnIO {
                        selectedPlaylists.forEach { playlistWithMusics ->
                            val result: SoulResult<Unit> = addMusicsToPlaylistUseCase(
                                musicIds = listOf(musicToAdd.musicId),
                                playlistId = playlistWithMusics.playlist.playlistId,
                            )
                            feedbackPopUpManager.showResultErrorIfAny(result)
                            multiSelectionManagerImpl?.clearMultiSelection()
                        }
                    }
                },
                playlistsWithMusics = getAllPlaylistsWithMusics().filter {
                    it.musics.none { music -> music.musicId == musicToAdd.musicId }
                },
                setDialogState = setDialogState,
                selectedMusicIds = listOf(musicToAdd.musicId),
            )
        )
    }

    override fun showMusicBottomSheet(
        selectedMusic: Music,
        currentPlaylist: Playlist?,
    ) {
        setBottomSheetState(
            MusicBottomSheet(
                musicBottomSheetState = musicBottomSheetState,
                selectedMusic = selectedMusic,
                onClose = { setBottomSheetState(null) },
                onDeleteMusic = { showDeleteMusicDialog(musicToDelete = selectedMusic) },
                onModifyMusic = { onModifyMusic(selectedMusic)},
                onRemoveFromPlaylist = {
                    currentPlaylist?.let {
                        removeMusicFromPlaylistDialog(
                            musicToRemove = selectedMusic,
                            currentPlaylist = it,
                        )
                    }
                },
                onAddToPlaylist = { showAddToPlaylistsBottomSheet(musicToAdd = selectedMusic) },
                toggleQuickAccess = {
                    loadingManager.withLoadingOnIO {
                        val result: SoulResult<Unit> = toggleMusicQuickAccessStateUseCase(music = selectedMusic)
                        feedbackPopUpManager.showResultErrorIfAny(result)
                        multiSelectionManagerImpl?.clearMultiSelection()
                    }
                },
                onPlayNext = {
                    CoroutineScope(Dispatchers.IO).launch {
                        playbackManager.addMusicToPlayNext(music = selectedMusic)
                        multiSelectionManagerImpl?.clearMultiSelection()
                    }
                },
                onRemoveFromPlayedList = {
                    CoroutineScope(Dispatchers.IO).launch {
                        playbackManager.removeSongsFromPlayedPlaylist(
                            musicIds = listOf(selectedMusic.musicId)
                        )
                        multiSelectionManagerImpl?.clearMultiSelection()
                    }
                }
            )
        )
    }
}

