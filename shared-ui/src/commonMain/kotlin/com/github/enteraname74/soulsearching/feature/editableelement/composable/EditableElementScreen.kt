package com.github.enteraname74.soulsearching.feature.editableelement.composable

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey

abstract class EditableElementScreen(
    id: String,
): Screen {
    final override val key: ScreenKey = "EDIT-$id"
}