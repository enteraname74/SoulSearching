package com.github.enteraname74.soulsearching.features.musicmanager.multipleartists

import com.github.enteraname74.domain.model.*
import com.github.enteraname74.soulsearching.features.musicmanager.domain.AlbumInformation
import com.github.enteraname74.soulsearching.features.musicmanager.domain.OptimizedCachedData
import java.util.*
import kotlin.collections.HashMap

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
            dataMode = DataMode.Local,
        )
        optimizedCachedData.artistsByName[artistName] = newArtist
        return newArtist
    }

    override suspend fun deleteArtists(
        artists: List<Artist>,
    ) {
        val ids = artists.map { it.artistId }
        val names = artists.map { it.artistName }

        artists.forEach { artist ->
            optimizedCachedData.artistsByName.remove(artist.artistName)
        }
        optimizedCachedData.musicArtists.removeIf { it.artistId in ids }
        optimizedCachedData.albumsByInfo = HashMap(
            optimizedCachedData.albumsByInfo.filterNot {
                it.key.artist in names
            }
        )
    }

    override suspend fun getAllArtistFromName(artistsNames: List<String>): List<Artist> =
        optimizedCachedData.artistsByName.filter { it.key in artistsNames }.values.toList()

    override suspend fun getMusicIdsOfArtist(artist: Artist): List<UUID> =
        optimizedCachedData.musicsByPath
            .filter{ it.value.artist == artist.artistName }
            .map { it.value.musicId }

    override suspend fun getAlbumIdsOfArtist(artist: Artist): List<UUID> =
        optimizedCachedData.albumsByInfo
            .filter { it.key.artist == artist.artistName }
            .map { it.value.albumId }

    override suspend fun linkSongsToArtist(musicIds: List<UUID>, artistId: UUID) {
        optimizedCachedData.musicArtists.addAll(
            musicIds.map {
                MusicArtist(
                    musicId = it,
                    artistId = artistId,
                    dataMode = DataMode.Local,
                )
            }
        )
    }

    override suspend fun linkAlbumToArtist(album: Album, artist: Artist) {
        optimizedCachedData.albumsByInfo[
                AlbumInformation(
                    name = album.albumName,
                    artist = artist.artistName
                )
        ] = album.copy(
            artistId = artist.artistId,
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
        val musicsOfAlbumWithMultipleArtists: List<Music> =
            optimizedCachedData.musicsByPath.filter {
                it.value.album == fromAlbum.albumName && it.value.artist == multipleArtistName
            }.values.toList()

        musicsOfAlbumWithMultipleArtists.forEach { musicToMove ->
            optimizedCachedData.musicsByPath[musicToMove.path] = musicToMove.copy(
                albumId = toAlbum.albumId,
            )
        }

        // We delete the multiple artists album
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