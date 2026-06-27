package com.github.enteraname74.soulsearching.feature.settings.cloud

interface SettingsCloudNavScope {
    fun toConnection()
    fun toSettings()
    fun toUser()
    fun toSync()
    fun toSharedList()
    fun toFetchMusic()
    fun toUsers()
    fun navigateBack()
}