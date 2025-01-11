package com.github.enteraname74.soulsearching.feature.settings.cloud.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Text
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.animation.VerticalAnimatedVisibility
import com.github.enteraname74.soulsearching.coreui.button.SoulButton
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuExpand
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuSwitch
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.settings.cloud.state.SettingsCloudUploadState

@Composable
fun SettingsCloudUploadCard(
    uploadState: SettingsCloudUploadState,
    isEnable: Boolean,
    uploadSongs: () -> Unit,
    toggleSearchMetadata: () -> Unit,
) {
    var isExpanded: Boolean by rememberSaveable {
        mutableStateOf(false)
    }

    SoulMenuExpand(
        modifier = Modifier
            .alpha(if (isEnable) 1f else ContentAlpha.disabled),
        title = strings.cloudUploadTitle,
        subTitle = strings.cloudUploadText,
        clickAction = { isExpanded = !isExpanded },
        isExpanded = isEnable && isExpanded,
        clickEnabled = isEnable,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            SoulMenuSwitch(
                title = strings.cloudServerMetadataSearch,
                subTitle = null,
                toggleAction = toggleSearchMetadata,
                isChecked = uploadState.searchMetadata,
                padding = PaddingValues(
                    start = UiConstants.Spacing.large,
                    end = UiConstants.Spacing.large,
                    bottom = UiConstants.Spacing.small,
                ),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(UiConstants.Spacing.large),
                contentAlignment = Alignment.CenterEnd,
            ) {
                SoulButton(
                    enabled = uploadState !is SettingsCloudUploadState.Uploading,
                    onClick = uploadSongs,
                    colors = SoulButtonDefaults.primaryColors(),
                ) {
                    Text(
                        text = strings.upload,
                        textAlign = TextAlign.Center,
                        color = SoulSearchingColorTheme.colorScheme.onPrimary,
                        fontSize = 15.sp
                    )
                }
            }
            VerticalAnimatedVisibility(
                visible = uploadState is SettingsCloudUploadState.Uploading
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(UiConstants.Spacing.medium),
                    contentAlignment = Alignment.Center,
                ) {
                    (uploadState as? SettingsCloudUploadState.Uploading)?.let { uploadState ->
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            progress = { uploadState.progress },
                            color = SoulSearchingColorTheme.colorScheme.onPrimary,
                            trackColor = SoulSearchingColorTheme.colorScheme.primary,
                            drawStopIndicator = {},
                        )
                    }
                }
            }
            VerticalAnimatedVisibility(
                visible = uploadState is SettingsCloudUploadState.Error
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(UiConstants.Spacing.large),
                    contentAlignment = Alignment.Center,
                ) {
                    (uploadState as? SettingsCloudUploadState.Error)?.error?.let { errorText ->
                        Text(
                            text = errorText,
                            color = SoulSearchingColorTheme.colorScheme.onPrimary,
                            style = UiConstants.Typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}