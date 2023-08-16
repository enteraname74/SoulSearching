package com.github.soulsearching.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.classes.SettingsUtils
import com.github.soulsearching.classes.enumsAndTypes.ElementEnum
import com.github.soulsearching.ui.theme.DynamicColor
import kotlinx.coroutines.launch

@Composable
fun MainPageVerticalShortcut(
    mainListState: LazyListState
) {
    val list = SettingsUtils.settingsViewModel.getListOfVisibleElements()
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        items(list) {
            Text(
                modifier = Modifier
                    .padding(
                        top = Constants.Spacing.medium,
                        bottom = Constants.Spacing.medium
                    )
                    .clickable {
                        coroutineScope.launch {
                            val pos = list.indexOf(it)
                            if (pos != -1) {
                                mainListState.animateScrollToItem(pos)
                            }
                        }
                    }
                    .rotate(-90f)
                    .vertical(),
                text = when (it) {
                    ElementEnum.QUICK_ACCESS -> stringResource(id = R.string.quick_access)
                    ElementEnum.PLAYLISTS -> stringResource(id = R.string.playlists)
                    ElementEnum.ALBUMS -> stringResource(id = R.string.albums)
                    ElementEnum.ARTISTS -> stringResource(id = R.string.artists)
                    ElementEnum.MUSICS -> stringResource(id = R.string.musics)
                },
                color = DynamicColor.onPrimary
            )
        }
        item {
            PlayerSpacer()
        }
    }
}

fun Modifier.vertical() =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.height, placeable.width) {
            placeable.place(
                x = -(placeable.width / 2 - placeable.height / 2),
                y = -(placeable.height / 2 - placeable.width / 2)
            )
        }
    }