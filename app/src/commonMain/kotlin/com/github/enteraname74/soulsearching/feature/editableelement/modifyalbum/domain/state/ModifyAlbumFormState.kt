package com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.domain.state

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.SoulDropdownTextFieldHolderImpl
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldStyle

sealed interface ModifyAlbumFormState {
    data object NoData : ModifyAlbumFormState
    data class Data(
        private val initialAlbum: AlbumWithMusics,
        private val updateFoundAlbums: suspend (name: String) -> List<String>,
        private val updateFoundArtists: suspend (name: String) -> List<String>,
    ) : ModifyAlbumFormState {
        val textFields: List<SoulTextFieldHolder> = listOf(
            SoulDropdownTextFieldHolderImpl(
                modifier = Modifier
                    .fillMaxWidth(),
                id = ALBUM_NAME,
                isValid = { it.isNotBlank() },
                initialValue = initialAlbum.album.albumName,
                updateProposedValues = updateFoundAlbums,
                getLabel = { strings.albumName },
                style = SoulTextFieldStyle.Top,
                getError = { strings.fieldCannotBeEmpty },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
            ),
            SoulDropdownTextFieldHolderImpl(
                modifier = Modifier
                    .fillMaxWidth(),
                id = ARTIST_NAME,
                isValid = { it.isNotBlank() },
                initialValue = initialAlbum.album.artist.artistName,
                updateProposedValues = updateFoundArtists,
                getLabel = { strings.artistName },
                style = SoulTextFieldStyle.Bottom,
                getError = { strings.fieldCannotBeEmpty },
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