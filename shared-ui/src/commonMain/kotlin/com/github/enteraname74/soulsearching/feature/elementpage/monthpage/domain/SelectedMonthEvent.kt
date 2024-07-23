package com.github.enteraname74.soulsearching.feature.elementpage.monthpage.domain

import java.util.*

/**
 * Events of the selected month screen.
 */
sealed interface SelectedMonthEvent {
    data class SetSelectedMonth(val month: String): SelectedMonthEvent
    data class AddNbPlayed(val playlistId: UUID): SelectedMonthEvent
}