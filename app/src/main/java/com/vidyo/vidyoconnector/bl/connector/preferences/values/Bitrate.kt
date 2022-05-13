package com.vidyo.vidyoconnector.bl.connector.preferences.values

object Bitrate {
    private const val MIN_VALUE = 0L
    private val MAX_VALUE = UInt.MAX_VALUE.toLong()

    fun parse(text: String): Long {
        val value = text.toLongOrNull()
        if (value == null || value > MAX_VALUE) {
            return MAX_VALUE
        }
        if (value < MIN_VALUE) {
            return MIN_VALUE
        }
        return value
    }

    fun fromJniValue(jniValue: Int): Long {
        return jniValue.toUInt().toLong()
    }

    fun toJniValue(value: Long): Int {
        if (value > MAX_VALUE) {
            return MAX_VALUE.toInt()
        }
        if (value < MIN_VALUE) {
            return MIN_VALUE.toInt()
        }
        return value.toInt()
    }

    fun toString(value: Long): String {
        if (value <= 0) {
            return "0 kbps"
        }
        if (value >= MAX_VALUE) {
            return "Unlimited"
        }

        val types = listOf("bps", "kbps", "Mbps", "Gbps")

        var typeIndex = 0
        var typeValue = value
        while (typeValue > 1000 && typeIndex < types.size - 1) {
            typeIndex += 1
            typeValue /= 1000
        }

        return "$typeValue ${types[typeIndex]}"
    }
}
