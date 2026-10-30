package com.github.enteraname74.soulsearching.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope

/**
 * An [OverlayScene] that renders its [entry] as is, above the previous entries.
 * The entry draws its own container (dialog, swipeable view…); the scene only keeps
 * what's behind it on screen.
 */
internal class OverlaySceneImpl<T : Any>(
    override val key: Any,
    override val previousEntries: List<NavEntry<T>>,
    override val overlaidEntries: List<NavEntry<T>>,
    private val entry: NavEntry<T>,
) : OverlayScene<T> {
    override val entries: List<NavEntry<T>> = listOf(entry)

    override val content: @Composable (() -> Unit) = {
        entry.Content()
    }

    override fun equals(other: Any?): Boolean =
        other is OverlaySceneImpl<*> && key == other.key

    override fun hashCode(): Int = key.hashCode()
}

class OverlaySceneStrategy<T : Any> : SceneStrategy<T> {
    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        val lastEntry = entries.lastOrNull()
        if (lastEntry?.metadata?.get(OVERLAY_KEY) != true) return null
        return OverlaySceneImpl(
            key = lastEntry.contentKey,
            previousEntries = entries.dropLast(1),
            overlaidEntries = entries.dropLast(1),
            entry = lastEntry,
        )
    }

    companion object {
        fun overlay(): Map<String, Any> = mapOf(OVERLAY_KEY to true)

        private const val OVERLAY_KEY = "OVERLAY_KEY"
    }
}