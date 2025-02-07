package com.github.enteraname74.soulsearching.feature.settings.cloud

interface SettingsCloudListener {
    fun signIn()
    fun logOut()
    fun logIn()
    fun toggleCloudState()
    fun toggleSearchMetadata()
    fun uploadAllMusicToCloud()
    fun generateCode()
}