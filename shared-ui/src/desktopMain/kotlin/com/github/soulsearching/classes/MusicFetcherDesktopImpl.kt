package com.github.soulsearching.classes

import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.FolderRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.model.MusicFetcher
import com.github.soulsearching.model.SelectableMusicItem

/**
 * Class handling music fetching for desktop application.
 */
class MusicFetcherDesktopImpl(
    private val playlistRepository: PlaylistRepository,
    musicRepository: MusicRepository,
    albumRepository: AlbumRepository,
    artistRepository: ArtistRepository,
    musicAlbumRepository: MusicAlbumRepository,
    musicArtistRepository: MusicArtistRepository,
    albumArtistRepository: AlbumArtistRepository,
    imageCoverRepository: ImageCoverRepository,
    folderRepository: FolderRepository
): MusicFetcher(
    musicRepository = musicRepository,
    albumRepository = albumRepository,
    artistRepository = artistRepository,
    musicAlbumRepository = musicAlbumRepository,
    musicArtistRepository = musicArtistRepository,
    albumArtistRepository = albumArtistRepository,
    imageCoverRepository = imageCoverRepository,
    folderRepository = folderRepository
) {
    override suspend fun fetchMusics(updateProgress: (Float) -> Unit, finishAction: () -> Unit) {

    }

    override fun fetchMusicsFromSelectedFolders(
        updateProgress: (Float) -> Unit,
        alreadyPresentMusicsPaths: List<String>,
        hiddenFoldersPaths: List<String>
    ): ArrayList<SelectableMusicItem> {
        return ArrayList()
    }
}