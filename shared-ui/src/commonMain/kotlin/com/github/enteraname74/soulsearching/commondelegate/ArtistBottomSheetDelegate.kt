package com.github.enteraname74.soulsearching.commondelegate

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.usecase.artist.DeleteArtistUseCase
import com.github.enteraname74.domain.usecase.artist.UpsertArtistUseCase
import com.github.enteraname74.soulsearching.composables.bottomsheets.artist.ArtistBottomSheet
import com.github.enteraname74.soulsearching.composables.dialog.DeleteArtistDialog
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface ArtistBottomSheetDelegate {
    fun showArtistBottomSheet(selectedArtist: ArtistWithMusics)
}

class ArtistBottomSheetDelegateImpl(
    private val deleteArtistUseCase: DeleteArtistUseCase,
    private val upsertArtistUseCase: UpsertArtistUseCase,
) : ArtistBottomSheetDelegate {
    private var setDialogState: (SoulDialog?) -> Unit = {}
    private var setBottomSheetState: (SoulBottomSheet?) -> Unit = {}
    private var onModifyArtist: (artist: Artist) -> Unit = {}

    fun initDelegate(
        setDialogState: (SoulDialog?) -> Unit,
        setBottomSheetState: (SoulBottomSheet?) -> Unit,
        onModifyArtist: (artist: Artist) -> Unit,
    ) {
        this.setDialogState = setDialogState
        this.setBottomSheetState = setBottomSheetState
        this.onModifyArtist = onModifyArtist
    }

    private fun showDeleteArtistDialog(artistWithMusics: ArtistWithMusics) {
        setDialogState(
            DeleteArtistDialog(
                artistToDelete = artistWithMusics,
                onDelete = {
                    CoroutineScope(Dispatchers.IO).launch {
                        deleteArtistUseCase(artistWithMusics)
                    }
                    setDialogState(null)
                    // We make sure to close the bottom sheet after deleting the selected music.
                    setBottomSheetState(null)
                },
                onClose = { setDialogState(null) }
            )
        )
    }

    override fun showArtistBottomSheet(selectedArtist: ArtistWithMusics) {
        setBottomSheetState(
            ArtistBottomSheet(
                selectedArtist = selectedArtist.artist,
                onClose = { setBottomSheetState(null) },
                onDeleteArtist = { showDeleteArtistDialog(artistWithMusics = selectedArtist) },
                onModifyArtist = { onModifyArtist(selectedArtist.artist) },
                toggleQuickAccess = {
                    CoroutineScope(Dispatchers.IO).launch {
                        upsertArtistUseCase(
                            artist = selectedArtist.artist.copy(
                                isInQuickAccess = !selectedArtist.artist.isInQuickAccess,
                            )
                        )
                    }
                }
            )
        )
    }
}