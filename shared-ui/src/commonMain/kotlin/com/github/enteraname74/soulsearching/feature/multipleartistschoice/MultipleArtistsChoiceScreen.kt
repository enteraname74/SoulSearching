package com.github.enteraname74.soulsearching.feature.multipleartistschoice

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarValidateAction
import com.github.enteraname74.soulsearching.coreui.utils.LaunchInit
import com.github.enteraname74.soulsearching.ext.safePush
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.MainPageScreen
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.composable.MultipleArtistsChoiceItem
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.composable.MultipleArtistsWarningCard
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.state.ArtistChoice
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.state.MultipleArtistChoiceState
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.state.MultipleArtistsChoiceNavigationState
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.SettingsAddMusicsScreen

class MultipleArtistsChoiceScreen(
    private val multipleArtists: List<Artist>,
    private val mode: MultipleArtistsChoiceMode,
): Screen {
    @Composable
    override fun Content() {
        val screenModel: MultipleArtistsChoiceViewModel = koinScreenModel()
        val state: MultipleArtistChoiceState by screenModel.state.collectAsState()
        val navigationState: MultipleArtistsChoiceNavigationState by screenModel.navigationState.collectAsState()

        val navigator = LocalNavigator.currentOrThrow

        LaunchInit {
            screenModel.loadArtistsChoices(multipleArtists = multipleArtists)
        }

        LaunchedEffect(navigationState) {
            when(navigationState) {
                MultipleArtistsChoiceNavigationState.Idle -> {
                    /*no-op*/
                }
                MultipleArtistsChoiceNavigationState.Quit -> {
                    when (mode) {
                        MultipleArtistsChoiceMode.InitialFetch -> navigator.replaceAll(MainPageScreen())
                        MultipleArtistsChoiceMode.NewSongs -> navigator.safePush(
                            SettingsAddMusicsScreen(
                                shouldShowSaveScreen = true,
                            )
                        )
                        MultipleArtistsChoiceMode.GeneralCheck -> navigator.pop()
                    }
                    screenModel.consumeNavigation()
                }
            }
        }

        MainComposable(
            state = state,
            onToggleArtistChoice = screenModel::toggleSelection,
            onSaveSelection = screenModel::saveSelection,
            navigateBack = navigator::pop,
        )
    }

    @Composable
    private fun MainComposable(
        state: MultipleArtistChoiceState,
        onSaveSelection: () -> Unit,
        navigateBack: () -> Unit,
        onToggleArtistChoice: (ArtistChoice) -> Unit,
    ) {
        when (state) {
            MultipleArtistChoiceState.Loading -> {
                SoulLoadingScreen(text = null)
            }
            is MultipleArtistChoiceState.UserAction -> {
                UserActionScreen(
                    choices = state.artists,
                    onSaveSelection = onSaveSelection,
                    onToggleArtistChoice = onToggleArtistChoice,
                    navigateBack = navigateBack,
                )
            }
        }
    }

    @Composable
    private fun UserActionScreen(
        choices: List<ArtistChoice>,
        onSaveSelection: () -> Unit,
        navigateBack: () -> Unit,
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
                LazyColumn(
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
                        Text(
                            text = strings.multipleArtistsSelectionTitle,
                            color = SoulSearchingColorTheme.colorScheme.onPrimary,
                            style = UiConstants.Typography.bodyTitle,
                        )
                    }
                    items(
                        items = choices,
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
}

private const val WarningCardContentType: String = "WarningCardContentType"
private const val WarningCardContentKey: String = "WarningCardContentKey"
private const val SelectionTextContentType: String = "SelectionTextContentType"
private const val SelectionTextKey: String = "SelectionTextKey"
private const val ArtistChoicesContentType: String = "ArtistChoicesContentType"
