package com.github.enteraname74.soulsearching.coreui.feedbackmanager

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FeedbackPopUpAndroidManager(private val context: Context): FeedbackPopUpManager {
    override suspend fun showFeedback(feedback: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(
                context,
                feedback,
                Toast.LENGTH_SHORT,
            ).show()
        }
    }
}