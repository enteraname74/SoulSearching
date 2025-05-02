package com.github.enteraname74.domain.model

/**
 * Possible types of sorts.
 */
enum class SortType(val value: Int) {
    NAME(0),
    ADDED_DATE(1),
    NB_PLAYED(2);

    companion object {
        val DEFAULT = NAME

        fun from(value: Int): SortType? =
            entries.firstOrNull { it.value == value }
    }
}