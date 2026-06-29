package com.github.enteraname74.soulsearching.coreui.feedbackmanager

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.soulsearching.coreui.strings.strings

/**
 * Manages the feedbacks sent to the user after performing an action.
 */
interface FeedbackPopUpManager {
    /**
     * Shows a feedback notification to the user.
     */
    suspend fun showFeedback(feedback: String)

    suspend fun showErrorIfAny(result: SoulResult<*>) {
        (result as? SoulResult.Error)?.let {
            showFeedback(feedback = result.error ?: strings.anErrorOccurred)
        }
    }
}