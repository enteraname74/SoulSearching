package com.github.soulsearching.di

import androidx.compose.runtime.Composable
import org.koin.compose.koinInject

/**
 * Inject an element.
 */
@Composable
inline fun <reified T>injectElement(): T {
    return koinInject<T>()
}