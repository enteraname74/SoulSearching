package com.github.enteraname74.soulsearching.composables.bottomsheets.music

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.composables.PlaylistSelectableComposable
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBarDefaults
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarValidateAction

@Composable
fun AddToPlaylistMenuBottomSheet(
    playlistsWithMusics: List<PlaylistWithMusics>,
    onDismiss: () -> Unit,
    onConfirm: (selectedPlaylists: List<PlaylistWithMusics>) -> Unit,
    primaryColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
) {
    val selectedPlaylists = remember {
        mutableStateListOf<PlaylistWithMusics>()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(primaryColor)
            .padding(UiConstants.Spacing.medium)
    ) {
        SoulTopBar(
            title = strings.addToPlaylist,
            leftAction = TopBarNavigationAction(
                onClick = {
                    onDismiss()
                    selectedPlaylists.clear()
                }
            ),
            rightAction = TopBarValidateAction(
                onClick = {
                    onConfirm(selectedPlaylists.toList())
                    selectedPlaylists.clear()
                }
            ),
            colors = SoulTopBarDefaults.primary(
                containerColor = Color.Transparent,
                contentColor = textColor,
            ),
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(
                items = playlistsWithMusics,
                key = { it.playlist.playlistId },
                contentType = { ALL_PLAYLISTS_CONTENT_TYPE }
            ) { playlistWithMusics ->
                PlaylistSelectableComposable(
                    modifier = Modifier.animateItem(),
                    playlistWithMusics = playlistWithMusics,
                    onClick = {
                        if (playlistWithMusics in selectedPlaylists)
                            selectedPlaylists.remove(playlistWithMusics)
                        else
                            selectedPlaylists.add(playlistWithMusics)
                    },
                    isSelected = playlistWithMusics in selectedPlaylists,
                    textColor = textColor,
                )
            }

            item(
                key = ALL_PLAYLISTS_SPACER_KEY,
                contentType = ALL_PLAYLISTS_SPACER_CONTENT_TYPE,
            ) {
                SoulPlayerSpacer()
            }
        }
    }
}

private const val ALL_PLAYLISTS_CONTENT_TYPE: String = "ALL_PLAYLISTS_CONTENT_TYPE"
private const val ALL_PLAYLISTS_SPACER_KEY: String = "ALL_PLAYLISTS_SPACER_KEY"
private const val ALL_PLAYLISTS_SPACER_CONTENT_TYPE: String = "ALL_PLAYLISTS_SPACER_CONTENT_TYPE"
