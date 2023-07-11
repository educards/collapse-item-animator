package com.educards.collapseitemanimator

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemAnimator.ItemHolderInfo

/**
 * "Snapshot" of the view in one of its animation states.
 *
 * @see CollapseItemAnimator.recordPreLayoutInformation
 * @see CollapseItemAnimator.recordPostLayoutInformation
 */
class CollapseAnimItemHolderInfo : ItemHolderInfo() {

    var viewExpansionState: ExpansionState? = null

    var animBitmap: Bitmap? = null
    var animBitmapCollapsedFirstLineY: Float? = null
    var animBitmapCollapsedHeight: Float? = null

    fun isCustomAnimated() = viewExpansionState != null

    fun isExpanded() = viewExpansionState == ExpansionState.EXPANDED

    override fun setFrom(holder: RecyclerView.ViewHolder): ItemHolderInfo {
        if (holder is CollapseAnimViewHolder) {

            viewExpansionState = holder.viewExpansionState

            // Animation for both directions ...
            //    (expanded  -> collapsed)
            //    (collapsed -> expanded)
            // ... is performed from bitmap of expanded view.
            // Therefore the `animInfo` is provided only for the view in its expanded state.
            if (isExpanded()) {
                val animInfo = holder.animInfo
                if (animInfo == null) {
                    error("${AnimInfo::class.simpleName} expected")

                } else {

                    // temporarily switch off potential ongoing animation
                    val preRenderAnimPhase = holder.rootView.getCollapseAnimViewData().animBitmapPhase
                    setOngoingAnimationPhase(holder, CollapseAnimView.NOT_ANIMATING)

                    // render bitmap
                    animBitmap = renderToNewBitmap(holder)

                    // compute Y of the first line visible in collapsed state
                    val firstLineY = holder.textView.layout.getLineTop(animInfo.collapsedStateVisibleFirstLine).toFloat()
                    animBitmapCollapsedFirstLineY = firstLineY

                    // compute height of collapsed view
                    val lastLineY = holder.textView.layout.getLineBottom(
                        animInfo.collapsedStateVisibleFirstLine +
                                animInfo.collapsedStateVisibleLinesCount - 1
                    ).toFloat()
                    animBitmapCollapsedHeight = lastLineY - firstLineY

                    // continue temporarily switched off anim
                    setOngoingAnimationPhase(holder, preRenderAnimPhase)
                }
            }
        }
        return super.setFrom(holder)
    }

    private fun setOngoingAnimationPhase(holder: CollapseAnimViewHolder, animBitmapPhase: Float) {
        holder.rootView.getCollapseAnimViewData().animBitmapPhase = animBitmapPhase
    }

    private fun renderToNewBitmap(holder: RecyclerView.ViewHolder): Bitmap {
        val view = holder.itemView
        val width = holder.itemView.width
        val height = holder.itemView.height

        // Create a bitmap with the same dimensions as the view
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        // Create a canvas with the bitmap as the drawing target
        val canvas = Canvas(bitmap)
        // Draw the view onto the canvas
        view.draw(canvas)

        return bitmap
    }

}