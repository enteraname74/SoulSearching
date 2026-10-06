package com.github.enteraname74.soulsearching.feature.musiclistdetail.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.composables.BigPreviewComposable
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.list.LazyRowCompat
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.domain.model.CollectionElementPreview
import com.github.enteraname74.soulsearching.feature.musiclistdetail.MusicListDetailState

@Composable
fun CollectionElements(
    spec: MusicListDetailState.Data.OptionalContent.CollectionElements,
) {
    val lazyListState = rememberLazyListState()
    val windowSize = rememberWindowSize()
    val canShowColumnLayout = MusicListDetailUiUtils.canShowColumnLayout() && windowSize != WindowSize.Small

    Column(
        modifier = Modifier.fillMaxWidth().padding(bottom = UiConstants.Spacing.veryLarge),
        verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small),
    ) {
        PlaylistPartTitle(title = strings.inThisCollection)
        LazyRowCompat(
            state = lazyListState,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                if (canShowColumnLayout) UiConstants.Spacing.large else UiConstants.Spacing.medium
            ),
            contentPadding = PaddingValues(
                horizontal = if (canShowColumnLayout) UiConstants.Spacing.huge else UiConstants.Spacing.medium
            ),
        ) {
            items(
                items = spec.elements,
                key = { "${it.typeKey}-${it.id}" },
                contentType = { it.typeKey },
            ) { element ->
                BigPreviewComposable(
                    modifier = Modifier.animateItem(),
                    cover = element.cover,
                    title = element.name,
                    text = when (element) {
                        is CollectionElementPreview.Playlist -> strings.musics(element.preview.totalMusics)
                        is CollectionElementPreview.Album -> element.preview.artist
                        is CollectionElementPreview.Artist -> strings.musics(element.preview.totalMusics)
                    },
                    onClick = { spec.onClick(element) },
                    imageSize = if (canShowColumnLayout) LARGE_COVER_SIZE else UiConstants.ImageSize.veryLarge,
                    onLongClick = { spec.onLongClick(element) },
                    isFavoritePlaylist = (element as? CollectionElementPreview.Playlist)?.preview?.isFavorite == true,
                )
            }
        }
    }
}

private val LARGE_COVER_SIZE: Dp = 174.dp

private val CollectionElementPreview.typeKey: String
    get() = when (this) {
        is CollectionElementPreview.Playlist -> "playlist"
        is CollectionElementPreview.Album -> "album"
        is CollectionElementPreview.Artist -> "artist"
    }
