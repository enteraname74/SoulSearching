package com.github.enteraname74.localdesktop.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.model.PlayerMusic
import com.github.enteraname74.domain.model.PlayerWithMusicItem
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.localdesktop.tables.AlbumTable
import com.github.enteraname74.localdesktop.tables.ArtistTable
import com.github.enteraname74.localdesktop.tables.FolderTable
import com.github.enteraname74.localdesktop.tables.ImageCoverTable
import com.github.enteraname74.localdesktop.tables.MusicPlaylistTable
import com.github.enteraname74.localdesktop.tables.MusicTable
import com.github.enteraname74.localdesktop.tables.PlayerMusicTable
import com.github.enteraname74.localdesktop.tables.PlaylistTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.skia.Image
import org.jetbrains.skiko.toBufferedImage
import java.io.ByteArrayOutputStream
import java.util.Base64
import java.util.UUID

/**
 * Utils related to the use of Exposed.
 */
object ExposedUtils {
    /**
     * Builds an Album from a ResultRow.
     */
    fun resultRowToAlbum(row: ResultRow): Album = Album(
            albumId = UUID.fromString(row[AlbumTable.albumId]),
            albumName = row[AlbumTable.albumName],
            coverId = if (row[AlbumTable.coverId] == null) null else UUID.fromString(row[AlbumTable.coverId]),
            addedDate = row[AlbumTable.addedDate],
            nbPlayed = row[AlbumTable.nbPlayed],
            isInQuickAccess = row[AlbumTable.isInQuickAccess]
        )

    /**
     * Builds an Artist from a ResultRow.
     */
    fun resultRowToArtist(row: ResultRow): Artist = Artist(
        artistId = UUID.fromString(row[ArtistTable.artistId]),
        artistName = row[ArtistTable.artistName],
        coverId = if (row[ArtistTable.coverId] == null) null else UUID.fromString(row[ArtistTable.coverId]),
        addedDate = row[ArtistTable.addedDate],
        nbPlayed = row[ArtistTable.nbPlayed],
        isInQuickAccess = row[ArtistTable.isInQuickAccess]
    )

    /**
     * Builds a Folder from a ResultRow.
     */
    fun resultRowToFolder(row: ResultRow): Folder = Folder(
        folderPath = row[FolderTable.folderPath],
        isSelected = row[FolderTable.isSelected]
    )

    /**
     * Builds an ImageCover from a ResultRow.
     */
    fun resultRowToImageCover(row: ResultRow): ImageCover = ImageCover(
        id = row[ImageCoverTable.id].value,
        cover = stringToImageBitmap(row[ImageCoverTable.cover]),
        coverId = UUID.fromString(row[ImageCoverTable.coverId])
    )

    /**
     * Builds a Music from a ResultRow.
     */
    fun resultRowToMusic(row: ResultRow): Music = Music(
        musicId = UUID.fromString(row[MusicTable.musicId]),
        name = row[MusicTable.name],
        album = row[MusicTable.album],
        artist = row[MusicTable.artist],
        coverId = if(row[MusicTable.coverId] == null) null else UUID.fromString(row[MusicTable.coverId]),
        duration = row[MusicTable.duration],
        path = row[MusicTable.path],
        folder = row[MusicTable.folder],
        addedDate = row[MusicTable.addedDate],
        nbPlayed = row[MusicTable.nbPlayed],
        isInQuickAccess = row[MusicTable.isInQuickAccess],
        isHidden = row[MusicTable.isHidden]
    )

    /**
     * Builds a PlayerMusic from a ResultRow.
     */
    fun resultRowToPlayerMusic(row: ResultRow): PlayerMusic = PlayerMusic(
        id = row[PlayerMusicTable.id].value,
        playerMusicId = UUID.fromString(row[PlayerMusicTable.playerMusicId])
    )

    /**
     * Builds a Playlist from a ResultRow.
     */
    fun resultRowToPlaylist(row: ResultRow): Playlist = Playlist(
        playlistId = UUID.fromString(row[PlaylistTable.playlistId]),
        name = row[PlaylistTable.name],
        coverId = if (row[PlaylistTable.coverId] == null) null else UUID.fromString(row[PlaylistTable.coverId]),
        isFavorite = row[PlaylistTable.isFavorite],
        addedDate = row[PlaylistTable.addedDate],
        nbPlayed = row[PlaylistTable.nbPlayed],
        isInQuickAccess = row[PlaylistTable.isInQuickAccess]
    )

    /**
     * Builds a MusicPlaylist from a ResultRow.
     */
    fun resultRowToMusicPlaylist(row: ResultRow): MusicPlaylist = MusicPlaylist(
        id = row[MusicPlaylistTable.id].value,
        musicId = UUID.fromString(row[MusicPlaylistTable.musicId]),
        playlistId = UUID.fromString(row[MusicPlaylistTable.playlistId])
    )

    /**
     * Builds a PlayerWithMusicItem from a ResultRow.
     */
    fun resultRowToPlayerWithMusicItem(row: ResultRow): PlayerWithMusicItem = PlayerWithMusicItem(
        playerMusic = resultRowToPlayerMusic(row),
        music = resultRowToMusic(row)
    )

    /**
     * Converts a string representation of an ImageBitmap to an ImageBitmap.
     */
    private fun stringToImageBitmap(stringBitmap: String?): ImageBitmap? {
        if (stringBitmap == "" || stringBitmap == null) return null

        val imageBytes = Base64.getDecoder().decode(stringBitmap)
        return Image.makeFromEncoded(imageBytes).toComposeImageBitmap()
    }

    /**
     * Converts an ImageBitmap to a string representation of it.
     */
    fun imageBitmapToString(imageBitmap: ImageBitmap?): String? {
        if (imageBitmap == null) return null

        imageBitmap.asSkiaBitmap().readPixels()?.let {
            return Base64.getEncoder().encodeToString(it)
        }

        return null
    }
}