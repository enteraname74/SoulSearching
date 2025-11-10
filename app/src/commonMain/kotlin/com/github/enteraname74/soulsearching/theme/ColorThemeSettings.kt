package com.github.enteraname74.soulsearching.theme

sealed interface ColorThemeSettings {
    data object FromSystem : ColorThemeSettings
    data object DynamicTheme : ColorThemeSettings
    data class Personalized(
        val hasDynamicPlayer: Boolean,
        val hasDynamicPlaylists: Boolean,
        val hasDynamicOtherViews: Boolean,
    ) : ColorThemeSettings

    fun canShowDynamicOtherViewsTheme(): Boolean =
        this is DynamicTheme ||
                (this as? Personalized)?.hasDynamicOtherViews == true

    fun canShowDynamicPlayerTheme(): Boolean =
        this is DynamicTheme ||
                (this as? Personalized)?.hasDynamicPlayer == true

    fun canShowDynamicPlaylistsTheme(): Boolean =
        (this as? Personalized)?.hasDynamicPlaylists == true
}