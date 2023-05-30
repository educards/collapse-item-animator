package com.educards.collapseitemanimator

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemAnimator.ItemHolderInfo

class CollapseAnimHolderInfo : ItemHolderInfo() {

    var animInfo: CollapseAnimAdapter.CollapseAnimInfo? = null

    var animBitmap: Bitmap? = null
    var animBitmapCollapsedFirstLineY: Float? = null
    var animBitmapCollapsedHeight: Float? = null

    fun isExpanded() = animInfoIfExpanded() != null

    fun animInfoIfExpanded() =
        animInfo?.let {
            if (it.expanded) it else null
        }

    override fun setFrom(holder: RecyclerView.ViewHolder): ItemHolderInfo {
        if (holder is CollapseAnimAdapter.CollapseAnimViewHolder) {

            animInfo = holder.animInfo

            animInfoIfExpanded()?.let {

                // tmp switch off potential ongoing animation
                val preRenderAnimPhase = holder.rootView.getCollapseAnimViewData().animBitmapPhase
                holder.rootView.getCollapseAnimViewData().animBitmapPhase = CollapseAnimView.NOT_ANIMATING

                animBitmap = renderToNewBitmap(holder)
                val firstLineY = holder.textView.layout.getLineTop(it.firstLineIndex).toFloat()
                animBitmapCollapsedFirstLineY = firstLineY
                val lastLineY = holder.textView.layout.getLineBottom(it.firstLineIndex + it.linesCount - 1).toFloat()
                animBitmapCollapsedHeight = lastLineY - firstLineY

                // continue tmp switched of anim
                holder.rootView.getCollapseAnimViewData().animBitmapPhase = preRenderAnimPhase
            }
        }
        return super.setFrom(holder)
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