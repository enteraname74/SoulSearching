package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever

import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder

data class CoverFolderRetrieverUi(
    val title: String,
    val activateText: String,
    val dynamicNameTitle: String,
    val whiteSpaceReplacementTextField: SoulTextFieldHolder,
    val coverFileNameTextField: SoulTextFieldHolder
)
