package com.github.enteraname74.soulsearching.feature.elementpage.artistpage.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.composables.BigPreviewComposable
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.NoElementView
import com.github.enteraname74.soulsearching.coreui.strings.strings
import java.util.UUID

@Composable
fun ArtistAlbums(
    retrieveCoverMethod: (UUID?) -> ImageBitmap?,
    albums: List<Album>,
    onAlbumClick: (String) -> Unit = {},
    onAlbumLongClick: (Album) -> Unit = {},
) {
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
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
                contentPadding = PaddingValues(
                    start = UiConstants.Spacing.medium,
                    end = UiConstants.Spacing.medium
                )
            ) {
                items(
                    items = albums
                ) { element ->
                    BigPreviewComposable(
                        image = element.coverId?.let { retrieveCoverMethod(it) },
                        title = element.albumName,
                        onClick = {
                            onAlbumClick(element.albumId.toString())
                        },
                        onLongClick = { onAlbumLongClick(element) }
                    )
                }
            }
        } else {
            NoElementView()
        }
    }
}