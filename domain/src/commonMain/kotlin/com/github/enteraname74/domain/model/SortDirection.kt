package com.github.enteraname74.domain.model

/**
 * Possible values of sorts.
 */
enum class SortDirection(val value: Int) {
    ASC(0),
    DESC(1);

    companion object {
        val DEFAULT = ASC

        fun from(value: Int): SortDirection? =
            entries.firstOrNull { it.value == value }
    }
}