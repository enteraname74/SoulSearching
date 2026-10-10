package com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.domain.state

import com.github.enteraname74.soulsearching.domain.model.ArtistWithMusics
import com.github.enteraname74.soulsearching.feature.editableelement.domain.CoverEditMode

/**
 * UI state of the modify artist screen.
 */
sealed interface ModifyArtistState {
    data object Loading : ModifyArtistState
    data class Data(
        val initialArtist: ArtistWithMusics,
        val coverEditMode: CoverEditMode,
    ) : ModifyArtistState
}
