package com.github.enteraname74.soulsearching.feature.search.composable

import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.button.SoulIconButton
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_cancel_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_keyboard_arrow_down
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_search
import com.github.enteraname74.soulsearching.coreui.ext.blend
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.keyboard.blockGlobalKeyboardShortcutsWhileFocused
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SoulSearchBar(
    searchText: String,
    placeholder: SoulTextSpec,
    updateTextMethod: (String) -> Unit,
    modifier: Modifier = Modifier,
    onClose: (() -> Unit)? = null,
    focusManager: FocusManager,
    focusRequester: FocusRequester,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        onClose?.let {
            SoulIconButton(
                icon = CoreRes.drawable.ic_keyboard_arrow_down,
                contentDescription = CoreRes.string.cancel.toTextSpec().string,
                onClick = onClose,
                colors = SoulButtonDefaults.colors(
                    contentColor = SoulSearchingColorTheme.colorScheme.text,
                    containerColor = Color.Transparent
                )
            )
        }

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .blockGlobalKeyboardShortcutsWhileFocused()
                .focusRequester(focusRequester),
            value = searchText,
            onValueChange = updateTextMethod,
            placeholder = {
                SoulText(
                    text = placeholder,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingIcon = {
                if (searchText.isNotBlank()) {
                    SoulIcon(
                        icon = CoreRes.drawable.ic_cancel_filled,
                        contentDescription = CoreRes.string.cancel.toTextSpec().string,
                        color = SoulSearchingColorTheme.colorScheme.text,
                        modifier = Modifier
                            .clickableWithHandCursor {
                                updateTextMethod("")
                            }
                    )
                } else {
                    SoulIcon(
                        icon = CoreRes.drawable.ic_search,
                        contentDescription = CoreRes.string.cancel.toTextSpec().string,
                        color = SoulSearchingColorTheme.colorScheme.text,
                    )
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(percent = 50),
            colors = with(SoulSearchingColorTheme.colorScheme) {
                TextFieldDefaults.colors(
                    unfocusedContainerColor = surface,
                    focusedTextColor = text,
                    cursorColor = text,
                    focusedIndicatorColor = Color.Transparent,
                    focusedLabelColor = text,
                    unfocusedTextColor = text,
                    focusedContainerColor = surface,
                    selectionColors = TextSelectionColors(
                        handleColor = text,
                        backgroundColor = background.blend(Color.Black, 0.2f),
                    ),
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedLabelColor = text,
                    focusedPlaceholderColor = text,
                    unfocusedPlaceholderColor = text
                )
            },
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            )
        )
    }
}
