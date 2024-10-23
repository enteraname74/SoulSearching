package com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarValidateAction
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.FETCHED_MUSICS_SPACER_CONTENT_TYPE
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.composable.MusicSelectableComposable
import com.github.enteraname74.soulsearching.features.filemanager.musicfetching.SelectableMusicItem
import java.util.UUID

@Composable
fun SettingsAddMusicsDataScreen(
    navigateBack: () -> Unit,
    fetchedMusics: List<SelectableMusicItem>,
    toggleMusicSelectedState: (musicId: UUID) -> Unit,
    saveSelectedSongs: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        if (fetchedMusics.isEmpty()) {
            SoulTopBar(
                title = strings.addMusicsTitle,
                leftAction = TopBarNavigationAction(onClick = navigateBack),
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = strings.noNewMusics,
                    color = SoulSearchingColorTheme.colorScheme.onPrimary
                )
            }
        } else {
            SoulTopBar(
                title = strings.addMusicsTitle,
                leftAction = TopBarNavigationAction(onClick = navigateBack),
                rightAction = TopBarValidateAction(onClick = saveSelectedSongs),
            )
            LazyColumn {
                items(
                    key = { it.music.musicId },
                    contentType = { FETCHED_MUSICS_CONTENT_TYPE },
                    items = fetchedMusics
                ) {
                    MusicSelectableComposable(
                        music = it.music,
                        onClick = {
                            toggleMusicSelectedState(it.music.musicId)
                        },
                        isSelected = it.isSelected
                    )
                }
                item(
                    key = FETCHED_MUSICS_SPACER_KEY,
                    contentType = FETCHED_MUSICS_SPACER_CONTENT_TYPE,
                ) {
                    SoulPlayerSpacer()
                }
            }
        }
    }
}

private const val FETCHED_MUSICS_CONTENT_TYPE: String = "FETCHED_MUSICS_CONTENT_TYPE"
private const val FETCHED_MUSICS_SPACER_KEY: String = "FETCHED_MUSICS_SPACER_KEY"
