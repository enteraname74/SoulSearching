package com.github.soulsearching.composables.bottomsheets.music

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.soulsearching.Constants
import com.github.soulsearching.colortheme.domain.model.SoulSearchingColorTheme
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.PlaylistSelectableComposable
import com.github.soulsearching.strings.strings
import java.util.UUID

@Composable
fun AddToPlaylistMenuBottomSheet(
    playlistsWithMusics: List<PlaylistWithMusics>,
    onDismiss: () -> Unit,
    validationAction: (selectedPlaylists: List<UUID>) -> Unit,
    primaryColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary
) {

    val selectedPlaylistIds = rememberSaveable {
        mutableStateListOf<UUID>()
    }

    LaunchedEffect(null) {
        selectedPlaylistIds.clear()
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(primaryColor)
                .padding(it)
                .padding(Constants.Spacing.medium)
        ) {
            AppHeaderBar(
                title = strings.addToPlaylist,
                leftAction = onDismiss,
                rightAction = { validationAction(selectedPlaylistIds) },
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
                        textColor = textColor
                    )
                }
            }
        }
    }
}