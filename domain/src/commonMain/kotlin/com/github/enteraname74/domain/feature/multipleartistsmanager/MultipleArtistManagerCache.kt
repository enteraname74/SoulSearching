package com.github.enteraname74.domain.feature.multipleartistsmanager

import com.github.enteraname74.domain.model.*
import java.util.*

class MultipleArtistManagerCache(
    private var artistsByName: HashMap<String, Artist>,
    private var albumsByInfo: HashMap<AlbumInformation, Album>,
    private val albumArtists: ArrayList<AlbumArtist>,
    private val musicArtists: ArrayList<MusicArtist>,
    private val musicAlbums: ArrayList<MusicAlbum>,
) : MultipleArtistManager() {
    override suspend fun getAlbumsOfMultipleArtist(artistName: String): List<Album> =
        albumsByInfo.filter { (key, _) -> key.artist == artistName }.values.toList()

    override suspend fun getArtistFromName(artistName: String): Artist? =
        artistsByName[artistName]

    override suspend fun createNewArtist(artistName: String): Artist {
        val newArtist = Artist(
            artistId = UUID.randomUUID(),
            artistName = artistName,
        )
        artistsByName[artistName] = newArtist
        return newArtist
    }

    override suspend fun deleteArtist(
        artist: Artist,
        musicIdsOfInitialArtist: List<UUID>,
        albumIdsOfInitialArtist: List<UUID>,
    ) {
        artistsByName.remove(artist.artistName)
        musicIdsOfInitialArtist.forEach { musicId ->
            musicArtists.removeIf { it.musicId == musicId && it.artistId == artist.artistId }
        }
        albumIdsOfInitialArtist.forEach { albumId ->
            albumArtists.removeIf { it.albumId == albumId && it.artistId == artist.artistId }
        }
    }

    override suspend fun getMusicIdsOfArtist(artistId: UUID): List<UUID> =
        musicArtists
            .filter { it.artistId == artistId }
            .map { it.musicId }

    override suspend fun getAlbumIdsOfArtist(artistId: UUID): List<UUID> =
        albumArtists
            .filter { it.artistId == artistId }
            .map { it.albumId }

    override suspend fun linkMusicToArtist(musicId: UUID, artistId: UUID) {
        musicArtists.add(
            MusicArtist(
                musicId = musicId,
                artistId = artistId,
            )
        )
    }

    override suspend fun linkAlbumToArtist(albumId: UUID, artistId: UUID) {
        albumArtists.add(
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
        return albumsByInfo[albumKey]
    }

    override suspend fun moveSongsOfAlbum(
        fromAlbum: Album,
        toAlbum: Album,
        multipleArtistName: String,
    ) {
        // We redirect the songs of the multiple artist album
        val musicsIdsOfAlbumWithMultipleArtists = musicAlbums.filter { it.albumId == fromAlbum.albumId }.map { it.musicId }
        musicsIdsOfAlbumWithMultipleArtists.forEach { musicId ->
            musicAlbums.add(
                MusicAlbum(
                    musicId = musicId,
                    albumId = toAlbum.albumId,
                )
            )
        }
        // We delete the multiple artists album
        musicAlbums.removeIf { it.albumId == fromAlbum.albumId }
        albumArtists.removeIf { it.albumId == fromAlbum.albumId }
        albumsByInfo.remove(
            AlbumInformation(
                name = fromAlbum.albumName,
                artist = multipleArtistName,
            )
        )
    }
}