package com.github.enteraname74.soulsearching.features.musicmanager.multipleartists

import com.github.enteraname74.domain.model.*
import com.github.enteraname74.soulsearching.features.musicmanager.domain.AlbumInformation
import com.github.enteraname74.soulsearching.features.musicmanager.domain.OptimizedFetchData
import java.util.*

class FetchAllMultipleArtistManagerImpl(
    private val optimizedFetchData: OptimizedFetchData
): MultipleArtistManager() {
    override suspend fun getAlbumsOfMultipleArtist(artistName: String): List<Album> =
        optimizedFetchData.albumsByInfo.filter { (key, _) -> key.artist == artistName }.values.toList()

    override suspend fun getArtistFromName(artistName: String): Artist? =
        optimizedFetchData.artistsByName[artistName]

    override suspend fun createNewArtist(artistName: String): Artist {
        val newArtist = Artist(
            artistId = UUID.randomUUID(),
            artistName = artistName,
        )
        optimizedFetchData.artistsByName[artistName] = newArtist
        return newArtist
    }

    override suspend fun deleteArtist(
        artist: Artist,
        musicIdsOfInitialArtist: List<UUID>,
        albumIdsOfInitialArtist: List<UUID>,
    ) {
        optimizedFetchData.artistsByName.remove(artist.artistName)
        musicIdsOfInitialArtist.forEach { musicId ->
            optimizedFetchData.musicArtists.removeIf { it.musicId == musicId && it.artistId == artist.artistId }
        }
        albumIdsOfInitialArtist.forEach { albumId ->
            optimizedFetchData.albumArtists.removeIf { it.albumId == albumId && it.artistId == artist.artistId }
        }
    }

    override suspend fun getMusicIdsOfArtist(artistId: UUID): List<UUID> =
        optimizedFetchData.musicArtists
            .filter { it.artistId == artistId }
            .map { it.musicId }

    override suspend fun getAlbumIdsOfArtist(artistId: UUID): List<UUID> =
        optimizedFetchData.albumArtists
            .filter { it.artistId == artistId }
            .map { it.albumId }

    override suspend fun linkMusicToArtist(musicId: UUID, artistId: UUID) {
        optimizedFetchData.musicArtists.add(
            MusicArtist(
                musicId = musicId,
                artistId = artistId,
            )
        )
    }

    override suspend fun linkAlbumToArtist(albumId: UUID, artistId: UUID) {
        optimizedFetchData.albumArtists.add(
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
        return optimizedFetchData.albumsByInfo[albumKey]
    }

    override suspend fun moveSongsOfAlbum(
        fromAlbum: Album,
        toAlbum: Album,
        multipleArtistName: String,
    ) {
        // We redirect the songs of the multiple artist album
        val musicsIdsOfAlbumWithMultipleArtists =
            optimizedFetchData.musicAlbums.filter { it.albumId == fromAlbum.albumId }.map { it.musicId }
        musicsIdsOfAlbumWithMultipleArtists.forEach { musicId ->
            optimizedFetchData.musicAlbums.add(
                MusicAlbum(
                    musicId = musicId,
                    albumId = toAlbum.albumId,
                )
            )
        }
        // We delete the multiple artists album
        optimizedFetchData.musicAlbums.removeIf { it.albumId == fromAlbum.albumId }
        optimizedFetchData.albumArtists.removeIf { it.albumId == fromAlbum.albumId }
        optimizedFetchData.albumsByInfo.remove(
            AlbumInformation(
                name = fromAlbum.albumName,
                artist = multipleArtistName,
            )
        )
    }
}