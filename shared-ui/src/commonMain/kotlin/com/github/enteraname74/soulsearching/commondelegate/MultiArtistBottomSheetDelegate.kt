package com.github.enteraname74.soulsearching.commondelegate

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.usecase.artist.DeleteAllArtistsUseCase
import com.github.enteraname74.domain.usecase.artist.GetArtistWithMusicsUseCase
import com.github.enteraname74.soulsearching.composables.bottomsheets.multiartist.MultiArtistBottomSheet
import com.github.enteraname74.soulsearching.composables.dialog.DeleteMultiArtistDialog
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

interface MultiArtistBottomSheetDelegate {
    fun showMultiArtistBottomSheet()
}

class MultiArtistBottomSheetDelegateImpl(
    private val deleteAllArtistsUseCase: DeleteAllArtistsUseCase,
    private val getArtistWithMusicsUseCase: GetArtistWithMusicsUseCase,
    private val loadingManager: LoadingManager,
    private val playbackManager: PlaybackManager,
) : MultiArtistBottomSheetDelegate {
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

    private fun showDeleteMultiArtistDialog(
        selectedIdsToDelete: List<UUID>,
    ) {
        setDialogState(
            DeleteMultiArtistDialog(
                onDelete = {
                    CoroutineScope(Dispatchers.IO).launch {
                        loadingManager.withLoading {
                            selectedIdsToDelete.forEach { albumId ->
                                getArtistWithMusicsUseCase(
                                    artistId = albumId
                                ).firstOrNull()?.let { artistWithMusics ->
                                    playbackManager.removeSongsFromPlayedPlaylist(
                                        musicIds = artistWithMusics.musics.map { it.musicId },
                                    )
                                }
                            }
                            deleteAllArtistsUseCase(selectedIdsToDelete)
                            multiSelectionManagerImpl?.clearMultiSelection()
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
        val selectedIds = multiSelectionManagerImpl?.state?.value?.selectedIds ?: return

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
                                    getArtistWithMusicsUseCase(
                                        artistId = artistId
                                    ).firstOrNull()?.let { artistWithMusics ->
                                        addAll(artistWithMusics.musics)
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
                                .forEach { artistId ->
                                    getArtistWithMusicsUseCase(
                                        artistId = artistId
                                    ).firstOrNull()?.let { artistWithMusics ->
                                        addAll(artistWithMusics.musics)
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
                        selectedIds.forEach { artistId ->
                            getArtistWithMusicsUseCase(
                                artistId = artistId
                            ).firstOrNull()?.let { artistWithMusics ->
                                playbackManager.removeSongsFromPlayedPlaylist(
                                    musicIds = artistWithMusics.musics.map { it.musicId },
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