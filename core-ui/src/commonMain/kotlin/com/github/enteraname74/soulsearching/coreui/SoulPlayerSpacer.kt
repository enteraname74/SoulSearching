package com.github.enteraname74.soulsearching.coreui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.ext.toDp
import com.github.enteraname74.soulsearching.coreui.utils.OptionalPaddingForPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.utils.PlayerMinimisedHeight

@Composable
fun SoulPlayerSpacer() {
    Spacer(
        modifier = Modifier
            .height(
                PlayerMinimisedHeight.toDp() + OptionalPaddingForPlayerSpacer
            )
    )
}
