package com.github.enteraname74.soulsearching.features.musicmanager.multipleartists

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.features.musicmanager.domain.OptimizedCachedData
import java.util.*

open class FetchAllMultipleArtistManagerImpl(
    private val optimizedCachedData: OptimizedCachedData
): MultipleArtistManager() {
    override suspend fun getAlbumsOfMultipleArtist(artist: Artist): List<Album> =
        optimizedCachedData.musicsByPath.values.map { it.album }.filter {
            it.artist.artistId == artist.artistId
        }.distinctBy { it.albumId }

    override suspend fun getArtistFromName(artistName: String): Artist? =
        optimizedCachedData.musicsByPath.values
            .flatMap { it.artists }
            .distinct()
            .find { it.artistName == artistName }

    override suspend fun deleteArtists(
        artists: List<Artist>,
    ) {
        // no-op
    }

    override suspend fun getAllArtistFromName(artistsNames: List<String>): List<Artist> =
        optimizedCachedData.musicsByPath.values
            .flatMap { it.artists }
            .filter { it.artistName in artistsNames }

    override suspend fun getMusicIdsOfArtist(artist: Artist): List<UUID> =
        optimizedCachedData.musicsByPath.values
            .filter{ music -> music.artists.any { it.artistId == artist.artistId } }
            .map { it.musicId }

    override suspend fun getAlbumIdsOfArtist(artist: Artist): List<UUID> =
        optimizedCachedData.musicsByPath
            .values
            .filter { it.album.artist.artistId == artist.artistId }
            .map { it.album.albumId }

    override suspend fun linkMusicToArtists(musicId: UUID, artists: List<Artist>) {
        val musicEntry: Map<String, Music> = optimizedCachedData.musicsByPath
            .filter { it.value.musicId == musicId }

        val key = musicEntry.keys.firstOrNull() ?: return
        val musicToUpdate = musicEntry.values.firstOrNull() ?: return

        optimizedCachedData.musicsByPath[key] = musicToUpdate.copy(
            artists = artists,
        )
    }

    override suspend fun linkAlbumToArtist(
        album: Album,
        artist: Artist,
        multipleArtistName: String,
    ) {
        val updatedAlbum = album.copy(
            artist = artist,
        )
        val entries: Map<String, Music> = optimizedCachedData.musicsByPath
            .filter { it.value.album == album }

        entries.forEach { (key, value) ->
            optimizedCachedData.musicsByPath[key] = value.copy(
                album = updatedAlbum
            )
        }
    }

    override suspend fun getExistingAlbumOfFirstArtist(albumName: String, firstArtistName: String): Album? =
        optimizedCachedData.musicsByPath.values.find {
            it.album.albumName == albumName && it.album.artist.artistName == firstArtistName
        }?.album

    override suspend fun moveSongsOfAlbum(
        fromAlbum: Album,
        toAlbum: Album,
        multipleArtistName: String,
    ) {
        // We redirect the songs of the multiple artist album
        val musicsOfAlbumWithMultipleArtists =
            optimizedCachedData.musicsByPath.filter {
                it.value.album == fromAlbum
            }

        musicsOfAlbumWithMultipleArtists.forEach { (path, music) ->
            optimizedCachedData.musicsByPath[path] = music.copy(album = toAlbum)
        }
    }

    fun getPotentialMultipleArtists(): List<Artist> =
        optimizedCachedData.musicsByPath.values
            .flatMap { it.artists }
            .filter { it.isComposedOfMultipleArtists() }
            .distinctBy { it.artistId }

    fun doDataHaveMultipleArtists(): Boolean =
        getPotentialMultipleArtists().isNotEmpty()
}