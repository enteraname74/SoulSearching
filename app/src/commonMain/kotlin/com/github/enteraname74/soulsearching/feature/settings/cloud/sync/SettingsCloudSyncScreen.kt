package com.github.enteraname74.soulsearching.feature.settings.cloud.sync

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.enteraname74.domain.usecase.music.SyncDataWithCloudUseCase
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulFilledButton
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage

@Composable
fun SettingsCloudSyncScreen(
    actions: SettingsCloudSyncActions,
    state: SettingsCloudSyncState,
) {
    SettingPage(
        navigateBack = actions::navigateBack,
        title = strings.cloudSyncTitle,
        contentPadding = PaddingValues(
            all = UiConstants.Spacing.large,
        ),
        verticalPadding = UiConstants.Spacing.medium,
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                SoulFilledButton(
                    text = strings.cloudSyncButton,
                    onClick = actions::launchSync,
                    enabled = state.syncingState !is SyncDataWithCloudUseCase.State.WorkingState,
                )
            }
        }
        item {
            SyncingStateView(state = state.syncingState)
        }
    }
}

@Composable
private fun SyncingStateView(
    state: SyncDataWithCloudUseCase.State,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
    ) {
        Text(
            text = strings.cloudSyncNotificationTitle(state),
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
            style = UiConstants.Typography.bodyTitle
        )
        Text(
            text = strings.cloudSyncNotificationText(state),
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
            style = UiConstants.Typography.body
        )
        (state as? SyncDataWithCloudUseCase.State.ProgressState)?.progress?.let { progress ->

            val animatedProgress by animateFloatAsState(
                targetValue = progress,
                animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
            )

            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth(),
                progress = { animatedProgress },
                color = SoulSearchingColorTheme.colorScheme.onSecondary,
                trackColor = SoulSearchingColorTheme.colorScheme.secondary,
                drawStopIndicator = {},
            )
        }
    }
}
