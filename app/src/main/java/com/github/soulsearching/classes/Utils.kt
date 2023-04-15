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
import com.github.soulsearching.database.model.AlbumWithMusics
import com.github.soulsearching.database.model.Artist
import com.github.soulsearching.database.model.Music

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
            musicPlaylistDao: MusicPlaylistDao,
            musicAlbumDao: MusicAlbumDao,
            musicArtistDao: MusicArtistDao,
            musicToRemove: Music
        ) {
            musicDao.deleteMusic(music = musicToRemove)
            musicPlaylistDao.deleteMusicFromAllPlaylists(musicId = musicToRemove.musicId)
            musicAlbumDao.deleteMusicFromAlbum(musicId = musicToRemove.musicId)
            musicArtistDao.deleteMusicFromArtist(musicId = musicToRemove.musicId)
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

        suspend fun checkAndDeleteAlbum(
            albumToCheck: Album,
            musicsFromAlbum: List<Music>,
            albumDao: AlbumDao,
            albumArtistDao: AlbumArtistDao
        ) {
            if (musicsFromAlbum.isEmpty()) {
                removeAlbumFromApp(
                    albumToRemove = albumToCheck,
                    albumDao = albumDao,
                    albumArtistDao = albumArtistDao
                )
            }
        }

        suspend fun checkAndDeleteArtist(
            artistToCheck : Artist,
            musicsFromArtist : List<Music>,
            artistDao: ArtistDao
        ) {
            if (musicsFromArtist.isEmpty()) {
                removeArtistFromApp(
                    artistToRemove = artistToCheck,
                    artistDao = artistDao
                )
            }
        }

        suspend fun modifyMusicAlbum(
            musicAlbumDao: MusicAlbumDao,
            albumDao: AlbumDao,
            artistDao: ArtistDao,
            albumArtistDao: AlbumArtistDao,
            legacyMusic : Music,
            currentAlbum : String,
            currentCover : Bitmap?,
            currentArtist : String
        ) {
            // On supprime d'abord l'ancien lien.
            musicAlbumDao.deleteMusicFromAlbum(
                musicId = legacyMusic.musicId
            )


            Log.d("¨RE", "modifyAlbum")
            val albums = albumDao.getAllAlbumsWithMusicsSimple()
            var newAlbum = albums.find {
                (it.album.albumName == currentAlbum)
                        && (it.artist?.artistName == currentArtist)}

            Log.d("ModifyAlbum", "modifyAlbum")
            val albumWithMusics = albums.find {
                (it.album.albumName == legacyMusic.album)
                        && (it.artist?.artistName == legacyMusic.artist)
            }

            checkAndDeleteAlbum(
                albumToCheck = albumWithMusics!!.album,
                musicsFromAlbum = albumWithMusics.musics,
                albumDao = albumDao,
                albumArtistDao = albumArtistDao
            )

            if (newAlbum == null) {
                // C'est un nouvel album, il faut le créer :
                newAlbum = AlbumWithMusics(
                    album = Album(
                        albumName = currentAlbum,
                        albumCover = currentCover
                    ),
                    artist = artistDao.getArtistFromInfo(
                        artistName = currentArtist
                    )!!
                )
            }
            // On met les infos de la musique à jour :
            musicAlbumDao.updateAlbumOfMusic(
                musicId = legacyMusic.musicId,
                newAlbumId = newAlbum.album.albumId
            )
        }
    }
}