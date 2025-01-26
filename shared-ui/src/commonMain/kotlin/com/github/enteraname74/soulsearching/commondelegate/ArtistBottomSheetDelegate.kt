package com.github.enteraname74.soulsearching.commondelegate

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.usecase.artist.DeleteArtistUseCase
import com.github.enteraname74.domain.usecase.artist.ToggleArtistQuickAccessStateUseCase
import com.github.enteraname74.soulsearching.composables.bottomsheets.artist.ArtistBottomSheet
import com.github.enteraname74.soulsearching.composables.dialog.DeleteArtistDialog
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionManagerImpl
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface ArtistBottomSheetDelegate {
    fun showArtistBottomSheet(selectedArtist: ArtistWithMusics)
}

class ArtistBottomSheetDelegateImpl(
    private val deleteArtistUseCase: DeleteArtistUseCase,
    private val toggleArtistQuickAccessStateUseCase: ToggleArtistQuickAccessStateUseCase,
    private val playbackManager: PlaybackManager,
    private val loadingManager: LoadingManager,
    private val feedbackPopUpManager: FeedbackPopUpManager,
) : ArtistBottomSheetDelegate {
    private var setDialogState: (SoulDialog?) -> Unit = {}
    private var setBottomSheetState: (SoulBottomSheet?) -> Unit = {}
    private var onModifyArtist: (artist: Artist) -> Unit = {}
    private var multiSelectionManagerImpl: MultiSelectionManagerImpl? = null

    fun initDelegate(
        setDialogState: (SoulDialog?) -> Unit,
        setBottomSheetState: (SoulBottomSheet?) -> Unit,
        onModifyArtist: (artist: Artist) -> Unit,
        multiSelectionManagerImpl: MultiSelectionManagerImpl,
    ) {
        this.setDialogState = setDialogState
        this.setBottomSheetState = setBottomSheetState
        this.onModifyArtist = onModifyArtist
        this.multiSelectionManagerImpl = multiSelectionManagerImpl
    }

    private fun showDeleteArtistDialog(artistWithMusics: ArtistWithMusics) {
        setDialogState(
            DeleteArtistDialog(
                artistToDelete = artistWithMusics,
                onDelete = {
                    CoroutineScope(Dispatchers.IO).launch {
                        loadingManager.withLoading {
                            val result: SoulResult<String> = deleteArtistUseCase(artistWithMusics)
                            feedbackPopUpManager.showResultErrorIfAny(result = result)
                        }
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

    override fun showArtistBottomSheet(selectedArtist: ArtistWithMusics) {
        setBottomSheetState(
            ArtistBottomSheet(
                selectedArtist = selectedArtist,
                onClose = { setBottomSheetState(null) },
                onDeleteArtist = { showDeleteArtistDialog(artistWithMusics = selectedArtist) },
                onModifyArtist = { onModifyArtist(selectedArtist.artist) },
                toggleQuickAccess = {
                    CoroutineScope(Dispatchers.IO).launch {
                        loadingManager.withLoading {
                            val result: SoulResult<String> = toggleArtistQuickAccessStateUseCase(
                                artist = selectedArtist.artist
                            )
                            feedbackPopUpManager.showResultErrorIfAny(result)
                        }
                        multiSelectionManagerImpl?.clearMultiSelection()
                        setBottomSheetState(null)
                    }
                },
                onPlayNext = {
                    CoroutineScope(Dispatchers.IO).launch {
                        playbackManager.addMultipleMusicsToPlayNext(
                            musics = selectedArtist.musics,
                        )
                        multiSelectionManagerImpl?.clearMultiSelection()
                        setBottomSheetState(null)
                    }
                },
                onRemoveFromPlayedList = {
                    CoroutineScope(Dispatchers.IO).launch {
                        playbackManager.removeSongsFromPlayedPlaylist(
                            musicIds = selectedArtist.musics.map { it.musicId },
                        )
                        multiSelectionManagerImpl?.clearMultiSelection()
                        setBottomSheetState(null)
                    }
                }
            )
        )
    }
}