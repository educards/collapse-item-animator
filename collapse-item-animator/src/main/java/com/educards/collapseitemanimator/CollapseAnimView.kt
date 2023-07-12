package com.educards.collapseitemanimator

import android.graphics.Bitmap
import android.graphics.Canvas

interface CollapseAnimView {

    /**
     * Collapse animation is performed by "clipping" and positioning the `Bitmap` of list item view
     * in its expanded state. If your list item have vertical paddings/margins set the use this attribute
     * to position the clipping window properly with respect to these paddings/margins.
     * Value of [collapseAnimClipOffsetY] should match the sum of all vertical paddings/margins.
     */
    var collapseAnimClipOffsetY: Float

    fun getCollapseAnimViewData(): CollapseAnimViewData

    fun invokeDefaultOnDraw(canvas: Canvas) =
        getCollapseAnimViewData().animBitmapPhase == NOT_ANIMATING

    fun invokeDefaultDispatchDraw(canvas: Canvas) =
        getCollapseAnimViewData().animBitmapPhase == NOT_ANIMATING

    fun onDrawCollapseAnim(canvas: Canvas) {

        val animData = getCollapseAnimViewData()

        val animBitmap = animData.animBitmap ?: error("Bitmap not set during animation [animBitmapPhase: ${animData.animBitmapPhase}]")

        val collapsedDeltaHeight = animBitmap.height - animData.animBitmapCollapsedHeight
        canvas.clipRect(0f,
            collapseAnimClipOffsetY, // TODO Weight with 'animBitmapPhase'?
            animBitmap.width.toFloat(),
            collapseAnimClipOffsetY + animBitmap.height.toFloat() - collapsedDeltaHeight * animData.animBitmapPhase)

        canvas.drawBitmap(
            animBitmap,
            0f,
            -animData.animBitmapCollapsedFirstLineY * animData.animBitmapPhase,
            null
        )
    }

    class CollapseAnimViewData {
        var animBitmapPhase = NOT_ANIMATING
        var animBitmapCollapsedFirstLineY = NOT_ANIMATING
        var animBitmapCollapsedHeight = NOT_ANIMATING
        var animBitmap: Bitmap? = null
    }

    companion object {
        const val NOT_ANIMATING = -1f
    }

}