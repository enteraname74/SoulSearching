package com.github.enteraname74.soulsearching.composables.bottomsheets.music

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.composables.PlaylistSelectableComposable
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import java.util.UUID

@Composable
fun AddToPlaylistMenuBottomSheet(
    playlistsWithMusics: List<PlaylistWithMusics>,
    onDismiss: () -> Unit,
    onConfirm: (selectedPlaylists: List<UUID>) -> Unit,
    primaryColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
    retrieveCoverMethod: (UUID?) -> ImageBitmap?
) {

    val selectedPlaylistIds = remember {
        mutableStateListOf<UUID>()
    }

    Scaffold{
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(primaryColor)
                .padding(it)
                .padding(UiConstants.Spacing.medium)
        ) {
            SoulTopBar(
                title = strings.addToPlaylist,
                leftAction = {
                    onDismiss()
                    selectedPlaylistIds.clear()
                },
                rightAction = {
                    onConfirm(selectedPlaylistIds.toList())
                    selectedPlaylistIds.clear()
                },
                rightIcon = Icons.Rounded.Done,
                backgroundColor = Color.Transparent,
                contentColor = textColor
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(playlistsWithMusics) { playlistWithMusics ->
                    PlaylistSelectableComposable(
                        playlist = playlistWithMusics.playlist,
                        onClick = {
                            if (playlistWithMusics.playlist.playlistId in selectedPlaylistIds)
                                selectedPlaylistIds.remove(playlistWithMusics.playlist.playlistId)
                            else
                                selectedPlaylistIds.add(playlistWithMusics.playlist.playlistId)
                        },
                        isSelected = playlistWithMusics.playlist.playlistId in selectedPlaylistIds,
                        textColor = textColor,
                        cover = retrieveCoverMethod(playlistWithMusics.playlist.coverId)
                    )
                }
            }
        }
    }
}