package com.github.enteraname74.soulsearching.feature.playlistdetail.domain

import com.github.enteraname74.domain.model.Music
import java.util.*

interface PlaylistDetailListener {
    val onEdit: (() -> Unit)?

    fun onUpdateNbPlayed(musicId: UUID)
    fun onUpdateNbPlayed()
    fun onSubtitleClicked() {}
    fun onCloseSelection()
    fun onMoreClickedOnSelection()
    fun onShuffleClicked()
    fun onPlayClicked(music: Music? = null)
}