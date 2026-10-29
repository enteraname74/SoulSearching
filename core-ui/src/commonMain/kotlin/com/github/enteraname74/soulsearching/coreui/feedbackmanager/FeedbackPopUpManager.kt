package com.github.enteraname74.soulsearching.coreui.feedbackmanager

import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.an_error_occurred
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.domain.model.SoulResult

/**
 * Manages the feedbacks sent to the user after performing an action.
 */
interface FeedbackPopUpManager {
    /**
     * Shows a feedback notification to the user.
     */
    suspend fun showFeedback(feedback: SoulTextSpec)

    suspend fun showErrorIfAny(result: SoulResult<*>) {
        (result as? SoulResult.Error)?.let {
            showFeedback(feedback = result.error?.toTextSpec() ?: CoreRes.string.an_error_occurred.toTextSpec())
        }
    }
}
