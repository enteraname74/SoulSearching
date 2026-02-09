package com.github.enteraname74.soulsearching.feature.playlistdetail.domain


import com.github.enteraname74.domain.ext.duration
import com.github.enteraname74.domain.model.*
import com.github.enteraname74.soulsearching.coreui.strings.strings
import java.util.*

data class PlaylistDetail(
    val id: UUID?,
    val type: PlaylistDetailType,
    val title: String,
    val subTitle: String,
    val cover: Cover?,
    val musics: List<Music>
)

enum class PlaylistDetailType {
    Playlist,
    Album,
    Artist,
    Month,
    Folder,
}

fun PlaylistWithMusics.toPlaylistDetail(): PlaylistDetail =
    PlaylistDetail(
        id = this.playlist.playlistId,
        type = PlaylistDetailType.Playlist,
        title = this.playlist.name,
        subTitle = strings.musics(this.musics.size),
        cover = this.cover,
        musics = this.musics,
    )

fun AlbumWithMusics.toPlaylistDetail(): PlaylistDetail =
    PlaylistDetail(
        id = this.album.albumId,
        type = PlaylistDetailType.Album,
        title = this.album.albumName,
        subTitle = this.album.artist.artistName,
        cover = this.cover,
        musics = this.musics,
    )

fun ArtistWithMusics.toPlaylistDetail(): PlaylistDetail =
    PlaylistDetail(
        id = this.artist.artistId,
        type = PlaylistDetailType.Artist,
        title = this.artist.artistName,
        subTitle = strings.musics(this.musics.size),
        cover = this.cover,
        musics = this.musics,
    )

fun MonthMusicList.toPlaylistDetail(): PlaylistDetail =
    PlaylistDetail(
        id = null,
        type = PlaylistDetailType.Month,
        title = this.month,
        subTitle = strings.musics(this.musics.size),
        cover = this.cover,
        musics = this.musics,
    )

fun MusicFolderList.toPlaylistDetail(): PlaylistDetail =
    PlaylistDetail(
        id = null,
        type = PlaylistDetailType.Folder,
        title = this.path,
        subTitle = strings.musics(this.musics.size),
        cover = this.cover,
        musics = this.musics,
    )