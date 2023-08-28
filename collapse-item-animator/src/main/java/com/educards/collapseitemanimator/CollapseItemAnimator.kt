package com.educards.collapseitemanimator

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class CollapseItemAnimator : DefaultItemAnimator() {

    var animatorMap = HashMap<RecyclerView.ViewHolder, AnimatorInfo>()

    override fun recordPreLayoutInformation(
        state: RecyclerView.State,
        viewHolder: RecyclerView.ViewHolder,
        changeFlags: Int,
        payloads: MutableList<Any>
    ): ItemHolderInfo {
        return CollapseAnimItemHolderInfo().setFrom(viewHolder)
    }

    override fun recordPostLayoutInformation(
        state: RecyclerView.State,
        viewHolder: RecyclerView.ViewHolder
    ): ItemHolderInfo {
        return CollapseAnimItemHolderInfo().setFrom(viewHolder)
    }

    override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder) =
        if (viewHolder is CollapseAnimViewHolder
            && viewHolder.isCustomAnimated()
        ) {
            true
        } else {
            super.canReuseUpdatedViewHolder(viewHolder)
        }

    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder,
        newHolder: RecyclerView.ViewHolder,
        preInfo: ItemHolderInfo,
        postInfo: ItemHolderInfo
    ): Boolean {

        if (preInfo is CollapseAnimItemHolderInfo && postInfo is CollapseAnimItemHolderInfo) {

            if (postInfo.isCustomAnimated()) {
                val expanding = postInfo.isExpanded()

                // Data discrepancy fail-fast checks
                if (newHolder != oldHolder) {
                    error("A single holder instance expected for custom expand/collapse animation")
                }
                if (newHolder !is CollapseAnimViewHolder) {
                    error("A holder of type ${CollapseAnimViewHolder::class.simpleName} expected for custom expand/collapse animation")
                }

                val holder = oldHolder as CollapseAnimViewHolder
                val rootView = holder.rootView
                val rootViewAnimData = rootView.getCollapseAnimViewData()

                // record current (pre-reset) holder state
                val preResetTranslationY = rootView.translationY
                val preResetAnimBitmapPhase = if (rootViewAnimData.animBitmapPhase == CollapseAnimView.NOT_ANIMATING) {
                    if (expanding) 1f else 0f
                } else {
                    rootViewAnimData.animBitmapPhase
                }

                // reset animations
                val oldAnimatorInfo = animatorMap[oldHolder]
                oldAnimatorInfo?.animator?.cancel()

                // set new post-reset view state
                val deltaY = postInfo.top - preInfo.top - preResetTranslationY
                rootView.translationY = -deltaY
                rootViewAnimData.animBitmapPhase = preResetAnimBitmapPhase
                if (expanding) {
                    rootViewAnimData.animBitmap = postInfo.animBitmap ?: error("'animBitmap' expected")
                    rootViewAnimData.animBitmapCollapsedFirstLineY = postInfo.animBitmapCollapsedFirstLineY ?: error("'animBitmapCollapsedFirstLineY' expected")
                    rootViewAnimData.animBitmapCollapsedHeight = postInfo.animBitmapCollapsedHeight ?: error("'animBitmapCollapsedHeight' expected")
                } else {
                    rootViewAnimData.animBitmap = preInfo.animBitmap ?: error("'animBitmap' expected")
                    rootViewAnimData.animBitmapCollapsedFirstLineY = preInfo.animBitmapCollapsedFirstLineY ?: error("'animBitmapCollapsedFirstLineY' expected")
                    rootViewAnimData.animBitmapCollapsedHeight = preInfo.animBitmapCollapsedHeight ?: error("'animBitmapCollapsedHeight' expected")
                }
                rootView.invalidate() // redraw view with animBitmap which we've just set

                // prepare anim
                val anim = ValueAnimator.ofFloat(0f, 1f)
                anim.duration = changeDuration

            var scrolledY = 0
            val scrollOffsetFirstLineY =
                rootView.y + deltaY +
                        (if (expanding) rootViewAnimData.animBitmapCollapsedFirstLineY else 0f)

            val animUpdateListener = ValueAnimator.AnimatorUpdateListener {

                // translate Y
                rootView.translationY = -deltaY * (1f - it.animatedFraction)

                // render animBitmap
                rootViewAnimData.animBitmapPhase =
                    if (expanding) {
                        preResetAnimBitmapPhase - it.animatedFraction * preResetAnimBitmapPhase
                    } else {
                        preResetAnimBitmapPhase + it.animatedFraction * (1f - preResetAnimBitmapPhase)
                    }
                rootView.invalidate() // redraw animBitmap

                // scroll Y
                val scrollYDelta = ((it.animatedFraction * scrollOffsetFirstLineY) - scrolledY).toInt()
                recyclerView.scrollBy(0, scrollYDelta)
                scrolledY += scrollYDelta
            }

                val animListener = object : AnimatorListenerAdapter() {

                    override fun onAnimationStart(animator: Animator) {
                        dispatchChangeStarting(oldHolder, true)
                    }

                    override fun onAnimationEnd(animator: Animator) {

                        anim.removeListener(this)
                        anim.removeUpdateListener(animUpdateListener)

                        rootView.translationY = 0f
                        rootViewAnimData.animBitmapPhase = CollapseAnimView.NOT_ANIMATING
                        rootViewAnimData.animBitmapCollapsedFirstLineY = CollapseAnimView.NOT_ANIMATING
                        rootViewAnimData.animBitmapCollapsedHeight = CollapseAnimView.NOT_ANIMATING
                        rootViewAnimData.animBitmap = null
                        rootView.invalidate() // redraw view without animBitmap which we've just removed

                        dispatchChangeFinished(oldHolder, true)
                        animatorMap.remove(oldHolder)
                        if (animatorMap.isEmpty()) {
                            dispatchAnimationsFinished()
                        }
                    }

                }

                // track animation
                animatorMap[holder as RecyclerView.ViewHolder] = AnimatorInfo(anim)

                // start animation
                anim.addUpdateListener(animUpdateListener)
                anim.addListener(animListener)
                anim.start()

                return true
            }
        }

        return super.animateChange(oldHolder, newHolder, preInfo, postInfo)
    }

    override fun endAnimation(item: RecyclerView.ViewHolder) {
        super.endAnimation(item)
        animatorMap[item]?.animator?.cancel()
    }

    override fun endAnimations() {
        super.endAnimations()
        animatorMap.forEach { entry ->
            entry.value.animator.cancel()
        }
    }

    override fun isRunning(): Boolean {
        return super.isRunning() || animatorMap.isNotEmpty()
    }

    // TODO Get rid of this redundant wrapper.
    //      It was needed for composite animation when we wrapped
    //      here multiple ongoing sub-animations.
    data class AnimatorInfo(
        val animator: ValueAnimator
    )

}