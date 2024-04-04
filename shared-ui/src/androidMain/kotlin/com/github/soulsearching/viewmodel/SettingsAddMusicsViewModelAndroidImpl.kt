package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.FolderRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.soulsearching.classes.utils.MusicFetcherAndroidImpl
import com.github.soulsearching.viewmodel.handler.SettingsAddMusicsViewModelHandler

/**
 * Implementation of the SettingsAddMusicsViewModel.
 */
class SettingsAddMusicsViewModelAndroidImpl(
    folderRepository: FolderRepository,
    musicRepository: MusicRepository,
    musicFetcher: MusicFetcherAndroidImpl,
) : SettingsAddMusicsViewModel, ViewModel() {
    override val handler: SettingsAddMusicsViewModelHandler = SettingsAddMusicsViewModelHandler(
        folderRepository = folderRepository,
        musicRepository = musicRepository,
        musicFetcher = musicFetcher,
    )
}