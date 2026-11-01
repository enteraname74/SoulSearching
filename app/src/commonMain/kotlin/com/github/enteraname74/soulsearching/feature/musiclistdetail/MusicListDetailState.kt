package com.github.enteraname74.soulsearching.feature.musiclistdetail

import androidx.compose.ui.graphics.ImageBitmap
import androidx.paging.PagingData
import com.github.enteraname74.soulsearching.composables.MusicItemLeadingSpec
import com.github.enteraname74.soulsearching.domain.model.AlbumPreview
import com.github.enteraname74.soulsearching.domain.model.CachedPlayedListUiSpec
import com.github.enteraname74.soulsearching.domain.model.CollectionElementPreview
import com.github.enteraname74.soulsearching.domain.model.Cover
import com.github.enteraname74.soulsearching.domain.model.Music
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration
import kotlin.uuid.Uuid
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec

sealed interface MusicListDetailState {
    data class Error(
        val error: SoulTextSpec,
        val navigateBack: () -> Unit,
    ) : MusicListDetailState

    data class Loading(
        val navigateBack: () -> Unit,
    ) : MusicListDetailState

    data class Data(
        val cachedPlayedListUiSpec: CachedPlayedListUiSpec?,
        val type: SoulTextSpec,
        val title: SoulTextSpec,
        val subTitle: SoulTextSpec,
        val cover: Cover?,
        val musics: Flow<PagingData<Music>>,
        val duration: Duration,
        val searchMusics: List<Music>,
        val optionalContent: OptionalContent?,
        val musicItemLeadingSpec: (musicPosition: Int) -> MusicItemLeadingSpec,
        val navigateBack: () -> Unit,
        val onSubtitleClicked: (() -> Unit)?,
        val onCloseSelection: () -> Unit,
        val openSearch: () -> Unit,
        val onLongClickOnMusic: (musicId: Uuid) -> Unit,
        val onShuffle: () -> Unit,
        val onPlay: (music: Music?) -> Unit,
        val onEdit: (() -> Unit)?,
        val onAddToList: (() -> Unit)?,
        val showMusicBottomSheet: (musicId: Uuid) -> Unit,
        val onCoverLoaded: (ImageBitmap?) -> Unit,
    ) : MusicListDetailState {
        sealed interface OptionalContent {
            data class Albums(
                val albums: List<AlbumPreview>,
                val onClick: (albumId: Uuid) -> Unit,
                val onLongClick: (albumId: Uuid) -> Unit,
            ) : OptionalContent

            data class CollectionElements(
                val elements: List<CollectionElementPreview>,
                val onClick: (element: CollectionElementPreview) -> Unit,
                val onLongClick: (element: CollectionElementPreview) -> Unit,
            ) : OptionalContent
        }
    }
}
