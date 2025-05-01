package com.github.enteraname74.soulsearching.feature.playlistdetail.domain

import java.util.*

interface PlaylistDetailListener {
    val onEdit: (() -> Unit)?

    fun onUpdateNbPlayed(musicId: UUID)
    fun onUpdateNbPlayed()
    fun onSubtitleClicked() {}
    fun onCloseSelection()
    fun onMoreClickedOnSelection()
}