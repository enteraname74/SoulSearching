package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever

import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder

interface CoverFolderRetrieverActions {
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