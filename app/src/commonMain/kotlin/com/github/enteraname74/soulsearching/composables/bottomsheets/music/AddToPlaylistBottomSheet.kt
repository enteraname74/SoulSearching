package com.github.enteraname74.soulsearching.composables.bottomsheets.music

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.usecase.musicplaylist.CommonMusicPlaylistUseCase
import com.github.enteraname74.domain.usecase.playlist.CommonPlaylistUseCase
import com.github.enteraname74.soulsearching.composables.PlaylistSelectableComposable
import com.github.enteraname74.soulsearching.composables.dialog.CreatePlaylistDialog
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheetHandler
import com.github.enteraname74.soulsearching.coreui.composable.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBarDefaults
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarValidateAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*


class AddToPlaylistBottomSheet(
    private val onClose: () -> Unit,
    private val setDialogState: (SoulDialog?) -> Unit,
    private val selectedMusicIds: List<UUID>,
    private val addMusicToSelectedPlaylists: (selectedPlaylists: List<PlaylistWithMusics>) -> Unit,
    private val playlistsWithMusics: List<PlaylistWithMusics>,
) : SoulBottomSheet, KoinComponent {
    private val commonPlaylistUseCase: CommonPlaylistUseCase by inject()
    private val commonMusicPlaylistUseCase: CommonMusicPlaylistUseCase by inject()

    private fun showCreatePlaylistDialog(
        closeWithAnim: () -> Unit,
    ) {
        setDialogState(
            CreatePlaylistDialog(
                onDismiss = { setDialogState(null) },
                onConfirm = { playlistName ->
                    CoroutineScope(Dispatchers.IO).launch {
                        if (playlistName.isNotBlank()) {
                            val newPlaylist = Playlist(name = playlistName)
                            commonPlaylistUseCase.upsert(playlist = newPlaylist)
                            selectedMusicIds.forEach { musicId ->
                                commonMusicPlaylistUseCase.upsert(
                                    MusicPlaylist(
                                        musicId = musicId,
                                        playlistId = newPlaylist.playlistId,
                                    )
                                )
                            }
                        }

                        setDialogState(null)
                        closeWithAnim()
                    }
                }
            )
        )
    }

    @Composable
    override fun BottomSheet() {
        SoulBottomSheetHandler(
            onClose = onClose,
        ) { closeWithAnim ->
            Content(closeWithAnim = closeWithAnim)
        }
    }

    @Composable
    private fun Content(
        closeWithAnim: () -> Unit,
    ) {
        AddToPlaylistMenuBottomSheet(
            closeWithAnim = closeWithAnim,
            onConfirm = { selectedPlaylistsIds ->
                closeWithAnim()
                addMusicToSelectedPlaylists(selectedPlaylistsIds)
            },
            playlistsWithMusics = playlistsWithMusics,
        )
    }

    private fun LazyListScope.createPlaylistItem(
        closeWithAnim: () -> Unit,
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickableWithHandCursor {
                        showCreatePlaylistDialog(closeWithAnim)
                    }
                    .padding(
                        vertical = UiConstants.Spacing.medium
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                SoulIcon(
                    icon = Icons.Rounded.Add,
                    tint = SoulSearchingColorTheme.colorScheme.onSecondary,
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

    @Composable
    private fun AddToPlaylistMenuBottomSheet(
        playlistsWithMusics: List<PlaylistWithMusics>,
        closeWithAnim: () -> Unit,
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
                        closeWithAnim()
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
            LazyColumnCompat(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                createPlaylistItem(closeWithAnim = closeWithAnim)
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

    companion object {
        private const val ALL_PLAYLISTS_CONTENT_TYPE: String = "ALL_PLAYLISTS_CONTENT_TYPE"
        private const val ALL_PLAYLISTS_SPACER_KEY: String = "ALL_PLAYLISTS_SPACER_KEY"
        private const val ALL_PLAYLISTS_SPACER_CONTENT_TYPE: String = "ALL_PLAYLISTS_SPACER_CONTENT_TYPE"
    }
}