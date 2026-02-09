package com.github.enteraname74.soulsearching.feature.multipleartistschoice

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DownloadDone
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulCheckBox
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.screen.SoulTemplateScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarValidateAction
import com.github.enteraname74.soulsearching.coreui.utils.LaunchInit
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.composable.MultipleArtistsChoiceItem
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.composable.MultipleArtistsWarningCard
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.state.ArtistChoice
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.state.MultipleArtistChoiceState
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.state.MultipleArtistsChoiceNavigationState

@Composable
fun MultipleArtistsChoiceRoute(
    viewModel: MultipleArtistsChoiceViewModel,
    onNavigationState: (MultipleArtistsChoiceNavigationState) -> Unit,
) {
    val state: MultipleArtistChoiceState by viewModel.state.collectAsState()
    val navigationState: MultipleArtistsChoiceNavigationState by viewModel.navigationState.collectAsState()

    LaunchInit {
        viewModel.init()
    }

    LaunchedEffect(navigationState) {
        onNavigationState(navigationState)
        viewModel.consumeNavigation()
    }

    MainComposable(
        state = state,
        onToggleArtistChoice = viewModel::toggleSelection,
        onSaveSelection = viewModel::saveSelection,
        navigateBack = viewModel::navigateBack,
        onToggleAll = viewModel::toggleAll,
        mode = viewModel.mode,
    )
}

@Composable
private fun MainComposable(
    state: MultipleArtistChoiceState,
    mode: MultipleArtistsChoiceMode,
    onSaveSelection: () -> Unit,
    navigateBack: () -> Unit,
    onToggleArtistChoice: (ArtistChoice) -> Unit,
    onToggleAll: () -> Unit,
) {
    when (state) {
        MultipleArtistChoiceState.Loading -> {
            SoulLoadingScreen(text = null)
        }

        is MultipleArtistChoiceState.UserAction -> {
            UserActionScreen(
                state = state,
                onSaveSelection = onSaveSelection,
                onToggleArtistChoice = onToggleArtistChoice,
                navigateBack = navigateBack,
                onToggleAll = onToggleAll,
                mode = mode,
            )
        }

        MultipleArtistChoiceState.NoMultipleArtists -> {
            SoulTemplateScreen(
                leftAction = TopBarNavigationAction(
                    onClick = navigateBack,
                ),
                icon = Icons.Rounded.DownloadDone,
                text = strings.noMultipleArtists,
                buttonSpec = null,
            )
        }
    }
}

@Composable
private fun UserActionScreen(
    state: MultipleArtistChoiceState.UserAction,
    mode: MultipleArtistsChoiceMode,
    onSaveSelection: () -> Unit,
    navigateBack: () -> Unit,
    onToggleAll: () -> Unit,
    onToggleArtistChoice: (ArtistChoice) -> Unit,
) {
    SoulScreen {
        Column {
            SoulTopBar(
                title = strings.multipleArtistsTitle,
                rightAction = TopBarValidateAction(
                    onClick = onSaveSelection,
                ),
                leftAction = if (mode == MultipleArtistsChoiceMode.InitialFetch) {
                    null
                } else {
                    TopBarNavigationAction(onClick = navigateBack)
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
                        Text(
                            text = strings.multipleArtistsSelectionTitle,
                            color = SoulSearchingColorTheme.colorScheme.onPrimary,
                            style = UiConstants.Typography.bodyTitle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        SoulCheckBox(
                            checked = state.toggleAllState,
                            onCheckedChange = {
                                onToggleAll()
                            },
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
                            onToggleArtistChoice(artistChoice)
                        }
                    )
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
