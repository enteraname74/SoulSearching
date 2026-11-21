package com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.domain.state

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.github.enteraname74.soulsearching.domain.model.AlbumWithMusics
import com.github.enteraname74.soulsearching.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.coreui.textfield.SoulDropdownTextFieldHolderImpl
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldStyle
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec

sealed interface ModifyAlbumFormState {
    data object NoData : ModifyAlbumFormState
    data class Data(
        private val initialAlbum: AlbumWithMusics,
        private val updateFoundAlbums: suspend (name: String) -> List<String>,
        private val updateFoundArtists: suspend (name: String) -> List<String>,
        private val workDispatcher: WorkDispatcher,
    ) : ModifyAlbumFormState {
        val textFields: List<SoulTextFieldHolder> = listOf(
            SoulDropdownTextFieldHolderImpl(
                id = ALBUM_NAME,
                isValid = { it.isNotBlank() },
                initialValue = initialAlbum.album.albumName,
                updateProposedValues = updateFoundAlbums,
                workDispatcher = workDispatcher,
                getLabel = { CoreRes.string.album_name.toTextSpec() },
                style = SoulTextFieldStyle.Top,
                getError = { CoreRes.string.field_cannot_be_empty.toTextSpec() },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
            ),
            SoulDropdownTextFieldHolderImpl(
                id = ARTIST_NAME,
                isValid = { it.isNotBlank() },
                initialValue = initialAlbum.album.artist.artistName,
                updateProposedValues = updateFoundArtists,
                workDispatcher = workDispatcher,
                getLabel = { CoreRes.string.artist_name.toTextSpec() },
                style = SoulTextFieldStyle.Bottom,
                getError = { CoreRes.string.field_cannot_be_empty.toTextSpec() },
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
