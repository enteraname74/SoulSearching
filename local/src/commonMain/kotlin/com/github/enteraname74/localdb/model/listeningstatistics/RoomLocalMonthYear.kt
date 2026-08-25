package com.github.enteraname74.localdb.model.listeningstatistics

import com.github.enteraname74.domain.model.LocalMonthYear

data class RoomLocalMonthYear(
    val month: Int,
    val year: Int,
) {
    fun toLocalMonthYear(): LocalMonthYear =
        LocalMonthYear(
            month = month,
            year = year,
        )
}

fun LocalMonthYear.toRoomLocalMonthYear(): RoomLocalMonthYear =
    RoomLocalMonthYear(
        month = month,
        year = year,
    )
