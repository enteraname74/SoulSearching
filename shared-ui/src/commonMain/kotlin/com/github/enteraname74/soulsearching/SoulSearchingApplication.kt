package com.github.enteraname74.soulsearching

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.CrossfadeTransition
import cafe.adriel.voyager.transitions.SlideTransition
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.soulsearching.composables.navigation.NavigationPanel
import com.github.enteraname74.soulsearching.composables.navigation.NavigationRowSpec
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpScaffold
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.loading.LoadingScaffold
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.ext.isComingFromPlaylistDetails
import com.github.enteraname74.soulsearching.ext.navigationIcon
import com.github.enteraname74.soulsearching.ext.navigationTitle
import com.github.enteraname74.soulsearching.ext.safePush
import com.github.enteraname74.soulsearching.feature.appinit.FetchingMusicsComposable
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.PagerScreen
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.ApplicationState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.MainPageNavigationState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.ApplicationViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.MainPageViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.MainPageScreen
import com.github.enteraname74.soulsearching.feature.migration.MigrationScreen
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.MultipleArtistsChoiceScreen
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.settings.presentation.SettingsScreen
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import kotlinx.coroutines.launch

@Composable
fun SoulSearchingApplication(
    loadingManager: LoadingManager = injectElement()
) {
    LoadingScaffold(
        loadingManager = loadingManager
    ) { isLoading ->
        Navigator(
            screen = SoulSearchingApplicationScreen(),
            onBackPressed = {
                !isLoading
            }
        ) { navigator ->
            SlideTransition(
                navigator = navigator,
                animationSpec = tween(UiConstants.AnimationDuration.normal)
            ) { screen ->
                screen.Content()
            }
        }
    }
}
