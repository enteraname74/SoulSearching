package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalFocusManager
import com.github.enteraname74.domain.model.CoverFolderRetriever
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.button.SoulCheckBox
import com.github.enteraname74.soulsearching.coreui.button.SoulSegmentedOptionButton
import com.github.enteraname74.soulsearching.coreui.button.SoulSegmentedOptionTextButton
import com.github.enteraname74.soulsearching.coreui.ext.chainIf
import com.github.enteraname74.soulsearching.coreui.ext.disableFocus
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.CoverFolderRetrieverActions

@Composable
fun CoverFolderRetrieverRules(
    actions: CoverFolderRetrieverActions,
    coverFolderRetriever: CoverFolderRetriever,
    title: String,
    whiteSpaceReplacementTextField: SoulTextFieldHolder,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = SoulSearchingColorTheme.colorScheme.secondary,
            contentColor = SoulSearchingColorTheme.colorScheme.onSecondary,
        )
    ) {
        Column(
            modifier = Modifier
                .padding(UiConstants.Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
        ) {
            Text(
                text = title,
                style = UiConstants.Typography.bodyTitle,
                color = SoulSearchingColorTheme.colorScheme.onPrimary,
            )
            WhiteSpaceRule(
                whiteSpaceRule = coverFolderRetriever.whiteSpaceRule,
                onToggleWhiteSpaceRule = actions::toggleWhiteSpace,
                textField = whiteSpaceReplacementTextField,
            )
            LowerCaseRule(
                lowerCaseRule = coverFolderRetriever.lowerCaseRule,
                onUpdateLowerCaseRule = actions::updateLowerCase,
            )
        }
    }
}

@Composable
private fun WhiteSpaceRule(
    whiteSpaceRule: CoverFolderRetriever.WhiteSpaceRule,
    onToggleWhiteSpaceRule: () -> Unit,
    textField: SoulTextFieldHolder,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
    ) {
        SoulCheckBox(
            checked = whiteSpaceRule.isActivated,
            onCheckedChange = { onToggleWhiteSpaceRule() },
        )
        Box(
            modifier = Modifier
                .chainIf(!whiteSpaceRule.isActivated) {
                    Modifier.disableFocus()
                }
        ) {
            textField.TextField(
                modifier = Modifier
                    .alpha(if (whiteSpaceRule.isActivated) 1f else ContentAlpha.disabled),
                focusManager = LocalFocusManager.current,
            )
        }
    }
}

@Composable
private fun LowerCaseRule(
    lowerCaseRule: Boolean?,
    onUpdateLowerCaseRule: (Boolean?) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        SoulSegmentedOptionButton(
            colors = SoulButtonDefaults.primaryColors(),
            buttons = listOf(
                SoulSegmentedOptionTextButton(
                    data = strings.coverFolderRetrieverFolderDynamicNameLowercase,
                    isSelected = lowerCaseRule == true,
                    contentPadding = SoulButtonDefaults.contentPadding(),
                    onClick = { onUpdateLowerCaseRule(true) }
                ),
                SoulSegmentedOptionTextButton(
                    data = strings.coverFolderRetrieverFolderDynamicNameNoTreatment,
                    isSelected = lowerCaseRule == null,
                    contentPadding = SoulButtonDefaults.contentPadding(),
                    onClick = { onUpdateLowerCaseRule(null) }
                ),
                SoulSegmentedOptionTextButton(
                    data = strings.coverFolderRetrieverFolderDynamicNameUppercase,
                    isSelected = lowerCaseRule == false,
                    contentPadding = SoulButtonDefaults.contentPadding(),
                    onClick = { onUpdateLowerCaseRule(false) }
                )
            )
        )
    }
}