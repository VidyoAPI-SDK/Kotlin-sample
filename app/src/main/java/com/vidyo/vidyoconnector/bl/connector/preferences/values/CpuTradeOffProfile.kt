package com.vidyo.vidyoconnector.bl.connector.preferences.values

import androidx.annotation.StringRes
import com.vidyo.VidyoClient.Connector.Connector
import com.vidyo.vidyoconnector.R

enum class CpuTradeOffProfile(
    @StringRes val textId: Int,
    val jniValue: Connector.ConnectorTradeOffProfile,
) {
    Low(
        textId = R.string.CpuTradeOffProfile_Low,
        jniValue = Connector.ConnectorTradeOffProfile.VIDYO_CONNECTORTRADEOFFPROFILE_Low,
    ),
    Medium(
        textId = R.string.CpuTradeOffProfile_Medium,
        jniValue = Connector.ConnectorTradeOffProfile.VIDYO_CONNECTORTRADEOFFPROFILE_Medium,
    ),
    High(
        textId = R.string.CpuTradeOffProfile_High,
        jniValue = Connector.ConnectorTradeOffProfile.VIDYO_CONNECTORTRADEOFFPROFILE_High,
    );

    companion object {
        inline fun fromOrdinal(
            ordinal: Int,
            fallback: () -> CpuTradeOffProfile = { Medium },
        ): CpuTradeOffProfile {
            return values().find { it.ordinal == ordinal } ?: fallback()
        }

        inline fun fromJniValue(
            value: Connector.ConnectorTradeOffProfile,
            fallback: () -> CpuTradeOffProfile = { Medium },
        ): CpuTradeOffProfile {
            return values().find { it.jniValue == value } ?: fallback()
        }
    }
}
