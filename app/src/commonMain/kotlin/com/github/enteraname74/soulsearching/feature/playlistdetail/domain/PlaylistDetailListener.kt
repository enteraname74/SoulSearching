package com.github.enteraname74.soulsearching.feature.playlistdetail.domain

import com.github.enteraname74.domain.model.Music
import kotlin.uuid.Uuid

interface PlaylistDetailListener {
    val onEdit: (() -> Unit)?

    fun onSubtitleClicked() {}
    fun onCloseSelection()
    fun onLongClickOnMusic(musicId: Uuid)
    fun onShuffleClicked()
    fun onPlayClicked(music: Music? = null)
    fun onSearch(search: String)
    fun showMusicBottomSheet(musicIds: List<Uuid>)
    fun continuePlayedList(playedListId: Uuid)
    fun deletePlayedList(playedListId: Uuid)
}
