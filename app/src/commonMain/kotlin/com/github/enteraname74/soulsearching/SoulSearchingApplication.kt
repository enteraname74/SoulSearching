package com.github.enteraname74.soulsearching

import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.CachePolicy
import coil3.util.DebugLogger
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpScaffold
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.loading.LoadingScaffold
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.application.ApplicationRoute

@Composable
fun SoulSearchingApplication(
    loadingManager: LoadingManager = injectElement(),
    feedbackPopUpManager: FeedbackPopUpManager = injectElement(),
) {
    setSingletonImageLoaderFactory {
        ImageLoader(it)
            .newBuilder()
            .networkCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .logger(DebugLogger())
            .build()
    }

    SoulSearchingAppTheme {
        FeedbackPopUpScaffold(
            feedbackPopUpManager = feedbackPopUpManager,
        ) {
            LoadingScaffold(
                loadingManager = loadingManager
            ) {
                ApplicationRoute()
            }
        }
    }
}
