package com.github.enteraname74.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.soulsearching.domain.viewmodel.ModifyMusicViewModel
import com.github.enteraname74.soulsearching.feature.modifyelement.modifymusic.domain.ModifyMusicViewModelHandler
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager

/**
 * Implementation of the ModifyMusicViewModel.
 */
class ModifyMusicViewModelAndroidImpl(
    musicRepository: MusicRepository,
    imageCoverRepository: ImageCoverRepository,
    playbackManager: PlaybackManager,
    albumRepository: AlbumRepository,
    artistRepository: ArtistRepository
) : ModifyMusicViewModel, ViewModel() {
    override val handler: ModifyMusicViewModelHandler = ModifyMusicViewModelHandler(
        coroutineScope = viewModelScope,
        musicRepository = musicRepository,
        imageCoverRepository = imageCoverRepository,
        playbackManager = playbackManager,
        albumRepository = albumRepository,
        artistRepository = artistRepository
    )
}