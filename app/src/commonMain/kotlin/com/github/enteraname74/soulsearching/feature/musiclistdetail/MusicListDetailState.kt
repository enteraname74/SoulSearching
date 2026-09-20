package com.github.enteraname74.soulsearching.feature.musiclistdetail

import androidx.compose.ui.graphics.ImageBitmap
import androidx.paging.PagingData
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicListDetailId
import com.github.enteraname74.soulsearching.composables.MusicItemLeadingSpec
import com.github.enteraname74.soulsearching.domain.model.CachedPlayedListUiSpec
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration
import kotlin.uuid.Uuid

sealed interface MusicListDetailState {
    data class Error(
        val error: String,
        val navigateBack: () -> Unit,
    ) : MusicListDetailState

    data class Loading(
        val navigateBack: () -> Unit,
    ) : MusicListDetailState

    data class Data(
        val cachedPlayedListUiSpec: CachedPlayedListUiSpec?,
        val type: String,
        val title: String,
        val subTitle: String,
        val cover: Cover?,
        val musics: Flow<PagingData<Music>>,
        val duration: Duration,
        val searchMusics: List<Music>,
        val optionalContent: OptionalContent?,
        val musicItemLeadingSpec: (musicPosition: Int) -> MusicItemLeadingSpec,
        val navigateBack: () -> Unit,
        val onSubtitleClicked: (() -> Unit)?,
        val onCloseSelection: () -> Unit,
        val onLongClickOnMusic: (musicId: Uuid) -> Unit,
        val onShuffle: () -> Unit,
        val onPlay: (music: Music?) -> Unit,
        val onEdit: (() -> Unit)?,
        val onSearch: (search: String) -> Unit,
        val showMusicBottomSheet: (musicId: Uuid) -> Unit,
        val onCoverLoaded: (ImageBitmap?) -> Unit,
    ) : MusicListDetailState {
        sealed interface OptionalContent {
            data class Albums(
                val albums: List<AlbumWithMusics>,
                val onClick: (albumId: Uuid) -> Unit,
                val onLongClick: (albumId: Uuid) -> Unit,
            ) : OptionalContent
        }
    }
}