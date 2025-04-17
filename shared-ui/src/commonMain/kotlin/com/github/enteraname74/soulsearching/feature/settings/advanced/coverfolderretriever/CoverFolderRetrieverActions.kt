package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever

import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder

interface CoverFolderRetrieverActions {
    val coverFileNameTextField: SoulTextFieldHolder
    val whiteSpaceReplacementTextField: SoulTextFieldHolder

    fun onToggleActivation()
    fun updateFolderPath(newPath: String)
    fun updateCoverFileName(newName: String)
    fun toggleWhiteSpace()
    fun updateWhiteSpaceReplacement(replacement: String)
    fun updateLowerCase(isLowerCase: Boolean?)
    fun toggleMode()
}