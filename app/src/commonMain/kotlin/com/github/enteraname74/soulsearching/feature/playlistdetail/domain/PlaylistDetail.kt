package com.github.enteraname74.soulsearching.feature.playlistdetail.domain


import androidx.paging.PagingData
import com.github.enteraname74.domain.model.AlbumPreview
import com.github.enteraname74.domain.model.ArtistPreview
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.MonthMusicList
import com.github.enteraname74.domain.model.MonthMusicsPreview
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicFolderList
import com.github.enteraname74.domain.model.MusicFolderPreview
import com.github.enteraname74.domain.model.PlaylistPreview
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.composables.MusicItemLeadingSpec
import com.github.enteraname74.soulsearching.coreui.strings.strings
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import kotlin.time.Duration

data class PlaylistDetail(
    val id: UUID?,
    val type: PlaylistDetailType,
    val title: String,
    val subTitle: String,
    val cover: Cover?,
    val musics: Flow<PagingData<Music>>,
    val duration: Duration,
    val musicItemLeadingSpec: (musicPosition: Int) -> MusicItemLeadingSpec,
)

enum class PlaylistDetailType {
    Playlist,
    Album,
    Artist,
    Month,
    Folder;
}

fun PlaylistPreview.toPlaylistDetail(
    musics: Flow<PagingData<Music>>,
    duration: Duration,
): PlaylistDetail =
    PlaylistDetail(
        id = this.id,
        type = PlaylistDetailType.Playlist,
        title = this.name,
        subTitle = strings.musics(this.totalMusics),
        cover = this.cover,
        musics = musics,
        duration = duration,
        musicItemLeadingSpec = { MusicItemLeadingSpec.Cover },
    )

fun AlbumPreview.toPlaylistDetail(
    shouldShowTrackPosition: Boolean,
    musics: Flow<PagingData<Music>>,
    duration: Duration,
): PlaylistDetail =
    PlaylistDetail(
        id = this.id,
        type = PlaylistDetailType.Album,
        title = this.name,
        subTitle = this.artist,
        cover = this.cover,
        musics = musics,
        duration = duration,
        musicItemLeadingSpec = { musicPosition ->
            if (shouldShowTrackPosition) {
                MusicItemLeadingSpec.Position(pos = musicPosition)
            } else {
                MusicItemLeadingSpec.Cover
            }
        }
    )

fun ArtistPreview.toPlaylistDetail(
    musics: Flow<PagingData<Music>>,
    duration: Duration,
): PlaylistDetail =
    PlaylistDetail(
        id = this.id,
        type = PlaylistDetailType.Artist,
        title = this.name,
        subTitle = strings.musics(this.totalMusics),
        cover = this.cover,
        musics = musics,
        duration = duration,
        musicItemLeadingSpec = { MusicItemLeadingSpec.Cover },
    )

fun MonthMusicsPreview.toPlaylistDetail(
    musics: Flow<PagingData<Music>>,
    duration: Duration,
): PlaylistDetail =
    PlaylistDetail(
        id = null,
        type = PlaylistDetailType.Month,
        title = this.month,
        subTitle = strings.musics(this.totalMusics),
        cover = this.cover,
        musics = musics,
        duration = duration,
        musicItemLeadingSpec = { MusicItemLeadingSpec.Cover },
    )

fun MusicFolderPreview.toPlaylistDetail(
    musics: Flow<PagingData<Music>>,
    duration: Duration,
): PlaylistDetail =
    PlaylistDetail(
        id = null,
        type = PlaylistDetailType.Folder,
        title = this.folder,
        subTitle = strings.musics(this.totalMusics),
        cover = this.cover,
        musics = musics,
        duration = duration,
        musicItemLeadingSpec = { MusicItemLeadingSpec.Cover },
    )