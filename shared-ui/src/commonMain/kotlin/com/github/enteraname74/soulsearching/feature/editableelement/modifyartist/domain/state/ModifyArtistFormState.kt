package com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.domain.state

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.SoulDropdownTextFieldHolderImpl
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder

sealed interface ModifyArtistFormState {
    data object NoData: ModifyArtistFormState
    data class Data(
        private val initialArtist: Artist,
        private val updateFoundArtists: suspend (name: String) -> List<String>,
    ) : ModifyArtistFormState {
        val textFields: List<SoulTextFieldHolder> = listOf(
            SoulDropdownTextFieldHolderImpl(
                id = ARTIST_NAME,
                isValid = { it.isNotBlank() },
                initialValue = initialArtist.artistName,
                updateProposedValues = updateFoundArtists,
                getLabel = { strings.artistName },
            ),
        )

        fun isFormValid(): Boolean = textFields.all { it.isValid() }

        fun getArtistName(): String = textFields.first().value

        companion object {
            private const val ARTIST_NAME = "ARTIST_NAME"
        }
    }
}