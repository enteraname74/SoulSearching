package com.github.enteraname74.soulsearching.feature.settings.cloud.user.code

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.github.enteraname74.soulsearching.domain.model.user.UserInscriptionCode
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.button.SoulFilledButton
import com.github.enteraname74.soulsearching.coreui.button.SoulIconButton
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_delete_filled
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.list.ShapeListStyle
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import com.github.enteraname74.soulsearching.util.rememberClipboardController
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec

@Composable
fun SettingsCloudCodesScreen(
    actions: SettingsCloudCodesActions,
    state: SettingsCloudCodeState,
) {
    SettingPage(
        navigateBack = actions::navigateBack,
        title = CoreRes.string.inscription_code_settings_title.toTextSpec(),
        verticalPadding = UiConstants.Spacing.small,
        contentPadding = PaddingValues(all = UiConstants.Spacing.large),
    ) {
        items(
            count = state.codes.size,
            key = { state.codes[it].code }
        ) { index ->
            val code = state.codes[index]
            CodeView(
                style = ShapeListStyle.fromList(
                    listSize = state.codes.size,
                    index = index,
                ),
                code = code,
                onDelete = {
                    actions.deleteCode(code)
                }
            )
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                SoulFilledButton(
                    modifier = Modifier
                        .padding(
                            top = UiConstants.Spacing.medium,
                        ),
                    text = CoreRes.string.generate_code_button.toTextSpec(),
                    onClick = actions::generateCode,
                )
            }
        }
    }
}

@Composable
private fun CodeView(
    code: UserInscriptionCode,
    style: ShapeListStyle,
    onDelete: () -> Unit,
) {
    val clipboard = rememberClipboardController()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(style.shape())
            .background(SoulSearchingColorTheme.colorScheme.surface)
            .clickableWithHandCursor { clipboard.copy(code.code.toString()) }
            .padding(all = UiConstants.Spacing.large),
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SoulText(
            modifier = Modifier
                .weight(1f),
            text = code.code.toString().toTextSpec(),
            style = UiConstants.Typography.bodyLarge,
            color = SoulSearchingColorTheme.colorScheme.text,
        )
        SoulIconButton(
            icon = CoreRes.drawable.ic_delete_filled,
            size = UiConstants.ImageSize.medium,
            onClick = onDelete,
            colors = SoulButtonDefaults.colors(
                containerColor = Color.Transparent,
            )
        )
    }
}
