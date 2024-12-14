package com.github.enteraname74.soulsearching.feature.settings.personalisation.player.domain

data class SettingsPlayerPersonalisationState(
    val isPlayerSwipeEnabled: Boolean,
    val isRewindEnabled: Boolean,
    val isMinimisedSongProgressionShown: Boolean,
    val playerVolume: Float,
)
