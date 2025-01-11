package com.github.enteraname74.soulsearching.feature.settings.cloud

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.cloud.composable.SettingsCloudDataScreen
import com.github.enteraname74.soulsearching.feature.settings.cloud.state.SettingsCloudFormState
import com.github.enteraname74.soulsearching.feature.settings.cloud.state.SettingsCloudState
import com.github.enteraname74.soulsearching.feature.settings.cloud.state.SettingsCloudUploadState

class SettingsCloudScreen: Screen, SettingPage {

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<SettingsCloudViewModel>()
        val navigator = LocalNavigator.currentOrThrow

        val state: SettingsCloudState by screenModel.state.collectAsState()
        val uploadState: SettingsCloudUploadState by screenModel.uploadState.collectAsState()
        val logInFormState: SettingsCloudFormState by screenModel.logInFormState.collectAsState()
        val signInFormState: SettingsCloudFormState by screenModel.signInFormState.collectAsState()

        Screen(
            state = state,
            uploadState = uploadState,
            logInFormState = logInFormState,
            signInFormState = signInFormState,
            hostTextField = screenModel.hostTextField,
            navigateBack = {
                navigator.pop()
            },
            toggleCloudMode = screenModel::toggleCloudState,
            signIn = screenModel::signIn,
            logIn = screenModel::logIn,
            onLogOut = screenModel::logOut,
            uploadSongs = screenModel::uploadAllMusicToCloud,
            toggleSearchMetadata = screenModel::toggleSearchMetadata,
        )
    }

    @Composable
    private fun Screen(
        state: SettingsCloudState,
        uploadState: SettingsCloudUploadState,
        logInFormState: SettingsCloudFormState,
        signInFormState: SettingsCloudFormState,
        hostTextField: SoulTextFieldHolder,
        navigateBack: () -> Unit,
        toggleCloudMode: () -> Unit,
        signIn: () -> Unit,
        logIn: () -> Unit,
        onLogOut: () -> Unit,
        uploadSongs: () -> Unit,
        toggleSearchMetadata: () -> Unit,
    ) {

        when(state) {
            is SettingsCloudState.Data -> {
                SettingsCloudDataScreen(
                    state = state,
                    logInFormState = logInFormState,
                    signInFormState = signInFormState,
                    hostTextField = hostTextField,
                    navigateBack = navigateBack,
                    toggleCloudMode = toggleCloudMode,
                    signIn = signIn,
                    logIn = logIn,
                    onLogOut = onLogOut,
                    uploadState = uploadState,
                    uploadSongs = uploadSongs,
                    toggleSearchMetadata = toggleSearchMetadata,
                )
            }
            SettingsCloudState.Loading -> {
                SoulLoadingScreen(
                    navigateBack = navigateBack,
                    title = strings.cloudSettingsTitle,
                )
            }
        }
    }
}