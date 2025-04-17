package com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DownloadDone
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.composable.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.screen.SoulTemplateScreen
import com.github.enteraname74.soulsearching.coreui.screen.TemplateScreenButtonSpec
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarValidateAction
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.FETCHED_MUSICS_SPACER_CONTENT_TYPE
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.composable.MusicSelectableComposable
import com.github.enteraname74.soulsearching.features.musicmanager.fetching.SelectableMusicItem
import java.util.*

@Composable
fun SettingsAddMusicsDataScreen(
    navigateBack: () -> Unit,
    fetchSongs: () -> Unit,
    fetchedMusics: List<SelectableMusicItem>,
    toggleMusicSelectedState: (musicId: UUID) -> Unit,
    saveSelectedSongs: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        if (fetchedMusics.isEmpty()) {
            SoulTemplateScreen(
                leftAction = TopBarNavigationAction(onClick = navigateBack),
                icon = Icons.Rounded.DownloadDone,
                title = strings.addMusicsTitle,
                text = strings.noNewMusics,
                buttonSpec = TemplateScreenButtonSpec(
                    text = strings.searchForNewSongs,
                    onClick = fetchSongs,
                ),
            )
        } else {
            SoulTopBar(
                title = strings.addMusicsTitle,
                leftAction = TopBarNavigationAction(onClick = navigateBack),
                rightAction = TopBarValidateAction(onClick = saveSelectedSongs),
            )
            LazyColumnCompat {
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
