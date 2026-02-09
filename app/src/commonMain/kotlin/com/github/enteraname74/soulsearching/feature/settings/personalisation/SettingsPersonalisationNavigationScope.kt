package com.github.enteraname74.soulsearching.feature.settings.personalisation

interface SettingsPersonalisationNavigationScope {
    val navigateBack: () -> Unit
    val toMainPagePersonalisation: () -> Unit
    val toMusicViewPersonalisation: () -> Unit
    val toAlbumViewPersonalisation: () -> Unit
    val toPlayerPersonalisation: () -> Unit
}