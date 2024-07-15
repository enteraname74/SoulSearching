package com.github.enteraname74.soulsearching.coreui.feedbackmanager

/**
 * Manages the feedbacks sent to the user after performing an action.
 */
interface FeedbackPopUpManager {
    /**
     * Shows a feedback notification to the user.
     */
    suspend fun showFeedback(feedback: String)
}