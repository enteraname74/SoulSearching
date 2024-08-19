package com.github.enteraname74.soulsearching.feature.elementpage.domain


import com.github.enteraname74.domain.model.*
import com.github.enteraname74.soulsearching.coreui.strings.strings
import java.util.*

data class PlaylistDetail(
    val id: UUID?,
    val title: String,
    val subTitle: String?,
    val coverId: UUID?,
    val musics: List<Music>
)

fun PlaylistWithMusics.toPlaylistDetail(): PlaylistDetail =
    PlaylistDetail(
        id = this.playlist.playlistId,
        title = this.playlist.name,
        subTitle = strings.musics(this.musics.size),
        coverId = this.playlist.coverId,
        musics = this.musics,
    )

fun AlbumWithMusics.toPlaylistDetail(): PlaylistDetail =
    PlaylistDetail(
        id = this.album.albumId,
        title = this.album.albumName,
        subTitle = this.artist?.artistName,
        coverId = this.album.coverId,
        musics = this.musics,
    )

fun ArtistWithMusics.toPlaylistDetail(): PlaylistDetail =
    PlaylistDetail(
        id = this.artist.artistId,
        title = this.artist.artistName,
        subTitle = strings.musics(this.musics.size),
        coverId = this.artist.coverId,
        musics = this.musics,
    )

fun MonthMusicList.toPlaylistDetail(): PlaylistDetail =
    PlaylistDetail(
        id = null,
        title = this.month,
        subTitle = strings.musics(this.musics.size),
        coverId = this.coverId,
        musics = this.musics,
    )

fun MusicFolderList.toPlaylistDetail(): PlaylistDetail =
    PlaylistDetail(
        id = null,
        title = this.path,
        subTitle = strings.musics(this.musics.size),
        coverId = this.coverId,
        musics = this.musics,
    )