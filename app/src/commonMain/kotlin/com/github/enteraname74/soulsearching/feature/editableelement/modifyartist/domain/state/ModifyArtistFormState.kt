package com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.domain.state

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.domain.model.Artist
import com.github.enteraname74.soulsearching.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.coreui.textfield.SoulDropdownTextFieldHolderImpl
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec

sealed interface ModifyArtistFormState {
    data object NoData: ModifyArtistFormState
    data class Data(
        private val initialArtist: Artist,
        private val updateFoundArtists: suspend (name: String) -> List<String>,
        private val workDispatcher: WorkDispatcher,
    ) : ModifyArtistFormState {
        val textFields: List<SoulTextFieldHolder> = listOf(
            SoulDropdownTextFieldHolderImpl(
                id = ARTIST_NAME,
                isValid = { it.isNotBlank() },
                initialValue = initialArtist.artistName,
                updateProposedValues = updateFoundArtists,
                workDispatcher = workDispatcher,
                getLabel = { CoreRes.string.artist_name.toTextSpec() },
                getError = { CoreRes.string.field_cannot_be_empty.toTextSpec() },
            ),
        )

        fun isFormValid(): Boolean = textFields.all { it.isValid() }

        fun getArtistName(): String = textFields.first().value.trim()

        companion object {
            private const val ARTIST_NAME = "ARTIST_NAME"
        }
    }
}
