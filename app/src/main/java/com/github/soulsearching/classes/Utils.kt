package com.github.soulsearching.classes

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

class Utils {
    companion object {
        const val BITMAP_SIZE = 300

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

        suspend fun removeMusicFromApp(
            musicDao: MusicDao,
            albumDao: AlbumDao,
            artistDao: ArtistDao,
            albumArtistDao: AlbumArtistDao,
            musicAlbumDao: MusicAlbumDao,
            musicArtistDao: MusicArtistDao,
            musicToRemove: Music
        ) {
            val artist = artistDao.getArtistFromInfo(
                artistName = musicToRemove.artist
            )
            val album = getCorrespondingAlbum(
                musicId = musicToRemove.musicId,
                albumDao = albumDao,
                musicAlbumDao = musicAlbumDao
            )

            musicDao.deleteMusic(music = musicToRemove)

            checkAndDeleteAlbum(
                albumToCheck = album!!,
                albumDao = albumDao,
                musicAlbumDao = musicAlbumDao,
                albumArtistDao = albumArtistDao
            )
            checkAndDeleteArtist(
                artistToCheck = artist!!,
                musicArtistDao = musicArtistDao,
                artistDao = artistDao
            )
        }

        private suspend fun removeAlbumFromApp(
            albumToRemove: Album,
            albumDao: AlbumDao,
            albumArtistDao: AlbumArtistDao
        ) {
            albumDao.deleteAlbum(album = albumToRemove)
            albumArtistDao.deleteAlbumFromArtist(albumId = albumToRemove.albumId)
        }

        private suspend fun removeArtistFromApp(
            artistToRemove: Artist,
            artistDao: ArtistDao,
        ) {
            artistDao.deleteArtist(artist = artistToRemove)
        }

        private suspend fun checkAndDeleteAlbum(
            albumToCheck: Album,
            albumDao: AlbumDao,
            musicAlbumDao: MusicAlbumDao,
            albumArtistDao: AlbumArtistDao
        ) {
            if (musicAlbumDao.getNumberOfMusicsFromAlbum(
                    albumId = albumToCheck.albumId
                ) == 0
            ) {
                removeAlbumFromApp(
                    albumToRemove = albumToCheck,
                    albumDao = albumDao,
                    albumArtistDao = albumArtistDao
                )
            }
        }

        suspend fun checkAndDeleteArtist(
            artistToCheck: Artist,
            musicArtistDao: MusicArtistDao,
            artistDao: ArtistDao
        ) {
            if (musicArtistDao.getNumberOfMusicsFromArtist(
                    artistId = artistToCheck.artistId
                ) == 0
            ) {
                removeArtistFromApp(
                    artistToRemove = artistToCheck,
                    artistDao = artistDao
                )
            }
        }

        suspend fun modifyMusicAlbum(
            artist: Artist,
            musicAlbumDao: MusicAlbumDao,
            albumDao: AlbumDao,
            albumArtistDao: AlbumArtistDao,
            legacyMusic: Music,
            currentAlbum: String,
        ) {
            // On récupère l'ancien album :
            val legacyAlbum = getCorrespondingAlbum(
                musicId = legacyMusic.musicId,
                albumDao = albumDao,
                musicAlbumDao = musicAlbumDao
            )

            var newAlbum = albumDao.getCorrespondingAlbum(
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

                albumDao.insertAlbum(
                    album = album
                )
                // On lie l'album crée à son artiste :
                albumArtistDao.insertAlbumIntoArtist(
                    AlbumArtist(
                        albumId = newAlbum.albumId,
                        artistId = artist.artistId
                    )
                )
            }
            // On met les infos de la musique à jour :
            musicAlbumDao.updateAlbumOfMusic(
                musicId = legacyMusic.musicId,
                newAlbumId = newAlbum.albumId
            )

            checkAndDeleteAlbum(
                albumToCheck = legacyAlbum!!,
                musicAlbumDao = musicAlbumDao,
                albumDao = albumDao,
                albumArtistDao = albumArtistDao
            )
        }

        private fun getCorrespondingAlbum(
            musicId: UUID,
            albumDao: AlbumDao,
            musicAlbumDao: MusicAlbumDao
        ): Album? {
            val albumId: UUID? = musicAlbumDao.getAlbumIdFromMusicId(
                musicId = musicId
            )
            return if (albumId == null) {
                null
            } else {
                albumDao.getAlbumFromId(
                    albumId = albumId
                )
            }
        }

        fun getCorrespondingArtist(
            musicId: UUID,
            artistDao: ArtistDao,
            musicArtistDao: MusicArtistDao
        ): Artist? {
            val artistId: UUID? = musicArtistDao.getArtistIdFromMusicId(
                musicId = musicId
            )
            return if (artistId == null) {
                null
            } else {
                artistDao.getArtistFromId(
                    artistId = artistId
                )
            }
        }
    }
}