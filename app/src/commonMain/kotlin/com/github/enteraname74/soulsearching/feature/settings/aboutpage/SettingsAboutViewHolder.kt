package com.github.enteraname74.soulsearching.feature.settings.aboutpage

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.domain.usecase.release.CommonReleaseUseCase
import com.github.enteraname74.soulsearching.viewholder.SoulViewModelHolderV2
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec

class SettingsAboutViewHolder(
    private val commonReleaseUseCase: CommonReleaseUseCase,
    private val feedbackPopUpManager: FeedbackPopUpManager,
) : SoulViewModelHolderV2<SettingsAboutNavScope, SettingsAboutState>() {
    private var clickCount: Int = 0

    override fun getInitialState(): SettingsAboutState =
        SettingsAboutState(
            mostRecentRelease = null,
            navigateBack = { navigate { navigateBack() } },
            toDevelopers = { navigate { toDevelopers() } },
            onClickOnVersionName = ::onClickOnVersionName,
        )

    init {
        viewModelScope.launch {
            commonReleaseUseCase.getLatest().collectLatest { release ->
                updateState {
                    copy(
                        mostRecentRelease = release,
                    )
                }
            }
        }

        viewModelScope.launch {
            commonReleaseUseCase.fetchLatest()
            commonReleaseUseCase.getLatest().firstOrNull()?.let { release ->
                commonReleaseUseCase.markViewed(release.tag)
            }
        }
    }

    private fun onClickOnVersionName() {
        clickCount += 1
        if (clickCount == 10) {
            viewModelScope.launch {
                feedbackPopUpManager.showFeedback(
                    feedback = CoreRes.string.version_name_action_text.toTextSpec(),
                )
            }
            clickCount = 0
        }
    }

    @Composable
    override fun Content(state: SettingsAboutState) {
        SettingsAboutScreen(state)
    }
}
