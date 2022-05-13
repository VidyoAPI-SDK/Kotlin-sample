package com.vidyo.vidyoconnector.bl.connector

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.forEach

class ConnectorLayout(context: Context) : ViewGroup(context) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val childWidthMS = MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY)
        val childHeightMS = MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY)
        forEach {
            it.measure(childWidthMS, childHeightMS)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        forEach {
            it.layout(0, 0, width, height)
        }
    }
}
