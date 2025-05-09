package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever

import cafe.adriel.voyager.core.model.ScreenModel
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder

interface CoverFolderRetrieverActions: ScreenModel {
    val whiteSpaceReplacementTextField: SoulTextFieldHolder
    val coverFileNameTextField: SoulTextFieldHolder
    val extensionTextField: SoulTextFieldHolder

    fun onToggleActivation()
    fun updateFolderModePath(newPath: String)
    fun updateFileModePath(newPath: String)
    fun updateCoverFileName(newName: String)
    fun updateFileExtension(newExtension: String)
    fun toggleWhiteSpace()
    fun updateWhiteSpaceReplacement(replacement: String)
    fun updateLowerCase(isLowerCase: Boolean?)
    fun toggleMode()
}