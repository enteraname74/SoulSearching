package com.github.enteraname74.soulsearching

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpScaffold
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.loading.LoadingScaffold
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.application.AppWindow

@Composable
fun SoulSearchingApplication(
    loadingManager: LoadingManager = injectElement(),
    feedbackPopUpManager: FeedbackPopUpManager = injectElement(),
) {
    SoulSearchingAppTheme {
        FeedbackPopUpScaffold(
            feedbackPopUpManager = feedbackPopUpManager,
        ) {
            LoadingScaffold(
                loadingManager = loadingManager
            ) { isLoading ->
                AppWindow(
                    isLoadingManagerLoading = isLoading,
                )
            }
        }
    }
}
