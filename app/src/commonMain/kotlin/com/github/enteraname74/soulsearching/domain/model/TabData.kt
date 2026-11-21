package com.github.enteraname74.soulsearching.domain.model

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec

data class TabData(
    val title: SoulTextSpec,
    val screen: @Composable () -> Unit,
)
