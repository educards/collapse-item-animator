package com.educards.collapseitemanimator

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * [FrameLayout] used by items which support "collapse animation".
 *
 * This view is required to be the root view of animated item
 * and is responsible for proper rendering of the animation
 * (see [CollapseAnimView] interface).
 */
class CollapseAnimFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),
    CollapseAnimView {

    private val collapseAnimViewData = CollapseAnimView.CollapseAnimViewData()

    override var collapseAnimClipOffsetY = 0f

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CollapseAnimView)
        collapseAnimClipOffsetY = a.getDimension(R.styleable.CollapseAnimView_collapseAnimClipOffsetY, 0f)
        a.recycle()
    }

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
