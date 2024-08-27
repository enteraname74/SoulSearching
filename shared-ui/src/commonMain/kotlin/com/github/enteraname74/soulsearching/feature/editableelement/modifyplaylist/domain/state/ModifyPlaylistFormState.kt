package com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state

import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolderImpl

sealed interface ModifyPlaylistFormState {
    data object NoData: ModifyPlaylistFormState
    data class Data(
        private val initialPlaylist: Playlist
    ): ModifyPlaylistFormState {
        val textFields: List<SoulTextFieldHolder> = listOf(
            SoulTextFieldHolderImpl(
                id = PLAYLIST_NAME,
                initialValue = initialPlaylist.name,
                isValid = { it.isNotBlank() }
            )
        )

        fun getPlaylistName(): String = textFields.first().value

        fun isFormValid(): Boolean = textFields.all { it.isValid() }

        companion object {
            private const val PLAYLIST_NAME = "PLAYLIST_NAME"
        }
    }
}