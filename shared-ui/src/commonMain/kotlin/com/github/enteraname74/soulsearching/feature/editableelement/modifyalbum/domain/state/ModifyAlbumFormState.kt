package com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.domain.state

import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.SoulDropdownTextFieldHolderImpl
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder

sealed interface ModifyAlbumFormState {
    data object NoData : ModifyAlbumFormState
    data class Data(
        private val initialAlbum: AlbumWithMusics,
        private val updateFoundAlbums: suspend (name: String) -> List<String>,
        private val updateFoundArtists: suspend (name: String) -> List<String>,
    ) : ModifyAlbumFormState {
        val textFields: List<SoulTextFieldHolder> = listOf(
            SoulDropdownTextFieldHolderImpl(
                id = ALBUM_NAME,
                isValid = { it.isNotBlank() },
                initialValue = initialAlbum.album.albumName,
                updateProposedValues = updateFoundAlbums,
                getLabel = { strings.albumName },
            ),
            SoulDropdownTextFieldHolderImpl(
                id = ARTIST_NAME,
                isValid = { it.isNotBlank() },
                initialValue = initialAlbum.artist?.artistName.orEmpty(),
                updateProposedValues = updateFoundArtists,
                getLabel = { strings.artistName },
            ),
        )

        fun isFormValid(): Boolean = textFields.all { it.isValid() }

        fun getAlbumName(): String = textFields[0].value
        fun getArtistName(): String = textFields[1].value

        companion object {
            private const val ALBUM_NAME = "ALBUM_NAME"
            private const val ARTIST_NAME = "ARTIST_NAME"
        }
    }
}