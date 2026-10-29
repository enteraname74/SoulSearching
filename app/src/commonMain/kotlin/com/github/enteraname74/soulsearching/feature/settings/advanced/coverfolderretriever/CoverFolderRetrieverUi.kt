package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever

import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder

data class CoverFolderRetrieverUi(
    val title: SoulTextSpec,
    val activateText: SoulTextSpec,
    val dynamicNameTitle: SoulTextSpec,
    val whiteSpaceReplacementTextField: SoulTextFieldHolder,
    val coverFileNameTextField: SoulTextFieldHolder,
    val extensionTextField: SoulTextFieldHolder
)
