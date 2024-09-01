package com.github.enteraname74.soulsearching.feature.search.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Text
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
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize

@Composable
fun AppSearchBar(
    searchText: String,
    placeholder: String,
    updateTextMethod: (String) -> Unit,
    onClose: () -> Unit,
    focusManager: FocusManager,
    focusRequester: FocusRequester,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {

        val windowSize = rememberWindowSize()

        if (windowSize != WindowSize.Small) {
            SoulIconButton(
                icon = Icons.Rounded.Cancel,
                contentDescription = strings.cancel,
                onClick = onClose,
                colors = SoulButtonDefaults.colors(
                    contentColor = SoulSearchingColorTheme.colorScheme.onPrimary,
                    containerColor = Color.Transparent
                )
            )
        }

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = searchText,
            onValueChange = updateTextMethod,
            placeholder = {
                Text(
                    text = placeholder,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingIcon = {
                if (searchText.isNotBlank()) {
                    SoulIcon(
                        icon = Icons.Rounded.Cancel,
                        contentDescription = strings.cancel,
                        tint = SoulSearchingColorTheme.colorScheme.onSecondary,
                        modifier = Modifier
                            .clickable {
                                updateTextMethod("")
                            }
                    )
                } else {
                    SoulIcon(
                        icon = Icons.Rounded.Search,
                        contentDescription = strings.cancel,
                        tint = SoulSearchingColorTheme.colorScheme.onSecondary,
                    )
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(percent = 50),
            colors = with(SoulSearchingColorTheme.colorScheme) {
                TextFieldDefaults.colors(
                    unfocusedContainerColor = secondary,
                    focusedTextColor = onSecondary,
                    cursorColor = onSecondary,
                    focusedIndicatorColor = Color.Transparent,
                    focusedLabelColor = onSecondary,
                    unfocusedTextColor = onSecondary,
                    focusedContainerColor = secondary,
                    selectionColors = TextSelectionColors(
                        handleColor = onSecondary,
                        backgroundColor = secondary
                    ),
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedLabelColor = onSecondary,
                    focusedPlaceholderColor = onSecondary,
                    unfocusedPlaceholderColor = onSecondary
                )
            },
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            )
        )
    }
}