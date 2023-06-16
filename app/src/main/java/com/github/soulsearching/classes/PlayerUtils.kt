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
    }
}