package com.github.enteraname74.soulsearching.feature.settings.cloud

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.animation.VerticalAnimatedVisibility
import com.github.enteraname74.soulsearching.coreui.button.SoulButton
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuExpandSwitch
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.tab.SoulTabHeader
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.domain.model.TabData
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.cloud.state.SettingsCloudFormState
import com.github.enteraname74.soulsearching.feature.settings.cloud.state.SettingsCloudState
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import kotlinx.coroutines.launch

class SettingsCloudScreen: Screen, SettingPage {

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<SettingsCloudViewModel>()
        val navigator = LocalNavigator.currentOrThrow

        val state: SettingsCloudState by screenModel.state.collectAsState()
        val logInFormState: SettingsCloudFormState by screenModel.logInFormState.collectAsState()
        val signInFormState: SettingsCloudFormState by screenModel.signInFormState.collectAsState()

        Screen(
            state = state,
            logInFormState = logInFormState,
            signInFormState = signInFormState,
            hostTextField = screenModel.hostTextField,
            navigateBack = {
                navigator.pop()
            },
            toggleCloudMode = screenModel::toggleCloudState,
            signIn = screenModel::signIn,
            logIn = screenModel::logIn,
        )
    }

    @Composable
    private fun FormTab(
        formState: SettingsCloudFormState,
        focusManager: FocusManager,
        validateButtonTitle: String,
        onValidate: () -> Unit,
    ) {
        when(formState) {
            is SettingsCloudFormState.Data -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            all = UiConstants.Spacing.mediumPlus
                        ),
                ) {
                    formState.textFields.forEach {
                        it.TextField(
                            modifier = Modifier,
                            focusManager = focusManager,
                        )
                    }
                    VerticalAnimatedVisibility(
                        visible = formState.error != null,
                    ) {
                        formState.error?.let { error ->
                            Text(
                                text = error,
                                color = SoulSearchingColorTheme.colorScheme.onSecondary,
                                style = UiConstants.Typography.body,
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = UiConstants.Spacing.medium),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        SoulButton(
                            onClick = onValidate,
                            colors = SoulButtonDefaults.primaryColors(),
                        ) {
                            Text(
                                text = validateButtonTitle,
                                textAlign = TextAlign.Center,
                                color = SoulSearchingColorTheme.colorScheme.onPrimary,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
            SettingsCloudFormState.Loading -> {
                /* no-op*/
            }
        }
    }

    @Composable
    private fun ExpandSwitchContent(
        logInFormState: SettingsCloudFormState,
        signInFormState: SettingsCloudFormState,
        hostTextField: SoulTextFieldHolder,
        signIn: () -> Unit,
        logIn: () -> Unit,
    ) {

        val coroutineScope = rememberCoroutineScope()
        val focusManager = LocalFocusManager.current

        val tabs: List<TabData> = listOf(
            TabData(
                title = strings.cloudLogIn,
                screen = {
                    FormTab(
                        formState = logInFormState,
                        validateButtonTitle = strings.cloudLogIn,
                        onValidate = logIn,
                        focusManager = focusManager,
                    )
                }
            ),
            TabData(
                title = strings.cloudSignIn,
                screen = {
                    FormTab(
                        formState = signInFormState,
                        validateButtonTitle = strings.cloudSignIn,
                        onValidate = signIn,
                        focusManager = focusManager,
                    )
                }
            )
        )

        val pagerState = rememberPagerState(
            pageCount = { tabs.size }
        )

        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = UiConstants.Spacing.mediumPlus)
                    .padding(bottom = UiConstants.Spacing.medium)
            ) {
                hostTextField.TextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    focusManager = focusManager,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, tabData ->
                    val isSelected = pagerState.currentPage == index

                    SoulTabHeader(
                        modifier = Modifier
                            .height(50.dp)
                            .weight(1f),
                        title = tabData.title,
                        contentColor = if (isSelected) {
                            SoulSearchingColorTheme.colorScheme.onSecondary
                        } else {
                            SoulSearchingColorTheme.colorScheme.subSecondaryText
                        },
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        isSelected = isSelected,
                    )
                }
            }
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
            ) {pagePosition ->
                tabs[pagePosition].screen()
            }
        }
    }

    @Composable
    private fun DataScreen(
        state: SettingsCloudState.Data,
        logInFormState: SettingsCloudFormState,
        signInFormState: SettingsCloudFormState,
        hostTextField: SoulTextFieldHolder,
        navigateBack: () -> Unit,
        toggleCloudMode: () -> Unit,
        signIn: () -> Unit,
        logIn: () -> Unit,
    ) {
        SettingPage(
            navigateBack = navigateBack,
            title = strings.cloudSettingsTitle,
        ) {
            item {
                SoulMenuExpandSwitch(
                    modifier = Modifier
                        .padding(
                            horizontal = UiConstants.Spacing.large,
                        ),
                    title = strings.activateCloudMode,
                    subTitle = null,
                    clickAction = toggleCloudMode,
                    isExpanded = state.isCloudActivated,
                ) {
                    ExpandSwitchContent(
                        logInFormState = logInFormState,
                        signInFormState = signInFormState,
                        hostTextField = hostTextField,
                        signIn = signIn,
                        logIn = logIn,
                    )
                }
            }
        }
    }

    @Composable
    private fun Screen(
        state: SettingsCloudState,
        logInFormState: SettingsCloudFormState,
        signInFormState: SettingsCloudFormState,
        hostTextField: SoulTextFieldHolder,
        navigateBack: () -> Unit,
        toggleCloudMode: () -> Unit,
        signIn: () -> Unit,
        logIn: () -> Unit,
    ) {

        when(state) {
            is SettingsCloudState.Data -> {
                DataScreen(
                    state = state,
                    logInFormState = logInFormState,
                    signInFormState = signInFormState,
                    hostTextField = hostTextField,
                    navigateBack = navigateBack,
                    toggleCloudMode = toggleCloudMode,
                    signIn = signIn,
                    logIn = logIn,
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