package com.github.soulsearching.classes.utils

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import com.github.soulsearching.database.dao.*
import com.github.soulsearching.database.model.Album
import com.github.soulsearching.database.model.AlbumArtist
import com.github.soulsearching.database.model.Artist
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.service.PlayerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * All kind of different methods.
 */
object Utils {
    const val BITMAP_SIZE = 300

    /**
     * Launch the playback service.
     * It also assure that the service is launched in the player view model.
     */
    fun launchService(
        context: Context,
        isFromSavedList: Boolean,
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val serviceIntent = Intent(context, PlayerService::class.java)
            serviceIntent.putExtra(PlayerService.IS_FROM_SAVED_LIST, isFromSavedList)
            context.startForegroundService(serviceIntent)
            PlayerUtils.playerViewModel.isServiceLaunched = true
        }
    }

    /**
     * Retrieve a bitmap from a given Uri.
     */
    fun getBitmapFromUri(uri: Uri, contentResolver: ContentResolver): Bitmap {
        return if (Build.VERSION.SDK_INT >= 29) {
            contentResolver.loadThumbnail(
                uri,
                Size(BITMAP_SIZE, BITMAP_SIZE),
                null
            )
        } else {
            Bitmap.createScaledBitmap(
                MediaStore.Images.Media.getBitmap(
                    contentResolver,
                    uri
                ), BITMAP_SIZE, BITMAP_SIZE, false
            )
        }
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
            println("Nouvel album !")
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
    private fun getCorrespondingAlbum(
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
    fun getCorrespondingArtist(
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