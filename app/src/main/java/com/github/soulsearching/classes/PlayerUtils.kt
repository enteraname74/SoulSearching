package com.github.soulsearching.classes

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.viewModels.PlayerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class PlayerUtils {
    companion object {
        val playerViewModel = PlayerViewModel()

        fun convertDuration(duration: Int): String {
            val minutes: Float = duration.toFloat() / 1000 / 60
            val seconds: Float = duration.toFloat() / 1000 % 60

            val strMinutes: String = minutes.toString().split(".")[0]

            val strSeconds = if (seconds < 10.0) {
                "0" + seconds.toString().split(".")[0]
            } else {
                seconds.toString().split(".")[0]
            }

            return "$strMinutes:$strSeconds"
        }
    }
}