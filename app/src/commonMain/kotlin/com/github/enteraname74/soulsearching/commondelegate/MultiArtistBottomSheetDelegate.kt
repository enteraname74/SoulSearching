package com.github.enteraname74.soulsearching.commondelegate

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.soulsearching.composables.bottomsheets.multiartist.MultiArtistBottomSheet
import com.github.enteraname74.soulsearching.composables.dialog.DeleteMultiArtistDialog
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

interface MultiArtistBottomSheetDelegate {
    fun showMultiArtistBottomSheet()
}

class MultiArtistBottomSheetDelegateImpl(
    private val commonArtistUseCase: CommonArtistUseCase,
    private val loadingManager: LoadingManager,
    private val playbackManager: PlaybackManager,
) : MultiArtistBottomSheetDelegate {
    private var setDialogState: (SoulDialog?) -> Unit = {}
    private var setBottomSheetState: (SoulBottomSheet?) -> Unit = {}
    private var multiSelectionManager: MultiSelectionManager? = null

    private fun showDeleteMultiArtistDialog(
        selectedIdsToDelete: List<UUID>,
    ) {
        setDialogState(
            DeleteMultiArtistDialog(
                onDelete = {
                    CoroutineScope(Dispatchers.IO).launch {
                        loadingManager.withLoading {
                            selectedIdsToDelete.forEach { albumId ->
                                commonArtistUseCase.getArtistWithMusic(
                                    artistId = albumId
                                ).firstOrNull()?.let { artistWithMusics ->
                                    playbackManager.removeSongsFromPlayedPlaylist(
                                        musicIds = artistWithMusics.musics.map { it.musicId },
                                    )
                                }
                            }
                            commonArtistUseCase.deleteAll(selectedIdsToDelete)
                            multiSelectionManager?.clearMultiSelection()
                        }
                        setDialogState(null)
                        setBottomSheetState(null)
                    }
                },
                onClose = { setDialogState(null) }
            )
        )
    }

    override fun showMultiArtistBottomSheet() {
        val selectedIds = multiSelectionManager?.state?.value?.selectedIds ?: return

        setBottomSheetState(
            MultiArtistBottomSheet(
                onClose = { setBottomSheetState(null) },
                selectedIds = selectedIds,
                onDelete = { showDeleteMultiArtistDialog(selectedIds) },
                onPlayNext = {
                    CoroutineScope(Dispatchers.IO).launch {
                        val musics: List<Music> = buildList {
                            selectedIds
                                .forEach { artistId ->
                                    commonArtistUseCase.getArtistWithMusic(
                                        artistId = artistId
                                    ).firstOrNull()?.let { artistWithMusics ->
                                        addAll(artistWithMusics.musics)
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
                                .forEach { artistId ->
                                    commonArtistUseCase.getArtistWithMusic(
                                        artistId = artistId
                                    ).firstOrNull()?.let { artistWithMusics ->
                                        addAll(artistWithMusics.musics)
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
                        selectedIds.forEach { artistId ->
                            commonArtistUseCase.getArtistWithMusic(
                                artistId = artistId
                            ).firstOrNull()?.let { artistWithMusics ->
                                playbackManager.removeSongsFromPlayedPlaylist(
                                    musicIds = artistWithMusics.musics.map { it.musicId },
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