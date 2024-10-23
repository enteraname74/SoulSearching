package com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.presentation.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.soulsearching.composables.BigPreviewComposable
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.list.SoulHorizontalScrollBar
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.NoElementView
import java.util.UUID

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArtistAlbums(
    albums: List<AlbumWithMusics>,
    onAlbumClick: (albumId: UUID) -> Unit = {},
    onAlbumLongClick: (Album) -> Unit = {},
) {
    val lazyListState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = UiConstants.Spacing.veryLarge),
        verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small)
    ) {
        Text(
            modifier = Modifier
                .padding(start = UiConstants.Spacing.medium),
            text = strings.albums,
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
        if (albums.isNotEmpty()) {
            Column {
                LazyRow(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
                    contentPadding = PaddingValues(
                        start = UiConstants.Spacing.medium,
                        end = UiConstants.Spacing.medium
                    )
                ) {
                    items(
                        items = albums,
                        key = { it.album.albumId },
                        contentType = { ARTIST_ALBUM_CONTENT_TYPE },
                    ) { element ->
                        BigPreviewComposable(
                            modifier = Modifier
                                .animateItemPlacement(),
                            cover = element.cover,
                            title = element.album.albumName,
                            onClick = {
                                onAlbumClick(element.album.albumId)
                            },
                            onLongClick = { onAlbumLongClick(element.album) }
                        )
                    }
                }
                SoulHorizontalScrollBar(
                    lazyListState = lazyListState
                )
            }
        } else {
            NoElementView()
        }
    }
}

private const val ARTIST_ALBUM_CONTENT_TYPE: String = "ARTIST_ALBUM_CONTENT_TYPE"