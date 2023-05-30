package com.educards.collapseitemanimator

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.FrameLayout

class CollapseAnimFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), CollapseAnimView {

    private val collapseAnimViewData = CollapseAnimView.CollapseAnimViewData()

    override fun getCollapseAnimViewData(): CollapseAnimView.CollapseAnimViewData {
        return collapseAnimViewData
    }

    override fun onDraw(canvas: Canvas) {
        if (invokeDefaultOnDraw(canvas)) {
            super.onDraw(canvas)
        } else {
            onDrawCollapseAnim(canvas)
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (invokeDefaultDispatchDraw(canvas)) {
            super.dispatchDraw(canvas)
        }
    }

}
