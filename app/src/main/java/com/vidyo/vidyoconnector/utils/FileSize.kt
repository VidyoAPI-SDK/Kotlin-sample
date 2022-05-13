package com.vidyo.vidyoconnector.utils

object FileSize {
    fun toString(value: Long): String {
        if (value <= 0) {
            return "0 Bytes"
        }

        val types = listOf("Bytes", "kB", "MB", "GB")

        var typeIndex = 0
        var typeValue = value
        while (typeValue > 1000 && typeIndex < types.size - 1) {
            typeIndex += 1
            typeValue /= 1000
        }

        return "$typeValue ${types[typeIndex]}"
    }
}
