package com.github.enteraname74.soulsearching.feature.playlistdetail.domain

import com.github.enteraname74.domain.model.Music
import java.util.UUID

interface PlaylistDetailListener {
    val onEdit: (() -> Unit)?

    fun onSubtitleClicked() {}
    fun onCloseSelection()
    fun onLongClickOnMusic(musicId: UUID)
    fun onShuffleClicked()
    fun onPlayClicked(music: Music? = null)
    fun onSearch(search: String)
    fun showMusicBottomSheet(musicIds: List<UUID>)
    fun continuePlayedList(playedListId: UUID)
    fun deletePlayedList(playedListId: UUID)
}