package com.github.soulsearching.classes

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import com.github.soulsearching.database.dao.MusicAlbumDao
import com.github.soulsearching.database.dao.MusicArtistDao
import com.github.soulsearching.database.dao.MusicDao
import com.github.soulsearching.database.dao.MusicPlaylistDao
import com.github.soulsearching.database.model.Music

class Utils {
    companion object {
        fun getBitmapFromUri(uri : Uri, contentResolver : ContentResolver) : Bitmap {
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
            musicToRemove : Music
        ) {
            musicDao.deleteMusic(music = musicToRemove)
            musicPlaylistDao.deleteMusicFromAllPlaylists(musicId = musicToRemove.musicId)
            musicAlbumDao.deleteMusicFromAlbum(musicId = musicToRemove.musicId)
            musicArtistDao.deleteMusicFromArtist(musicId = musicToRemove.musicId)
        }
    }
}