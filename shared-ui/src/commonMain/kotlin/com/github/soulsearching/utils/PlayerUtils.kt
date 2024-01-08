package com.github.soulsearching.utils

import com.github.soulsearching.viewmodel.PlayerViewModel

/**
 * Object containing the instance of the player view model.
 * It is static because we need to access this view model in multiple parts of the applications
 * like the service for example.
 */
object PlayerUtils {
    lateinit var playerViewModel: PlayerViewModel
}
