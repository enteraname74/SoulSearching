package com.github.enteraname74.soulsearching.feature.editableelement.domain

import com.github.enteraname74.domain.model.Cover

data class EditableElement(
    val initialCover: Cover?,
    val newCover: ByteArray?,
)
