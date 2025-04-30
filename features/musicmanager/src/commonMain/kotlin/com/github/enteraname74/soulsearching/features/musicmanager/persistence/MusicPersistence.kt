package com.github.enteraname74.soulsearching.features.musicmanager.persistence

import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.usecase.album.CommonAlbumUseCase
import com.github.enteraname74.domain.usecase.albumartist.UpsertAllAlbumArtistUseCase
import com.github.enteraname74.domain.usecase.artist.UpsertAllArtistsUseCase
import com.github.enteraname74.domain.usecase.folder.UpsertAllFoldersUseCase
import com.github.enteraname74.domain.usecase.music.UpsertAllMusicsUseCase
import com.github.enteraname74.domain.usecase.musicalbum.UpsertAllMusicAlbumUseCase
import com.github.enteraname74.domain.usecase.musicartist.UpsertAllMusicArtistsUseCase
import com.github.enteraname74.soulsearching.features.musicmanager.domain.OptimizedCachedData
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Saves the collected data of music, album and artists.
 */
class MusicPersistence(
    private val optimizedCachedData: OptimizedCachedData,
): KoinComponent {
    private val upsertAllArtistsUseCase: UpsertAllArtistsUseCase by inject()
    private val commonAlbumUseCase: CommonAlbumUseCase by inject()
    private val upsertAllMusicsUseCase: UpsertAllMusicsUseCase by inject()
    private val upsertAllFoldersUseCase: UpsertAllFoldersUseCase by inject()
    private val upsertAllMusicArtistsUseCase: UpsertAllMusicArtistsUseCase by inject()
    private val upsertAllAlbumArtistUseCase: UpsertAllAlbumArtistUseCase by inject()
    private val upsertAllMusicAlbumUseCase: UpsertAllMusicAlbumUseCase by inject()
    private val settings: SoulSearchingSettings by inject()

    suspend fun saveAll() {
        upsertAllArtistsUseCase(optimizedCachedData.artistsByName.values.toList())
        commonAlbumUseCase.upsertAll(optimizedCachedData.albumsByInfo.values.toList())
        upsertAllMusicsUseCase(optimizedCachedData.musicsByPath.values.toList())
        upsertAllAlbumArtistUseCase(optimizedCachedData.albumArtists)
        upsertAllMusicAlbumUseCase(optimizedCachedData.musicAlbums)
        upsertAllMusicArtistsUseCase(optimizedCachedData.musicArtists)
        upsertAllFoldersUseCase(
            optimizedCachedData.musicsByPath.values.map { Folder(folderPath = it.folder) }.distinctBy { it.folderPath }
        )

        optimizedCachedData.clear()

        settings.set(
            SoulSearchingSettingsKeys.HAS_MUSICS_BEEN_FETCHED_KEY.key,
            true
        )
    }
}