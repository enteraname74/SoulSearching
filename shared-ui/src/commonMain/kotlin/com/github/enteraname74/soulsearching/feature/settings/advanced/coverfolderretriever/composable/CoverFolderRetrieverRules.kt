package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import com.github.enteraname74.domain.model.CoverFolderRetriever
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.button.SoulCheckBox
import com.github.enteraname74.soulsearching.coreui.button.SoulSegmentedOptionButton
import com.github.enteraname74.soulsearching.coreui.button.SoulSegmentedOptionTextButton
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextField
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldStyle
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.CoverFolderRetrieverActions

@Composable
fun CoverFolderRetrieverRules(
    actions: CoverFolderRetrieverActions,
    coverFolderRetriever: CoverFolderRetriever,
    title: String,
) {
    Column {
        Text(
            text = title,
            style = UiConstants.Typography.bodyTitle,
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
        )
        WhiteSpaceRule(
            whiteSpaceRule = coverFolderRetriever.whiteSpaceRule,
            onUpdateReplacement = actions::updateWhiteSpaceReplacement,
            onToggleWhiteSpaceRule = actions::toggleWhiteSpace,
        )
        LowerCaseRule(
            lowerCaseRule = coverFolderRetriever.lowerCaseRule,
            onUpdateLowerCaseRule = actions::updateLowerCase,
        )
    }
}

@Composable
private fun WhiteSpaceRule(
    whiteSpaceRule: CoverFolderRetriever.WhiteSpaceRule,
    onUpdateReplacement: (String) -> Unit,
    onToggleWhiteSpaceRule: () -> Unit,
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
        SoulTextField(
            value = whiteSpaceRule.replacement,
            onValueChange = onUpdateReplacement,
            error = null,
            isInError = false,
            labelName = null,
            focusManager = LocalFocusManager.current,
            style = SoulTextFieldStyle.Unique,
        )
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