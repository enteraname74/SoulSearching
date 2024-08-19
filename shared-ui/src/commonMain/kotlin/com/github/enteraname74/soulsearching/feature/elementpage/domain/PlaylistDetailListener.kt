package com.github.enteraname74.soulsearching.feature.elementpage.domain

import java.util.UUID

interface PlaylistDetailListener {
    fun onEdit()
    fun onUpdateNbPlayed(musicId: UUID)
    fun onUpdateNbPlayed()
    fun onSubtitleClicked() {}
}