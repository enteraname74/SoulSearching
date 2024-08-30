package com.github.enteraname74.soulsearching.feature.playlistdetail.domain

import java.util.UUID

interface PlaylistDetailListener {
    val onEdit: (() -> Unit)?
    fun onUpdateNbPlayed(musicId: UUID)
    fun onUpdateNbPlayed()
    fun onSubtitleClicked() {}
}