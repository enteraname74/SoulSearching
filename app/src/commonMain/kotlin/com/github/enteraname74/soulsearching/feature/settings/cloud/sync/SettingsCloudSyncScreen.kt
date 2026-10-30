package com.github.enteraname74.soulsearching.feature.settings.cloud.sync

import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.domain.usecase.music.SyncDataWithCloudUseCase
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulFilledButton
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.strings.SoulTexts

@Composable
fun SettingsCloudSyncScreen(
    state: SettingsCloudSyncState,
) {
    SettingPage(
        navigateBack = state.navigateBack,
        title = CoreRes.string.cloud_sync_title.toTextSpec(),
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
                    text = CoreRes.string.cloud_sync_button.toTextSpec(),
                    onClick = state.launchSync,
                    enabled = state.syncingState !is SyncDataWithCloudUseCase.State.WorkingState,
                )
            }
        }
        item {
            SyncingStateView(state = state.syncingState)
        }
        item {
            Column(
                modifier = Modifier
                    .padding(
                        top = UiConstants.Spacing.medium,
                    ),
                verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small),
            ) {
                SyncState(
                    modifier = Modifier
                        .padding(
                            top = UiConstants.Spacing.medium,
                        ),
                    title = CoreRes.string.cloud_settings_last_general_sync.toTextSpec(),
                    date = state.lastSync,
                )
                SyncState(
                    title = CoreRes.string.cloud_settings_last_settings_sync.toTextSpec(),
                    date = state.lastSettingsSync,
                )
                SyncState(
                    title = CoreRes.string.cloud_settings_last_statistics_sync.toTextSpec(),
                    date = state.lastStatisticsSync,
                )
            }
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
        SoulText(
            text = SoulTexts.cloudSyncNotificationTitle(state),
            color = SoulSearchingColorTheme.colorScheme.text,
            style = UiConstants.Typography.bodyTitle
        )
        SoulText(
            text = SoulTexts.cloudSyncNotificationText(state),
            color = SoulSearchingColorTheme.colorScheme.text,
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
                color = SoulSearchingColorTheme.colorScheme.text,
                trackColor = SoulSearchingColorTheme.colorScheme.surface,
                drawStopIndicator = {},
            )
        }
    }
}

@Composable
private fun SyncState(
    title: SoulTextSpec,
    date: SoulTextSpec,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        SoulText(
            text = title,
            color = SoulSearchingColorTheme.colorScheme.text,
            style = UiConstants.Typography.bodyTitle
        )
        SoulText(
            text = date,
            color = SoulSearchingColorTheme.colorScheme.text,
            style = UiConstants.Typography.body
        )
    }
}
