package com.github.enteraname74.domain.model

enum class DataMode(val value: String) {
    Local("Local"),
    Cloud("Cloud");

    companion object {
        fun fromString(value: String): DataMode? =
            entries.find { it.value == value }
    }
}