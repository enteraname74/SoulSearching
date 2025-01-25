package com.github.enteraname74.soulsearching.coreui.feedbackmanager

import com.github.enteraname74.domain.model.SoulResult

/**
 * Manages the feedbacks sent to the user after performing an action.
 */
interface FeedbackPopUpManager {
    /**
     * Shows a feedback notification to the user.
     */
    suspend fun showFeedback(feedback: String)

    suspend fun <T>showResultErrorIfAny(result: SoulResult<T>) =
        (result as? SoulResult.Error)?.error?.let { error ->
            showFeedback(
                feedback = error,
            )
        }
}