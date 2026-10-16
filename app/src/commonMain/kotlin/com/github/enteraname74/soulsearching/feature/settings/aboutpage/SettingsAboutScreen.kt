package com.github.enteraname74.soulsearching.feature.settings.aboutpage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.composable.SoulChip
import com.github.enteraname74.soulsearching.coreui.composable.SoulChipColors
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_download
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_open_in_new
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_release_alert_filled
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuElement
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuExpand
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.text.SoulMarkdownText
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.domain.AppVersion
import com.github.enteraname74.soulsearching.domain.model.Release
import com.github.enteraname74.soulsearching.domain.model.SoulPlatform
import com.github.enteraname74.soulsearching.domain.util.SoulPlatformUtils
import com.github.enteraname74.soulsearching.ext.isNewerThanCurrentVersion
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage

private val CardMinWidth: Dp = 0.dp
private val CardMaxWidth: Dp = 500.dp

@Composable
fun SettingsAboutScreen(
    state: SettingsAboutState,
) {
    var isChangelogExpanded: Boolean by rememberSaveable {
        mutableStateOf(false)
    }
    val uriHandler = LocalUriHandler.current

    SettingPage(
        navigateBack = state.navigateBack,
        title = strings.aboutTitle,
    ) {
        if (SoulPlatformUtils.platform != SoulPlatform.Web && state.mostRecentRelease?.isNewerThanCurrentVersion() == true) {
            item {
                LatestReleaseSection(
                    modifier = Modifier
                        .animateItem(),
                    release = state.mostRecentRelease,
                    onDownloadLatestRelease = state.onDownloadLatestRelease
                )
            }
        }
        item {
            SoulMenuElement(
                title = strings.developersTitle,
                subTitle = strings.developersText,
                onClick = state.toDevelopers
            )
        }
        state.mostRecentRelease?.changelog?.let { changelog ->
            item {
                SoulMenuExpand(
                    title = strings.changelogTitle,
                    subTitle = strings.changelogText,
                    clickAction = { isChangelogExpanded = !isChangelogExpanded },
                    isExpanded = isChangelogExpanded,
                    textColor = SoulSearchingColorTheme.colorScheme.onPrimary,
                    subTextColor = SoulSearchingColorTheme.colorScheme.subPrimaryText,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                end = UiConstants.Spacing.veryLarge,
                                start = UiConstants.Spacing.veryLarge,
                            ),
                        verticalArrangement = Arrangement.spacedBy(
                            UiConstants.Spacing.medium,
                        )
                    ) {
                        state.mostRecentRelease.takeIf { it.isNewerThanCurrentVersion() }?.let {
                            ChangelogUpdateCard()
                        }
                        SoulMarkdownText(
                            text = changelog,
                        )
                    }
                }
            }
        }
        item {
            SoulMenuElement(
                title = strings.projectSiteTitle,
                subTitle = strings.projectSiteText,
                onClick = {
                    uriHandler.openUri(PROJECT_SITE)
                },
                trailIcon = CoreRes.drawable.ic_open_in_new,
            )
        }
        item {
            SoulMenuElement(
                title = strings.versionNameTitle,
                subTitle = AppVersion.versionName,
                onClick = state.onClickOnVersionName,
            )
        }
    }
}

@Composable
private fun LatestReleaseSection(
    release: Release,
    onDownloadLatestRelease: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium)
    ) {
        LatestReleaseCard(release)
        LatestReleaseCardOptions(release, onDownloadLatestRelease)
    }
}

@Composable
private fun LatestReleaseCard(
    release: Release,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = UiConstants.Spacing.large,
                start = UiConstants.Spacing.large,
                end = UiConstants.Spacing.large,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier
                .widthIn(
                    min = CardMinWidth,
                    max = CardMaxWidth,
                ),
            colors = CardDefaults.cardColors(
                contentColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                containerColor = SoulSearchingColorTheme.colorScheme.secondary
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(UiConstants.Spacing.large),
                verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SoulIcon(
                    icon = CoreRes.drawable.ic_release_alert_filled,
                    size = UiConstants.ImageSize.mediumPlus,
                    color = SoulSearchingColorTheme.colorScheme.onSecondary,
                )
                Text(
                    text = strings.newReleaseAvailableTitle,
                    color = SoulSearchingColorTheme.colorScheme.onSecondary,
                    textAlign = TextAlign.Center,
                    style = UiConstants.Typography.bodyTitle,
                )
                Text(
                    text = strings.newReleaseAvailableText(
                        releaseName = release.name,
                    ),
                    color = SoulSearchingColorTheme.colorScheme.onSecondary,
                    textAlign = TextAlign.Center,
                    style = UiConstants.Typography.body,
                )
            }
        }
    }
}

@Composable
private fun LatestReleaseCardOptions(
    release: Release,
    onDownloadLatestRelease: (() -> Unit)?,
) {
    val uriHandler = LocalUriHandler.current

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
        contentPadding = PaddingValues(horizontal = UiConstants.Spacing.large),
    ) {
        onDownloadLatestRelease?.let {
            item {
                SoulChip(
                    title = strings.downloadLatestReleaseTitle,
                    leadingIcon = CoreRes.drawable.ic_download,
                    onClick = onDownloadLatestRelease,
                    colors = SoulChipColors.default(),
                )
            }
        }
        item {
            SoulChip(
                title = strings.seeLatestReleaseTitle,
                leadingIcon = CoreRes.drawable.ic_open_in_new,
                onClick = {
                    uriHandler.openUri(release.githubUrl)
                }
            )
        }
    }
}

@Composable
private fun ChangelogUpdateCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            contentColor = SoulSearchingColorTheme.colorScheme.onSecondary,
            containerColor = SoulSearchingColorTheme.colorScheme.secondary
        )
    ) {
        Text(
            modifier = Modifier
                .padding(UiConstants.Spacing.large),
            text = strings.changelogUpdateRequired,
            color = SoulSearchingColorTheme.colorScheme.onSecondary,
            textAlign = TextAlign.Center,
            style = UiConstants.Typography.body,
        )
    }
}

private const val PROJECT_SITE = "https://github.com/enteraname74/SoulSearching"
