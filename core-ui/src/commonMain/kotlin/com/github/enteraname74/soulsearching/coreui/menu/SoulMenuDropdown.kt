package com.github.enteraname74.soulsearching.coreui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulChoiceButton
import com.github.enteraname74.soulsearching.coreui.button.SoulChoiceButtonData
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun <T>SoulMenuDropdown(
    title: String,
    subTitle: String?,
    choices: List<SoulChoiceButtonData<T>>,
    selectedChoiceName: String,
    onChoice: (T) -> Unit,
    padding: PaddingValues = PaddingValues(UiConstants.Spacing.veryLarge),
    textColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary,
    subTextColor: Color = SoulSearchingColorTheme.colorScheme.subPrimaryText,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(padding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(end = UiConstants.Spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.large)
        ) {
            SoulMenuBody(
                title = title,
                text = subTitle,
                titleColor = textColor,
                textColor = subTextColor,
            )
        }
        SoulChoiceButton(
            choices = choices,
            currentChoiceName = selectedChoiceName,
            onClick = onChoice,
        )
    }
}