package com.github.enteraname74.soulsearching.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/**
 * Handles navigation events.
 */
class Navigator(private val backStack: NavBackStack<NavKey>) {
    val currentRoute: NavKey?
        get() = backStack.lastOrNull()

    val stack: List<NavKey> = backStack

    data class NavigationParameters(
        var clearBackStack: Boolean,
        var popUpTo: NavKey?,
    ) {
        companion object {
            fun default(): NavigationParameters =
                NavigationParameters(
                    clearBackStack = false,
                    popUpTo = null,
                )
        }
    }

    fun navigate(
        route: NavKey,
        parametersBlock: NavigationParameters.() -> Unit = { NavigationParameters.default() },
    ) {
        val params = NavigationParameters.default().apply {
            parametersBlock(this)
        }

        if (params.clearBackStack) {
            backStack.clear()
        }

        params.popUpTo?.let { destination ->
            backStack.removeLastWhile { it != destination }
        }

        // We don't want to navigate to the same route as the current one
        if (currentRoute == route) return
        backStack.add(route)
    }

    fun goBack(to: NavKey? = null) {
        // Ensure that we will not have an empty backstack -> leads to crashes
        if (stack.size == 1) return

        if (to != null) {
            backStack.removeLastWhile { it != to }
        } else {
            backStack.removeLastOrNull()
        }
    }
}

private fun NavBackStack<NavKey>.removeLastWhile(
    predicate: (NavKey) -> Boolean,
) {
    if (isEmpty()) return

    indices.reversed().forEach {
        val navKey = get(it)

        if (predicate(navKey)) {
            remove(navKey)
        } else {
            return
        }
    }
}