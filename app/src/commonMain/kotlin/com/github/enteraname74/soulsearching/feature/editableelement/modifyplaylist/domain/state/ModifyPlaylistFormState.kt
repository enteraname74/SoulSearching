package com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state

import com.github.enteraname74.soulsearching.domain.model.Playlist
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolderImpl
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec

sealed interface ModifyPlaylistFormState {
    data object NoData : ModifyPlaylistFormState
    data class Data(
        private val initialPlaylist: Playlist
    ) : ModifyPlaylistFormState {
        val textFields: List<SoulTextFieldHolder> = listOf(
            SoulTextFieldHolderImpl(
                id = PLAYLIST_NAME,
                initialValue = initialPlaylist.name,
                isValid = { it.isNotBlank() },
                getLabel = { CoreRes.string.playlist_name.toTextSpec() },
                getError = { CoreRes.string.field_cannot_be_empty.toTextSpec() },
            )
        )

        fun getPlaylistName(): String = textFields.first().value

        fun isFormValid(): Boolean = textFields.all { it.isValid() }

        companion object {
            private const val PLAYLIST_NAME = "PLAYLIST_NAME"
        }
    }
}
