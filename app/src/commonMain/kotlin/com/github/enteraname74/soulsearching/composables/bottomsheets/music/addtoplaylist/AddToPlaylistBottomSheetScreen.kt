package com.github.enteraname74.soulsearching.composables.bottomsheets.music.addtoplaylist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.enteraname74.soulsearching.composables.PlaylistSelectableComposable
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.composable.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_add
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBarDefaults
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarValidateAction

@Composable
fun AddToPlaylistBottomSheetScreen(
    viewModel: AddToPlaylistBottomSheetViewModel,
) {
    val state: AddToPlaylistBottomSheetState by viewModel.state.collectAsStateWithLifecycle()

    state.dialogState?.Dialog()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoulSearchingColorTheme.colorScheme.secondary)
            .padding(UiConstants.Spacing.medium)
    ) {
        SoulTopBar(
            title = strings.addToPlaylist,
            leftAction = TopBarNavigationAction(
                onClick = viewModel::close,
            ),
            rightAction = TopBarValidateAction(
                onClick = viewModel::confirm,
            ),
            colors = SoulTopBarDefaults.primary(
                containerColor = Color.Transparent,
                contentColor = SoulSearchingColorTheme.colorScheme.onSecondary,
            ),
        )
        LazyColumnCompat(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            createPlaylistItem(onCreatePlaylistClicked = viewModel::showCreatePlaylistDialog)
            items(
                items = state.playlistsWithMusics,
                key = { it.playlist.playlistId },
                contentType = { ALL_PLAYLISTS_CONTENT_TYPE }
            ) { playlistWithMusics ->
                PlaylistSelectableComposable(
                    modifier = Modifier.animateItem(),
                    playlistWithMusics = playlistWithMusics,
                    onClick = {
                        viewModel.toggleSelection(playlistId = playlistWithMusics.playlist.playlistId)
                    },
                    isSelected = state
                        .selectedPlaylistIds
                        .contains(playlistWithMusics.playlist.playlistId),
                    textColor = SoulSearchingColorTheme.colorScheme.onSecondary,
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

private fun LazyListScope.createPlaylistItem(
    onCreatePlaylistClicked: () -> Unit,
) {
    item {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickableWithHandCursor {
                    onCreatePlaylistClicked()
                }
                .padding(
                    vertical = UiConstants.Spacing.medium
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            SoulIcon(
                icon = CoreRes.drawable.ic_add,
                color = SoulSearchingColorTheme.colorScheme.onSecondary,
                size = UiConstants.ImageSize.medium,
            )
            Text(
                text = strings.createPlaylistDialogTitle,
                color = SoulSearchingColorTheme.colorScheme.onSecondary,
                style = UiConstants.Typography.bodyTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private const val ALL_PLAYLISTS_CONTENT_TYPE: String = "ALL_PLAYLISTS_CONTENT_TYPE"
private const val ALL_PLAYLISTS_SPACER_KEY: String = "ALL_PLAYLISTS_SPACER_KEY"
private const val ALL_PLAYLISTS_SPACER_CONTENT_TYPE: String = "ALL_PLAYLISTS_SPACER_CONTENT_TYPE"