package com.github.enteraname74.soulsearching.commondelegate

import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.usecase.music.DeleteMusicUseCase
import com.github.enteraname74.domain.usecase.music.UpsertMusicUseCase
import com.github.enteraname74.domain.usecase.musicplaylist.DeleteMusicFromPlaylistUseCase
import com.github.enteraname74.domain.usecase.musicplaylist.UpsertMusicIntoPlaylistUseCase
import com.github.enteraname74.domain.usecase.playlist.UpsertPlaylistUseCase
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.AddToPlaylistBottomSheet
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.MusicBottomSheet
import com.github.enteraname74.soulsearching.composables.dialog.CreatePlaylistDialog
import com.github.enteraname74.soulsearching.composables.dialog.DeleteMusicDialog
import com.github.enteraname74.soulsearching.composables.dialog.RemoveMusicFromPlaylistDialog
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionManagerImpl
import com.github.enteraname74.soulsearching.domain.model.types.MusicBottomSheetState
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

interface MusicBottomSheetDelegate {
    fun showMusicBottomSheet(
        selectedMusic: Music,
        currentPlaylist: Playlist? = null,
    )
}

class MusicBottomSheetDelegateImpl(
    private val deleteMusicUseCase: DeleteMusicUseCase,
    private val upsertMusicIntoPlaylistUseCase: UpsertMusicIntoPlaylistUseCase,
    private val deleteMusicFromPlaylistUseCase: DeleteMusicFromPlaylistUseCase,
    private val upsertMusicUseCase: UpsertMusicUseCase,
    private val playbackManager: PlaybackManager,
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
                    CoroutineScope(Dispatchers.IO).launch {
                        deleteMusicUseCase(music = musicToDelete)
                    }
                    setDialogState(null)
                    // We make sure to close the bottom sheet after deleting the selected music.
                    setBottomSheetState(null)
                    multiSelectionManagerImpl?.clearMultiSelection()
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
                    CoroutineScope(Dispatchers.IO).launch {
                        deleteMusicFromPlaylistUseCase(
                            musicId = musicToRemove.musicId,
                            playlistId = currentPlaylist.playlistId,
                        )
                    }
                    setDialogState(null)
                    // We make sure to close the bottom sheet after removing the selected music from the playlist.
                    setBottomSheetState(null)
                    multiSelectionManagerImpl?.clearMultiSelection()

                },
                onClose = { setDialogState(null) }
            )
        )
    }

    /**
     * Add a music to multiple playlists.
     */
    private fun addMusicToPlaylists(music: Music, selectedPlaylists: List<PlaylistWithMusics>) {
        CoroutineScope(Dispatchers.IO).launch {
            for (selectedPlaylist in selectedPlaylists) {
                upsertMusicIntoPlaylistUseCase(
                    MusicPlaylist(
                        musicId = music.musicId,
                        playlistId = selectedPlaylist.playlist.playlistId,
                    )
                )
            }
        }
    }

    private fun showAddToPlaylistsBottomSheet(musicToAdd: Music) {
        setAddToPlaylistBottomSheetState(
            AddToPlaylistBottomSheet(
                onClose = { setAddToPlaylistBottomSheetState(null) },
                addMusicToSelectedPlaylists = { selectedPlaylists ->
                    addMusicToPlaylists(
                        music = musicToAdd,
                        selectedPlaylists = selectedPlaylists,
                    )
                    multiSelectionManagerImpl?.clearMultiSelection()
                    setBottomSheetState(null)
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
                    CoroutineScope(Dispatchers.IO).launch {
                        upsertMusicUseCase(
                            music = selectedMusic.copy(
                                isInQuickAccess = !selectedMusic.isInQuickAccess,
                            )
                        )
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

