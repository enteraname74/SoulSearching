package com.github.enteraname74.soulsearching.feature.application

import com.github.enteraname74.soulsearching.composables.navigation.NavigationRowSpec

data class MainAppState(
    val navigationRows: List<NavigationRowSpec> = emptyList(),
)
