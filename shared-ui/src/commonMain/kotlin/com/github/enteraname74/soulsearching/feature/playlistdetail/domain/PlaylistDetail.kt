package com.github.enteraname74.soulsearching.feature.playlistdetail.domain


import com.github.enteraname74.domain.model.*
import com.github.enteraname74.soulsearching.coreui.strings.strings
import java.util.*

data class PlaylistDetail(
    val id: UUID?,
    val title: String,
    val subTitle: String?,
    val cover: Cover?,
    val musics: List<Music>
)

fun PlaylistWithMusics.toPlaylistDetail(): PlaylistDetail =
    PlaylistDetail(
        id = this.playlist.playlistId,
        title = this.playlist.name,
        subTitle = strings.musics(this.musics.size),
        cover = this.cover,
        musics = this.musics,
    )

fun AlbumWithMusics.toPlaylistDetail(): PlaylistDetail =
    PlaylistDetail(
        id = this.album.albumId,
        title = this.album.albumName,
        subTitle = this.artist?.artistName,
        cover = this.cover,
        musics = this.musics,
    )

fun ArtistWithMusics.toPlaylistDetail(): PlaylistDetail =
    PlaylistDetail(
        id = this.artist.artistId,
        title = this.artist.artistName,
        subTitle = strings.musics(this.musics.size),
        cover = this.cover,
        musics = this.musics,
    )

fun MonthMusicList.toPlaylistDetail(): PlaylistDetail =
    PlaylistDetail(
        id = null,
        title = this.month,
        subTitle = strings.musics(this.musics.size),
        cover = this.cover,
        musics = this.musics,
    )

fun MusicFolderList.toPlaylistDetail(): PlaylistDetail =
    PlaylistDetail(
        id = null,
        title = this.path,
        subTitle = strings.musics(this.musics.size),
        cover = this.cover,
        musics = this.musics,
    )