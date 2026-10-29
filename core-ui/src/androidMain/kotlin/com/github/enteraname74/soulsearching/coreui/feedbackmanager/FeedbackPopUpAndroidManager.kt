package com.github.enteraname74.soulsearching.coreui.feedbackmanager

import android.content.Context
import android.widget.Toast
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FeedbackPopUpAndroidManager(private val context: Context): FeedbackPopUpManager {
    override suspend fun showFeedback(feedback: SoulTextSpec) {
        val text = feedback.suspendedString()
        withContext(Dispatchers.Main) {
            Toast.makeText(
                context,
                text,
                Toast.LENGTH_SHORT,
            ).show()
        }
    }
}
