package com.github.enteraname74.soulsearching.feature.multipleartistschoice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarValidateAction
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.MainPageScreen
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.composable.MultipleArtistsChoiceItem
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.composable.MultipleArtistsWarningCard
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.state.ArtistChoice
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.state.MultipleArtistChoiceState
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.state.MultipleArtistsChoiceNavigationState

class MultipleArtistsChoiceScreen: Screen {
    @Composable
    override fun Content() {
        val screenModel: MultipleArtistsChoiceViewModel = koinScreenModel()
        val state: MultipleArtistChoiceState by screenModel.state.collectAsState()
        val navigationState: MultipleArtistsChoiceNavigationState by screenModel.navigationState.collectAsState()

        val navigator = LocalNavigator.currentOrThrow

        var hasFetchArtists: Boolean by rememberSaveable {
            mutableStateOf(false)
        }

        LaunchedEffect(hasFetchArtists) {
            if (!hasFetchArtists) {
                screenModel.loadArtistsChoices()
                hasFetchArtists = true
            }
        }

        LaunchedEffect(navigationState) {
            when(navigationState) {
                MultipleArtistsChoiceNavigationState.Idle -> {
                    /*no-op*/
                }
                MultipleArtistsChoiceNavigationState.ToMainScreen -> {
                    navigator.replaceAll(MainPageScreen())
                    screenModel.consumeNavigation()
                }
            }
        }

        MainComposable(
            state = state,
            onToggleArtistChoice = screenModel::toggleSelection,
            onSaveSelection = screenModel::saveSelection,
        )
    }

    @Composable
    private fun MainComposable(
        state: MultipleArtistChoiceState,
        onSaveSelection: () -> Unit,
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
                )
            }
        }
    }

    @Composable
    private fun UserActionScreen(
        choices: List<ArtistChoice>,
        onSaveSelection: () -> Unit,
        onToggleArtistChoice: (ArtistChoice) -> Unit,
    ) {
        SoulScreen {
            Column {
                SoulTopBar(
                    title = strings.multipleArtistsTitle,
                    rightAction = TopBarValidateAction(
                        onClick = onSaveSelection,
                    ),
                    leftAction = null,
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
