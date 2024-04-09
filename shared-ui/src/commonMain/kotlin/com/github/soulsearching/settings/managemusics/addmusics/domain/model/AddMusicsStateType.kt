package com.github.soulsearching.settings.managemusics.addmusics.domain.model

/**
 * Represent the possible operations for adding musics.
 */
enum class AddMusicsStateType {
    FETCHING_MUSICS,
    SAVING_MUSICS,
    WAITING_FOR_USER_ACTION
}