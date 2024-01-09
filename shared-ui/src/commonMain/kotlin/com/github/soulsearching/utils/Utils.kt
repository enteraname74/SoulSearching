package com.github.soulsearching.utils

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumArtist
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import java.util.UUID

/**
 * All kind of different methods.
 */
object Utils {
    /**
     * Convert a duration to a viewable duration.
     */
    fun convertDuration(duration: Int): String {
        val minutes: Float = duration.toFloat() / 1000 / 60
        val seconds: Float = duration.toFloat() / 1000 % 60

        val strMinutes: String = minutes.toString().split(".")[0]

        val strSeconds = if (seconds < 10.0) {
            "0" + seconds.toString().split(".")[0]
        } else {
            seconds.toString().split(".")[0]
        }

        return "$strMinutes:$strSeconds"
    }

    /**
     * Remove a music from the app.
     */
    suspend fun removeMusicFromApp(
        musicRepository: MusicRepository,
        albumRepository: AlbumRepository,
        artistRepository: ArtistRepository,
        albumArtistRepository: AlbumArtistRepository,
        musicAlbumRepository: MusicAlbumRepository,
        musicArtistRepository: MusicArtistRepository,
        musicToRemove: Music
    ) {
        val artist = artistRepository.getArtistFromInfo(
            artistName = musicToRemove.artist
        )
        val album = getCorrespondingAlbum(
            musicId = musicToRemove.musicId,
            albumRepository = albumRepository,
            musicAlbumRepository = musicAlbumRepository
        )

        musicRepository.deleteMusic(music = musicToRemove)

        checkAndDeleteAlbum(
            albumToCheck = album!!,
            albumRepository = albumRepository,
            musicAlbumRepository = musicAlbumRepository,
            albumArtistRepository = albumArtistRepository
        )
        checkAndDeleteArtist(
            artistToCheck = artist!!,
            musicArtistRepository = musicArtistRepository,
            artistRepository = artistRepository
        )
    }

    /**
     * Remove an album from the app.
     */
    private suspend fun removeAlbumFromApp(
        albumToRemove: Album,
        albumRepository: AlbumRepository,
        albumArtistRepository: AlbumArtistRepository
    ) {
        albumRepository.deleteAlbum(album = albumToRemove)
        albumArtistRepository.deleteAlbumFromArtist(albumId = albumToRemove.albumId)
    }

    /**
     * Remove an artist from the app.
     */
    private suspend fun removeArtistFromApp(
        artistToRemove: Artist,
        artistRepository: ArtistRepository,
    ) {
        artistRepository.deleteArtist(artist = artistToRemove)
    }

    /**
     * Check if an album can be deleted automatically (no songs in the album).
     * Delete the album if possible.
     */
    private suspend fun checkAndDeleteAlbum(
        albumToCheck: Album,
        albumRepository: AlbumRepository,
        musicAlbumRepository: MusicAlbumRepository,
        albumArtistRepository: AlbumArtistRepository
    ) {
        if (musicAlbumRepository.getNumberOfMusicsFromAlbum(
                albumId = albumToCheck.albumId
            ) == 0
        ) {
            removeAlbumFromApp(
                albumToRemove = albumToCheck,
                albumRepository = albumRepository,
                albumArtistRepository = albumArtistRepository
            )
        }
    }

    /**
     * Check if an artist can be deleted automatically (no songs in the artist).
     * Delete the artist if possible.
     */
    suspend fun checkAndDeleteArtist(
        artistToCheck: Artist,
        musicArtistRepository: MusicArtistRepository,
        artistRepository: ArtistRepository
    ) {
        if (musicArtistRepository.getNumberOfMusicsFromArtist(
                artistId = artistToCheck.artistId
            ) == 0
        ) {
            removeArtistFromApp(
                artistToRemove = artistToCheck,
                artistRepository = artistRepository
            )
        }
    }

    /**
     * Change the album of a music.
     */
    suspend fun modifyMusicAlbum(
        artist: Artist,
        musicAlbumRepository: MusicAlbumRepository,
        albumRepository: AlbumRepository,
        albumArtistRepository: AlbumArtistRepository,
        legacyMusic: Music,
        currentAlbum: String,
    ) {
        // On récupère l'ancien album :
        val legacyAlbum = getCorrespondingAlbum(
            musicId = legacyMusic.musicId,
            albumRepository = albumRepository,
            musicAlbumRepository = musicAlbumRepository
        )

        var newAlbum = albumRepository.getCorrespondingAlbum(
            albumName = currentAlbum,
            artistId = artist.artistId
        )

        if (newAlbum == null) {
            // C'est un nouvel album, il faut le créer :
            val album = Album(
                albumName = currentAlbum
            )
            newAlbum = album

            albumRepository.insertAlbum(
                album = album
            )
            // On lie l'album crée à son artiste :
            albumArtistRepository.insertAlbumIntoArtist(
                AlbumArtist(
                    albumId = newAlbum.albumId,
                    artistId = artist.artistId
                )
            )
        }
        // On met les infos de la musique à jour :
        musicAlbumRepository.updateAlbumOfMusic(
            musicId = legacyMusic.musicId,
            newAlbumId = newAlbum.albumId
        )

        checkAndDeleteAlbum(
            albumToCheck = legacyAlbum!!,
            musicAlbumRepository = musicAlbumRepository,
            albumRepository = albumRepository,
            albumArtistRepository = albumArtistRepository
        )
    }

    /**
     * Tries to retrieve the corresponding album of a music.
     */
    private suspend fun getCorrespondingAlbum(
        musicId: UUID,
        albumRepository: AlbumRepository,
        musicAlbumRepository: MusicAlbumRepository
    ): Album? {
        val albumId: UUID? = musicAlbumRepository.getAlbumIdFromMusicId(
            musicId = musicId
        )
        return if (albumId == null) {
            null
        } else {
            albumRepository.getAlbumFromId(
                albumId = albumId
            )
        }
    }

    /**
     * Tries to retrieve the corresponding artist of a music.
     */
    suspend fun getCorrespondingArtist(
        musicId: UUID,
        artistRepository: ArtistRepository,
        musicArtistRepository: MusicArtistRepository
    ): Artist? {
        val artistId: UUID? = musicArtistRepository.getArtistIdFromMusicId(
            musicId = musicId
        )
        return if (artistId == null) {
            null
        } else {
            artistRepository.getArtistFromId(
                artistId = artistId
            )
        }
    }
}