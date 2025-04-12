package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever

interface CoverFolderRetrieverActions {
    fun onToggleActivation()
    fun updateFolderPath(newPath: String)
    fun updateCoverFileName(newName: String)
    fun toggleWhiteSpace()
    fun updateWhiteSpaceReplacement(replacement: String)
    fun updateLowerCase(isLowerCase: Boolean?)
    fun toggleMode()
}