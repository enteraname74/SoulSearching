package com.github.enteraname74.soulsearching.commondelegate

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.usecase.playlist.DeleteAllPlaylistsUseCase
import com.github.enteraname74.domain.usecase.playlist.GetPlaylistWithMusicsUseCase
import com.github.enteraname74.soulsearching.composables.bottomsheets.multiplaylist.MultiPlaylistBottomSheet
import com.github.enteraname74.soulsearching.composables.dialog.DeleteMultiPlaylistDialog
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionManagerImpl
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.*

interface MultiPlaylistBottomSheetDelegate {
    fun showMultiPlaylistBottomSheet()
}

class MultiPlaylistBottomSheetDelegateImpl(
    private val deleteAllPlaylistsUseCase: DeleteAllPlaylistsUseCase,
    private val getPlaylistWithMusicsUseCase: GetPlaylistWithMusicsUseCase,
    private val loadingManager: LoadingManager,
    private val playbackManager: PlaybackManager,
) : MultiPlaylistBottomSheetDelegate {
    private var setDialogState: (SoulDialog?) -> Unit = {}
    private var setBottomSheetState: (SoulBottomSheet?) -> Unit = {}
    private var multiSelectionManagerImpl: MultiSelectionManagerImpl? = null

    fun initDelegate(
        setDialogState: (SoulDialog?) -> Unit,
        setBottomSheetState: (SoulBottomSheet?) -> Unit,
        multiSelectionManagerImpl: MultiSelectionManagerImpl,
    ) {
        this.setDialogState = setDialogState
        this.setBottomSheetState = setBottomSheetState
        this.multiSelectionManagerImpl = multiSelectionManagerImpl
    }

    private fun showDeleteMultiPlaylistDialog(
        selectedIdsToDelete: List<UUID>,
    ) {
        setDialogState(
            DeleteMultiPlaylistDialog(
                onDelete = {
                    CoroutineScope(Dispatchers.IO).launch {
                        loadingManager.withLoading {
                            deleteAllPlaylistsUseCase(selectedIdsToDelete)
                            multiSelectionManagerImpl?.clearMultiSelection()
                            setDialogState(null)
                            setBottomSheetState(null)
                        }
                    }
                },
                onClose = { setDialogState(null) }
            )
        )
    }

    override fun showMultiPlaylistBottomSheet() {
        val selectedIds = multiSelectionManagerImpl?.state?.value?.selectedIds ?: return

        setBottomSheetState(
            MultiPlaylistBottomSheet(
                onClose = { setBottomSheetState(null) },
                selectedIds = selectedIds,
                onDelete = { showDeleteMultiPlaylistDialog(selectedIds) },
                onPlayNext = {
                    CoroutineScope(Dispatchers.IO).launch {
                        val musics: List<Music> = buildList {
                            selectedIds
                                .forEach { playlistId ->
                                    getPlaylistWithMusicsUseCase(
                                        playlistId = playlistId
                                    ).firstOrNull()?.let { playlistWithMusics ->
                                        addAll(playlistWithMusics.musics)
                                    }
                                }
                        }

                        playbackManager.addMultipleMusicsToPlayNext(
                            musics = musics,
                        )

                        multiSelectionManagerImpl?.clearMultiSelection()
                        setBottomSheetState(null)
                    }
                },
                onAddToQueue = {
                    CoroutineScope(Dispatchers.IO).launch {
                        val musics: List<Music> = buildList {
                            selectedIds
                                .forEach { playlistId ->
                                    getPlaylistWithMusicsUseCase(
                                        playlistId = playlistId
                                    ).firstOrNull()?.let { playlistWithMusics ->
                                        addAll(playlistWithMusics.musics)
                                    }
                                }
                        }

                        playbackManager.addMultipleMusicsToQueue(
                            musics = musics,
                        )

                        multiSelectionManagerImpl?.clearMultiSelection()
                        setBottomSheetState(null)
                    }
                },
                onRemoveFromPlayedList = {
                    CoroutineScope(Dispatchers.IO).launch {
                        selectedIds.forEach { playlistId ->
                            getPlaylistWithMusicsUseCase(
                                playlistId = playlistId
                            ).firstOrNull()?.let { playlistWithMusics ->
                                playbackManager.removeSongsFromPlayedPlaylist(
                                    musicIds = playlistWithMusics.musics.map { it.musicId },
                                )
                            }
                        }

                        multiSelectionManagerImpl?.clearMultiSelection()
                        setBottomSheetState(null)
                    }
                }
            )
        )
    }
}