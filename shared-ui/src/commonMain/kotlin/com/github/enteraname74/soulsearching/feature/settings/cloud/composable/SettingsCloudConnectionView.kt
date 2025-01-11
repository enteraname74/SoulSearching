package com.github.enteraname74.soulsearching.feature.settings.cloud.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.tab.SoulTabHeader
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.domain.model.TabData
import com.github.enteraname74.soulsearching.feature.settings.cloud.state.SettingsCloudFormState
import kotlinx.coroutines.launch

@Composable
fun SettingsCloudConnectionView(
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
                SettingsCloudFormTab(
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
                SettingsCloudFormTab(
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
        ) { pagePosition ->
            tabs[pagePosition].screen()
        }
    }
}