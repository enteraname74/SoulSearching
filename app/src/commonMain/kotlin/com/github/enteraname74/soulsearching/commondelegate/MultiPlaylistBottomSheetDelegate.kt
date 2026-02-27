package com.github.enteraname74.soulsearching.commondelegate

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.usecase.playlist.CommonPlaylistUseCase
import com.github.enteraname74.soulsearching.composables.bottomsheets.multiplaylist.MultiPlaylistBottomSheet
import com.github.enteraname74.soulsearching.composables.dialog.DeleteMultiPlaylistDialog
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.feature.multiselection.MultiSelectionManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID

interface MultiPlaylistBottomSheetDelegate {
    fun showMultiPlaylistBottomSheet()
}

class MultiPlaylistBottomSheetDelegateImpl(
    private val commonPlaylistUseCase: CommonPlaylistUseCase,
    private val loadingManager: LoadingManager,
    private val playbackManager: PlaybackManager,
) : MultiPlaylistBottomSheetDelegate {
    private var setDialogState: (SoulDialog?) -> Unit = {}
    private var setBottomSheetState: (SoulBottomSheet?) -> Unit = {}
    private var multiSelectionManager: MultiSelectionManager? = null

    private fun showDeleteMultiPlaylistDialog(
        selectedIdsToDelete: List<UUID>,
    ) {
        setDialogState(
            DeleteMultiPlaylistDialog(
                onDelete = {
                    CoroutineScope(Dispatchers.IO).launch {
                        loadingManager.withLoading {
                            commonPlaylistUseCase.deleteAll(selectedIdsToDelete)
                            multiSelectionManager?.clearMultiSelection()
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
        val selectedIds = multiSelectionManager?.state?.value?.selectedIds ?: return

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
                                    commonPlaylistUseCase.getWithMusics(
                                        playlistId = playlistId
                                    ).firstOrNull()?.let { playlistWithMusics ->
                                        addAll(playlistWithMusics.musics)
                                    }
                                }
                        }

                        playbackManager.addMultipleMusicsToPlayNext(
                            musics = musics,
                        )

                        multiSelectionManager?.clearMultiSelection()
                        setBottomSheetState(null)
                    }
                },
                onAddToQueue = {
                    CoroutineScope(Dispatchers.IO).launch {
                        val musics: List<Music> = buildList {
                            selectedIds
                                .forEach { playlistId ->
                                    commonPlaylistUseCase.getWithMusics(
                                        playlistId = playlistId
                                    ).firstOrNull()?.let { playlistWithMusics ->
                                        addAll(playlistWithMusics.musics)
                                    }
                                }
                        }

                        playbackManager.addMultipleMusicsToQueue(
                            musics = musics,
                        )

                        multiSelectionManager?.clearMultiSelection()
                        setBottomSheetState(null)
                    }
                },
                onRemoveFromPlayedList = {
                    CoroutineScope(Dispatchers.IO).launch {
                        selectedIds.forEach { playlistId ->
                            commonPlaylistUseCase.getWithMusics(
                                playlistId = playlistId
                            ).firstOrNull()?.let { playlistWithMusics ->
                                playbackManager.removeSongsFromPlayedPlaylist(
                                    musicIds = playlistWithMusics.musics.map { it.musicId },
                                )
                            }
                        }

                        multiSelectionManager?.clearMultiSelection()
                        setBottomSheetState(null)
                    }
                }
            )
        )
    }
}