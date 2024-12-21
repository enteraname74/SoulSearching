package com.github.enteraname74.soulsearching.features.musicmanager.multipleartists

import com.github.enteraname74.domain.model.*
import com.github.enteraname74.soulsearching.features.musicmanager.domain.AlbumInformation
import com.github.enteraname74.soulsearching.features.musicmanager.domain.OptimizedCachedData
import java.util.*

open class FetchAllMultipleArtistManagerImpl(
    private val optimizedCachedData: OptimizedCachedData
): MultipleArtistManager() {
    override suspend fun getAlbumsOfMultipleArtist(artist: Artist): List<Album> =
        optimizedCachedData.albumsByInfo.filter { (key, _) -> key.artist == artist.artistName }.values.toList()

    override suspend fun getArtistFromName(artistName: String): Artist? =
        optimizedCachedData.artistsByName[artistName]

    override suspend fun createNewArtist(artistName: String): Artist {
        val newArtist = Artist(
            artistId = UUID.randomUUID(),
            artistName = artistName,
        )
        optimizedCachedData.artistsByName[artistName] = newArtist
        return newArtist
    }

    override suspend fun deleteArtist(
        artist: Artist,
        musicIdsOfInitialArtist: List<UUID>,
        albumIdsOfInitialArtist: List<UUID>,
    ) {
        optimizedCachedData.artistsByName.remove(artist.artistName)
        musicIdsOfInitialArtist.forEach { musicId ->
            optimizedCachedData.musicArtists.removeIf { it.musicId == musicId && it.artistId == artist.artistId }
        }
        albumIdsOfInitialArtist.forEach { albumId ->
            optimizedCachedData.albumArtists.removeIf { it.albumId == albumId && it.artistId == artist.artistId }
        }
    }

    override suspend fun getMusicIdsOfArtist(artist: Artist): List<UUID> =
        optimizedCachedData.musicsByPath
            .filter{ it.value.artist == artist.artistName }
            .map { it.value.musicId }

    override suspend fun getAlbumIdsOfArtist(artist: Artist): List<UUID> =
        optimizedCachedData.albumsByInfo
            .filter { it.key.artist == artist.artistName }
            .map { it.value.albumId }

    override suspend fun linkMusicToArtist(musicId: UUID, artistId: UUID) {
        optimizedCachedData.musicArtists.add(
            MusicArtist(
                musicId = musicId,
                artistId = artistId,
            )
        )
    }

    override suspend fun linkAlbumToArtist(albumId: UUID, artistId: UUID) {
        optimizedCachedData.albumArtists.add(
            AlbumArtist(
                albumId = albumId,
                artistId = artistId,
            )
        )
    }

    override suspend fun getExistingAlbumOfFirstArtist(albumName: String, firstArtistName: String): Album? {
        val albumKey = AlbumInformation(
            name = albumName,
            artist = firstArtistName,
        )
        return optimizedCachedData.albumsByInfo[albumKey]
    }

    override suspend fun moveSongsOfAlbum(
        fromAlbum: Album,
        toAlbum: Album,
        multipleArtistName: String,
    ) {
        // We redirect the songs of the multiple artist album
        val musicsIdsOfAlbumWithMultipleArtists =
            optimizedCachedData.musicsByPath.filter {
                it.value.album == fromAlbum.albumName && it.value.artist == multipleArtistName
            }.map { it.value.musicId }

        musicsIdsOfAlbumWithMultipleArtists.forEach { musicId ->
            optimizedCachedData.musicAlbums.add(
                MusicAlbum(
                    musicId = musicId,
                    albumId = toAlbum.albumId,
                )
            )
        }
        // We delete the multiple artists album
        optimizedCachedData.musicAlbums.removeIf { it.albumId == fromAlbum.albumId }
        optimizedCachedData.albumArtists.removeIf { it.albumId == fromAlbum.albumId }
        optimizedCachedData.albumsByInfo.remove(
            AlbumInformation(
                name = fromAlbum.albumName,
                artist = multipleArtistName,
            )
        )
    }

    fun getPotentialMultipleArtists(): List<Artist> =
        optimizedCachedData.artistsByName.values.filter { it.isComposedOfMultipleArtists() }

    fun doDataHaveMultipleArtists(): Boolean =
        getPotentialMultipleArtists().isNotEmpty()
}