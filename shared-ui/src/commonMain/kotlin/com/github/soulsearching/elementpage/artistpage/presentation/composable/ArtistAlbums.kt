package com.github.soulsearching.elementpage.artistpage.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.enteraname74.domain.model.Album
import com.github.soulsearching.Constants
import com.github.soulsearching.colortheme.domain.model.SoulSearchingColorTheme
import com.github.soulsearching.composables.BigPreviewComposable
import com.github.soulsearching.mainpage.presentation.composable.NoElementView
import com.github.soulsearching.strings.strings
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
            .padding(bottom = Constants.Spacing.veryLarge),
        verticalArrangement = Arrangement.spacedBy(Constants.Spacing.small)
    ) {
        Text(
            modifier = Modifier
                .padding(start = Constants.Spacing.medium),
            text = strings.albums,
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
        if (albums.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Constants.Spacing.medium),
                contentPadding = PaddingValues(
                    start = Constants.Spacing.medium,
                    end = Constants.Spacing.medium
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