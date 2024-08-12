package com.github.enteraname74.soulsearching.ext

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator

/**
 * Checks if we are not already on the same screen before navigating.
 */
fun Navigator.safePush(screen: Screen) {
    if (this.lastItem::class.java != screen::class.java) {
        this.push(screen)
    }
}