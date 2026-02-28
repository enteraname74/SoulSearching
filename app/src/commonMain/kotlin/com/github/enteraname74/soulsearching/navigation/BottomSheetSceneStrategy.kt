package com.github.enteraname74.soulsearching.navigation


import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheetHandler
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheetHandlerDOS
import com.github.enteraname74.soulsearching.navigation.BottomSheetSceneStrategy.Companion.bottomSheet

/** An [OverlayScene] that renders an [entry] within a [ModalBottomSheet]. */
@OptIn(ExperimentalMaterial3Api::class)
internal class BottomSheetScene<T : Any>(
    override val key: T,
    override val previousEntries: List<NavEntry<T>>,
    override val overlaidEntries: List<NavEntry<T>>,
    private val entry: NavEntry<T>,
    private val onBack: () -> Unit,
) : OverlayScene<T> {

    override val entries: List<NavEntry<T>> = listOf(entry)

    override val content: @Composable (() -> Unit) = {
        SoulBottomSheetHandlerDOS(
            onClose = onBack,
        ) { closeWithAnim ->
            CompositionLocalProvider(
                LocalBottomSheetCloseWithAnimAction provides closeWithAnim
            ) {
                entry.Content()
            }
        }
    }
}

val LocalBottomSheetCloseWithAnimAction: ProvidableCompositionLocal<(()->Unit) -> Unit> = compositionLocalOf { {} }

/**
 * A [SceneStrategy] that displays entries that have added [bottomSheet] to their [NavEntry.metadata]
 * within a [ModalBottomSheet] instance.
 *
 * This strategy should always be added before any non-overlay scene strategies.
 */
@OptIn(ExperimentalMaterial3Api::class)
class BottomSheetSceneStrategy<T : Any> : SceneStrategy<T> {

    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        val lastEntry = entries.lastOrNull()
        val isBottomSheet = lastEntry?.metadata?.get(BOTTOM_SHEET_KEY) as? Boolean == true
        return if (isBottomSheet) {
            @Suppress("UNCHECKED_CAST")
            BottomSheetScene(
                key = lastEntry.contentKey as T,
                previousEntries = entries.dropLast(1),
                overlaidEntries = entries.dropLast(1),
                entry = lastEntry,
                onBack = onBack
            )
        } else {
            null
        }
    }

    companion object {
        fun bottomSheet(): Map<String, Any> = mapOf(BOTTOM_SHEET_KEY to true)

        internal const val BOTTOM_SHEET_KEY = "BOTTOM_SHEET_KEY"
    }
}