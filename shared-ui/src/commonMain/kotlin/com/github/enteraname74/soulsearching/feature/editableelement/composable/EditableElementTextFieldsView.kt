package com.github.enteraname74.soulsearching.feature.editableelement.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder

fun LazyListScope.editableElementTextFieldsView(
    focusManager: FocusManager,
    textFields: List<SoulTextFieldHolder>,
    extraFormBottomContent: @Composable (() -> Unit)?,
    extraFormTopContent: @Composable (() -> Unit)?,
) {
    extraFormTopContent?.let {
        item {
            Box(
                modifier = Modifier
                    .animateItem()
            ) {
                it()
            }
        }
    }
    items(textFields) {
        it.TextField(
            modifier = Modifier
                .animateItem(),
            focusManager = focusManager,
        )
    }
    extraFormBottomContent?.let {
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