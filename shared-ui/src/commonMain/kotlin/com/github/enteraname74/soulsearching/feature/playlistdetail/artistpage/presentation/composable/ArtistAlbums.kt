package com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.presentation.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.soulsearching.composables.BigPreviewComposable
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.list.LazyRowCompat
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionState
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.NoElementView
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.PlaylistPartTitle
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistViewUiUtils
import java.util.*

@Composable
fun ArtistAlbums(
    albums: List<AlbumWithMusics>,
    multiSelectionState: MultiSelectionState,
    onAlbumClick: (albumId: UUID) -> Unit = {},
    onAlbumLongClick: (AlbumWithMusics) -> Unit = {},
) {
    val lazyListState = rememberLazyListState()
    val canShowColumnLayout = PlaylistViewUiUtils.canShowColumnLayout()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = UiConstants.Spacing.veryLarge),
        verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small)
    ) {
        PlaylistPartTitle(title = strings.albums)

        if (albums.isNotEmpty()) {
            LazyRowCompat(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    if (canShowColumnLayout) {
                        UiConstants.Spacing.large
                    } else {
                        UiConstants.Spacing.medium
                    }
                ),
                contentPadding = PaddingValues(
                    horizontal = if (canShowColumnLayout) {
                        UiConstants.Spacing.huge
                    } else {
                        UiConstants.Spacing.medium
                    },
                )
            ) {
                items(
                    items = albums,
                    key = { it.album.albumId },
                    contentType = { ARTIST_ALBUM_CONTENT_TYPE },
                ) { element ->
                    BigPreviewComposable(
                        modifier = Modifier
                            .animateItem(),
                        cover = element.cover,
                        title = element.album.albumName,
                        onClick = {
                            onAlbumClick(element.album.albumId)
                        },
                        imageSize = if (canShowColumnLayout) {
                            LARGE_COVER_SIZE
                        } else {
                            UiConstants.ImageSize.veryLarge
                        },
                        onLongClick = { onAlbumLongClick(element) },
                        isSelected = multiSelectionState.selectedIds.contains(element.album.albumId),
                        isSelectionModeOn = multiSelectionState.selectedIds.isNotEmpty(),
                    )
                }
            }
        } else {
            NoElementView()
        }
    }
}

private const val ARTIST_ALBUM_CONTENT_TYPE: String = "ARTIST_ALBUM_CONTENT_TYPE"
private val LARGE_COVER_SIZE: Dp = 182.dp