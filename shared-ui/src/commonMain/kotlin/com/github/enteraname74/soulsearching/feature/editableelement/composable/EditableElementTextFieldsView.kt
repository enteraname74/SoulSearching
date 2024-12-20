package com.github.enteraname74.soulsearching.feature.editableelement.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder

fun LazyListScope.editableElementTextFieldsView(
    focusManager: FocusManager,
    textFields: List<SoulTextFieldHolder>,
    extraContent: @Composable (() -> Unit)? = null,
) {
    items(textFields) {
        it.TextField(
            modifier = Modifier
                .animateItem(),
            focusManager = focusManager,
        )
    }
    extraContent?.let {
        item {
            Box(
                modifier = Modifier
                    .animateItem()
            ) {
                it()
            }
        }
    }

    item {
        SoulPlayerSpacer()
    }
}