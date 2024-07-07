package com.github.soulsearching.domain.navigation

import androidx.compose.runtime.mutableStateListOf

/**
 * Used to navigate between multiples elements
 */
class NavigationController<T>(
    initialRoute: Route<T>
) {
    private val navigationStack = mutableStateListOf(initialRoute)

    val currentRoute: Route<T>
        get() = navigationStack.last()

    /**
     * Navigate to the previous screen.
     * Does nothing if no previous route existed.
     */
    fun navigateBack() {
        if (navigationStack.size <= 1) return
        navigationStack.removeLast()
    }

    /**
     * Navigate to a given route.
     * Does nothing if the given route is not found.
     */
    fun navigateTo(route: Route<T>) {
        navigationStack.add(route)
    }

    /**
     * Tries to retrieve an argument of the current route.
     */
    fun getArgument(key: String): Any? {
        return currentRoute.arguments[key]
    }
}