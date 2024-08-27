package com.github.enteraname74.soulsearching.feature.editableelement.domain

import androidx.compose.ui.graphics.ImageBitmap
import java.util.UUID

data class EditableElement(
    val initialCoverId: UUID?,
    val newCover: ImageBitmap?,
)
