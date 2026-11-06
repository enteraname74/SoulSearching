package com.github.enteraname74.soulsearching.feature.multipleartistschoice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulCheckBox
import com.github.enteraname74.soulsearching.coreui.composable.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_download_done
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.multiple_artists_selection_title
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.multiple_artists_title
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.no_multiple_artists
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.navigation.SoulBackHandler
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.screen.SoulTemplateScreen
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulAdaptiveTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBarAction
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.composable.MultipleArtistsChoiceItem
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.composable.MultipleArtistsWarningCard

@Composable
fun MultipleArtistsChoiceScreen(
    state: MultipleArtistChoiceState,
) {
    SoulBackHandler {
        when (state) {
            MultipleArtistChoiceState.Loading -> {
                // no-op
            }
            is MultipleArtistChoiceState.NoMultipleArtists -> {
                state.navigateBack()
            }
            is MultipleArtistChoiceState.UserAction -> {
                state.navigateBack?.invoke()
            }
        }
    }
    when (state) {
        MultipleArtistChoiceState.Loading -> {
            SoulLoadingScreen(text = null)
        }

        is MultipleArtistChoiceState.UserAction -> {
            UserActionScreen(
                state = state,
            )
        }

        is MultipleArtistChoiceState.NoMultipleArtists -> {
            SoulTemplateScreen(
                leftAction = SoulTopBarAction.navigation(
                    onClick = state.navigateBack,
                ),
                icon = CoreRes.drawable.ic_download_done,
                text = CoreRes.string.no_multiple_artists.toTextSpec(),
                buttonSpec = null,
            )
        }
    }
}

@Composable
private fun UserActionScreen(
    state: MultipleArtistChoiceState.UserAction,
) {
    SoulScreen {
        Column {
            SoulAdaptiveTopBar(
                title = CoreRes.string.multiple_artists_title.toTextSpec(),
                rightAction = SoulTopBarAction.navigation(
                    onClick = state.onSaveSelection,
                ),
                leftAction = state.navigateBack?.let {
                    SoulTopBarAction.navigation(onClick = it)
                },
            )
            LazyColumnCompat(
                contentPadding = PaddingValues(
                    all = UiConstants.Spacing.medium
                ),
            ) {
                item(
                    key = WarningCardContentKey,
                    contentType = WarningCardContentType,
                ) {
                    MultipleArtistsWarningCard()
                }
                item(
                    key = SelectionTextKey,
                    contentType = SelectionTextContentType,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        SoulText(
                            text = CoreRes.string.multiple_artists_selection_title.toTextSpec(),
                            color = SoulSearchingColorTheme.colorScheme.text,
                            style = UiConstants.Typography.bodyTitle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        SoulCheckBox(
                            checked = state.toggleAllState,
                            onCheckedChange = state.onToggleAll,
                        )
                    }
                }
                items(
                    items = state.artists,
                    contentType = { ArtistChoicesContentType },
                    key = { it.artist.artistId }
                ) { artistChoice ->
                    MultipleArtistsChoiceItem(
                        artistChoice = artistChoice,
                        onClick = {
                            state.onToggleArtistChoice(artistChoice)
                        }
                    )
                }
                item(
                    key = PlayerSpacerContentType,
                    contentType = PlayerSpacerKey,
                ) {
                    SoulPlayerSpacer()
                }
            }
        }
    }
}

private const val WarningCardContentType: String = "WarningCardContentType"
private const val WarningCardContentKey: String = "WarningCardContentKey"
private const val SelectionTextContentType: String = "SelectionTextContentType"
private const val SelectionTextKey: String = "SelectionTextKey"
private const val ArtistChoicesContentType: String = "ArtistChoicesContentType"
private const val PlayerSpacerContentType: String = "PlayerSpacerContentType"
private const val PlayerSpacerKey: String = "PlayerSpacerKey"
