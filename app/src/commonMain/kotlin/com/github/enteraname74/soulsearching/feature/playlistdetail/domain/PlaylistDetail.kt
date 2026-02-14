package com.github.enteraname74.soulsearching.feature.playlistdetail.domain


import androidx.paging.PagingData
import com.github.enteraname74.domain.ext.duration
import com.github.enteraname74.domain.model.*
import com.github.enteraname74.soulsearching.composables.MusicItemLeadingSpec
import com.github.enteraname74.soulsearching.coreui.strings.strings
import kotlinx.coroutines.flow.Flow
import java.util.*

data class PlaylistDetail(
    val id: UUID?,
    val type: PlaylistDetailType,
    val title: String,
    val subTitle: String,
    val cover: Cover?,
    val musics: Flow<PagingData<Music>>,
    val musicItemLeadingSpec: (musicPosition: Int) -> MusicItemLeadingSpec,
)

enum class PlaylistDetailType {
    Playlist,
    Album,
    Artist,
    Month,
    Folder;
}

fun PlaylistWithMusics.toPlaylistDetail(musics: Flow<PagingData<Music>>): PlaylistDetail =
    PlaylistDetail(
        id = this.playlist.playlistId,
        type = PlaylistDetailType.Playlist,
        title = this.playlist.name,
        subTitle = strings.musics(this.musics.size),
        cover = this.cover,
        musics = musics,
        musicItemLeadingSpec = { MusicItemLeadingSpec.Cover },
    )

fun AlbumWithMusics.toPlaylistDetail(
    shouldShowTrackPosition: Boolean,
    musics: Flow<PagingData<Music>>
): PlaylistDetail =
    PlaylistDetail(
        id = this.album.albumId,
        type = PlaylistDetailType.Album,
        title = this.album.albumName,
        subTitle = this.album.artist.artistName,
        cover = this.cover,
        musics = musics,
        musicItemLeadingSpec = { musicPosition ->
            if (shouldShowTrackPosition) {
                MusicItemLeadingSpec.Position(pos = musicPosition)
            } else {
                MusicItemLeadingSpec.Cover
            }
        }
    )

fun ArtistWithMusics.toPlaylistDetail(musics: Flow<PagingData<Music>>): PlaylistDetail =
    PlaylistDetail(
        id = this.artist.artistId,
        type = PlaylistDetailType.Artist,
        title = this.artist.artistName,
        subTitle = strings.musics(this.musics.size),
        cover = this.cover,
        musics = musics,
        musicItemLeadingSpec = { MusicItemLeadingSpec.Cover },
    )

fun MonthMusicList.toPlaylistDetail(musics: Flow<PagingData<Music>>): PlaylistDetail =
    PlaylistDetail(
        id = null,
        type = PlaylistDetailType.Month,
        title = this.month,
        subTitle = strings.musics(this.musics.size),
        cover = this.cover,
        musics = musics,
        musicItemLeadingSpec = { MusicItemLeadingSpec.Cover },
    )

fun MusicFolderList.toPlaylistDetail(musics: Flow<PagingData<Music>>): PlaylistDetail =
    PlaylistDetail(
        id = null,
        type = PlaylistDetailType.Folder,
        title = this.path,
        subTitle = strings.musics(this.musics.size),
        cover = this.cover,
        musics = musics,
        musicItemLeadingSpec = { MusicItemLeadingSpec.Cover },
    )