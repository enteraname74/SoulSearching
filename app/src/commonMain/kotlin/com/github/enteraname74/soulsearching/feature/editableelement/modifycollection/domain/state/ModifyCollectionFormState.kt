package com.github.enteraname74.soulsearching.feature.editableelement.modifycollection.domain.state

import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolderImpl
import com.github.enteraname74.soulsearching.domain.model.Collection

sealed interface ModifyCollectionFormState {
    data object NoData : ModifyCollectionFormState

    data class Data(
        private val initialCollection: Collection,
    ) : ModifyCollectionFormState {
        val textFields: List<SoulTextFieldHolder> = listOf(
            SoulTextFieldHolderImpl(
                id = COLLECTION_NAME,
                initialValue = initialCollection.name,
                isValid = { it.isNotBlank() },
                getLabel = { strings.collectionName },
                getError = { strings.fieldCannotBeEmpty },
            )
        )

        fun getCollectionName(): String = textFields.first().value

        fun isFormValid(): Boolean = textFields.all { it.isValid() }

        companion object {
            private const val COLLECTION_NAME = "COLLECTION_NAME"
        }
    }
}
