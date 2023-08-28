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

    var itemAnimInfo: ItemAnimInfo? = null

    fun isCustomAnimated() = viewExpansionState != null

    fun isExpanded() = viewExpansionState == ExpansionState.EXPANDED

    override fun setFrom(holder: RecyclerView.ViewHolder): ItemHolderInfo {
        if (holder is CollapseAnimViewHolder) {

            viewExpansionState = holder.viewExpansionState

            if (isExpanded()) {

                // Animation for both directions ...
                //    (expanded  -> collapsed)
                //    (collapsed -> expanded)
                // ... is performed from bitmap of expanded view.
                // Therefore the `animInfo` is provided only for the view in its expanded state.
                val itemAnimInfo = holder.itemAnimInfo ?: error("${AnimInfo::class.simpleName} expected")
                this.itemAnimInfo = itemAnimInfo

                // temporarily "switch off" potential ongoing animation
                val preRenderAnimPhase = holder.rootView.getCollapseAnimViewData().animBitmapPhase
                setOngoingAnimationPhase(holder, CollapseAnimView.NOT_ANIMATING)

                // render bitmap
                animBitmap = renderToNewBitmap(holder)

                val layout = holder.textView.layout

                // compute Y of the first line visible in collapsed state
                val firstLineY =
                    (layout.getLineTop(itemAnimInfo.animInfo.collapsedStateVisibleFirstLine) + holder.textView.layout.topPadding).toFloat()
                animBitmapCollapsedFirstLineY = firstLineY

                // compute height of collapsed view
                val lastLineY = (layout.getLineBottom(
                    itemAnimInfo.animInfo.collapsedStateVisibleFirstLine +
                            itemAnimInfo.animInfo.collapsedStateVisibleLinesCount - 1
                ) + layout.bottomPadding).toFloat()
                animBitmapCollapsedHeight = lastLineY - firstLineY

                // restore temporarily "switched off" anim
                // (see few lines above, where the anim is "switched off")
                setOngoingAnimationPhase(holder, preRenderAnimPhase)
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