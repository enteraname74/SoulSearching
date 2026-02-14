package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuBody
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun CoverFolderRetrieverExpander(
    isExpanded: Boolean,
    title: String,
    subTitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = SoulSearchingColorTheme.colorScheme.secondary,
            contentColor = SoulSearchingColorTheme.colorScheme.onSecondary,
        )
    ) {
        Column {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .clickableWithHandCursor { onClick() }
                    .padding(
                        all = UiConstants.Spacing.medium,
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.large)
                ) {
                    SoulMenuBody(
                        modifier = Modifier.fillMaxWidth(),
                        title = title,
                        text = subTitle,
                        titleColor = SoulSearchingColorTheme.colorScheme.onPrimary,
                        textColor = SoulSearchingColorTheme.colorScheme.subPrimaryText,
                    )
                }
                RadioButton(
                    selected = isExpanded,
                    onClick = onClick,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = SoulSearchingColorTheme.colorScheme.onPrimary,
                        unselectedColor = SoulSearchingColorTheme.colorScheme.onPrimary,
                    )
                )
            }
            AnimatedVisibility(
                enter = expandVertically(),
                exit = shrinkVertically(),
                visible = isExpanded
            ) {
                Box(
                    modifier = Modifier
                        .padding(
                            start = UiConstants.Spacing.medium,
                            end = UiConstants.Spacing.medium,
                            bottom = UiConstants.Spacing.medium,
                        )
                ) {
                    content()
                }
            }
        }
    }
}