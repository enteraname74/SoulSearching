package com.github.soulsearching.classes

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import com.github.soulsearching.database.dao.*
import com.github.soulsearching.database.model.Album
import com.github.soulsearching.database.model.AlbumArtist
import com.github.soulsearching.database.model.Artist
import com.github.soulsearching.database.model.Music
import java.util.*

class Utils {
    companion object {
        fun getBitmapFromUri(uri: Uri, contentResolver: ContentResolver): Bitmap {
            return if (Build.VERSION.SDK_INT >= 29) {
                contentResolver.loadThumbnail(
                    uri,
                    Size(400, 400),
                    null
                )
            } else {
                Bitmap.createScaledBitmap(
                    MediaStore.Images.Media.getBitmap(
                        contentResolver,
                        uri
                    ), 400, 400, false
                )
            }
        }

        suspend fun removeMusicFromApp(
            musicDao: MusicDao,
            albumDao: AlbumDao,
            artistDao: ArtistDao,
            albumArtistDao: AlbumArtistDao,
            musicPlaylistDao: MusicPlaylistDao,
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
            musicPlaylistDao.deleteMusicFromAllPlaylists(musicId = musicToRemove.musicId)
            musicAlbumDao.deleteMusicFromAlbum(musicId = musicToRemove.musicId)
            musicArtistDao.deleteMusicFromArtist(musicId = musicToRemove.musicId)

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
            artistDao: ArtistDao
        ) {
            artistDao.deleteArtist(artist = artistToRemove)
        }

        private suspend fun checkAndDeleteAlbum(
            albumToCheck: Album,
            albumDao: AlbumDao,
            musicAlbumDao: MusicAlbumDao,
            albumArtistDao: AlbumArtistDao
        ) {
            Log.d("TOT", musicAlbumDao.getNumberOfMusicsFromAlbum(
                albumId = albumToCheck.albumId
            ).toString())
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
            Log.d(
                "Number of musics in artist", artistToCheck.artistName + " : " +
                        musicArtistDao.getNumberOfMusicsFromArtist(artistId = artistToCheck.artistId)
                            .toString()
            )
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
            currentCover: Bitmap?,
        ) {
            Log.d("¨RE", "modifyAlbum")
            // On récupère l'ancien album :
            val legacyAlbum = getCorrespondingAlbum(
                musicId = legacyMusic.musicId,
                albumDao = albumDao,
                musicAlbumDao = musicAlbumDao
            )

            var newAlbum = getCorrespondingAlbum(
                musicId = legacyMusic.musicId,
                albumDao = albumDao,
                musicAlbumDao = musicAlbumDao
            )

            if (newAlbum == null) {
                // C'est un nouvel album, il faut le créer :
                val album = Album(
                    albumName = currentAlbum,
                    albumCover = currentCover
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