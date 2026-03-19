package com.github.enteraname74.soulsearching.feature.musiclink

expect class MusicLinkHandler {
    suspend fun handleLink(link: String)
}