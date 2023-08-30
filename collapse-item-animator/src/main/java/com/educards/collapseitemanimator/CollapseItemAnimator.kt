package com.educards.collapseitemanimator

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.util.Log
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class CollapseItemAnimator(
    private val recyclerView: RecyclerView,
    private val collapseAnimAdapter: CollapseAnimAdapter
) : DefaultItemAnimator() {

    interface AnimStateListener {

        /**
         * Invoked after collapse/expand animation ends.
         *
         * The animation may be part of "animation sequence":
         * a series of animations invoked sequentially over the same 'viewHolder'.
         * A typical scenario of "animation sequence" is when
         * a certain type of animation starts (let's say the "collapse") and before it is finished, the
         * user interrupts it and starts opposite animation ("expand").
         * In this specific case 2 invocations of [onPostAnim] will occur:
         * 1. Attributes of first [onPostAnim] invocation after "expand" anim is interrupted:
         *    * `animSequenceFinished == false`
         *    * `animCancelled == true`
         * 2. Attributes of subsequent [onPostAnim] invocation after "collapse" anim ends:
         *    * `animSequenceFinished == false`
         *    * `animCancelled == false`
         *
         * @param animSequenceFinished `true` if this is the last animation being performed
         *                             within current sequence of animations
         * @param animCancelled `false` if animation ended regularly or `true`
         *                      if animation ended as a result of cancellation
         */
        fun onPostAnim(
            recyclerView: RecyclerView,
            animator: CollapseItemAnimator,
            viewHolder: CollapseAnimViewHolder,
            animSequenceFinished: Boolean,
            animCancelled: Boolean,
        )

    }

    var animStateListeners = mutableListOf<AnimStateListener>()

    init {
        animStateListeners.add(collapseAnimAdapter)
    }

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

    override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
        if (viewHolder is CollapseAnimViewHolder) {

            if (viewHolder.isCustomAnimated()) {
                return true

            } else if (suppressAnimRequestedFor(viewHolder)) {
                // 'suppressNextAnimCycle' is set by animator client prior to invoking 'notifyItemChanged'
                // to tell this animator that it should suppress any animations on this view holder.
                //
                // First step to do that is to return 'true' here.
                //
                // By returning 'true' we ensure 2 things:
                //
                // 1. There are only 2 possible invocations which may now follow depending
                //    on the item ID:
                //    - animateAdd    (item ID differs across notification)
                //    - animateChange (item ID did not change, this happens when
                //                     animateChange is interrupted e.g. when anim such
                //                     as collapse->expand is in progress and user switches
                //                     this the course of this anim to expand->collapse)
                //    - animateRemove (3rd option) is suppressed by returning true
                //                     because we effectively denied the creation of new ViewHolder
                //                     which would be needed for subsequent invocation of
                //                     animateRemove and then animateChange
                //    So we are now sure (hopefully :)) that by returning true here
                //    we need to suppress animation and then reset 'suppressNextAnimCycle' only on
                //    two places: animateAdd(), animateChange().
                //
                // 2. The default implementation of animateChange() (super.animateChange())
                //    does not perform any animation if it doesn't have 2 different ViewHolder instances.
                //    The default super.animateChange() implementation performs fade-out (oldHolder) and then
                //    fadeIn (newHolder) only if it is provided with two ViewHolder instances.
                //    In other words, if we return true here, we are safe to call super.animateChange()
                //    and no animation will occur.
                //
                return true
            }
        }

        return super.canReuseUpdatedViewHolder(viewHolder)
    }

    private fun suppressAnimRequestedFor(viewHolder: RecyclerView.ViewHolder) =
        collapseAnimAdapter.suppressNextAnimCycleSet.contains(
            viewHolder.bindingAdapterPosition
        )

    private fun resetSuppressAnimFlag(viewHolder: RecyclerView.ViewHolder) {
        if (BuildConfig.DEBUG) Log.d(TAG, "resetSuppressAnimFlag [bindingAdapterPosition: ${viewHolder.bindingAdapterPosition}]")
        val removed = collapseAnimAdapter.suppressNextAnimCycleSet.remove(
            viewHolder.bindingAdapterPosition
        )
        assert(removed)
    }

    override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {

        // Did the client requested to suppress this animation?
        return if (holder is CollapseAnimViewHolder && suppressAnimRequestedFor(holder)) {
            if (BuildConfig.DEBUG) Log.d(TAG, "animateAdd [" +
                    "holder.bindingAdapterPosition: ${holder.bindingAdapterPosition}, " +
                    "holder.hashCode: ${holder.hashCode()}, " +
                    "animType: suppressNextAnimCycle]")

            dispatchAddStarting(holder)
            // suppress the animation by "pretending"
            // it is already done
            dispatchAddFinished(holder)
            if (animatorMap.isEmpty()) {
                dispatchAnimationsFinished()
            }

            // reset flag
            resetSuppressAnimFlag(holder)

            true

        } else {
            if (BuildConfig.DEBUG) Log.d(TAG, "animateAdd [" +
                    "holder.bindingAdapterPosition: ${holder?.bindingAdapterPosition}, " +
                    "holder.hashCode: ${holder.hashCode()}, " +
                    "animType: default]")
            super.animateAdd(holder)
        }
    }

    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder,
        newHolder: RecyclerView.ViewHolder,
        preInfo: ItemHolderInfo,
        postInfo: ItemHolderInfo
    ): Boolean {

        // Did the client request to suppress this particular animation?
        if (newHolder is CollapseAnimViewHolder && suppressAnimRequestedFor(newHolder)) {
            // If we are here, the state is following:
            // * newHolder == oldHolder (see canReuseUpdatedViewHolder())
            // * holder.suppressNextAnimCycle = true
            // * holder.isCustomAnimated() == false (client did already reset animation metadata
            //                                       prior to invocation of 'notifyItemChanged'
            //                                       which eventually triggered this method,
            //                                       see CollapseAnimAdapter.onPostAnim)
            // We just need to reset this flag.
            // Since holder.isCustomAnimated() == false, the default animateChange() implementation
            // is about to be invoked, but no animation will occur, because
            // super.animateChange() can implicitly animate only 2 ViewHolder instances,
            // but in our case newHolder == oldHolder.
            resetSuppressAnimFlag(newHolder)
            if (BuildConfig.DEBUG) Log.d(TAG, "animateChange [" +
                    "holder.bindingAdapterPosition: ${newHolder.bindingAdapterPosition}, " +
                    "animType: suppressNextAnimCycle]")
        }

        if (preInfo is CollapseAnimItemHolderInfo
            && postInfo is CollapseAnimItemHolderInfo
            && postInfo.isCustomAnimated())
        {

            val expanding = postInfo.isExpanded()
            if (BuildConfig.DEBUG) Log.d(TAG, "animateChange [animType: ${if (expanding) "expand" else "collapse"}, holder.bindingAdapterPosition: ${newHolder.bindingAdapterPosition}]")

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
            oldAnimatorInfo?.animator?.cancel(true)

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
            val anim = CollapseExpandValueAnimator()
            anim.setFloatValues(0f, 1f)
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
                    animator as CollapseExpandValueAnimator

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

                    animStateListeners.forEach { listener ->
                        listener.onPostAnim(
                            recyclerView,
                            this@CollapseItemAnimator,
                            newHolder,
                            animator.isLastChainAnimation(),
                            animator.cancelRequested
                        )
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

        } else {
            if (BuildConfig.DEBUG) Log.d(
                TAG, "animateChange [" +
                        "animType: default (super.animateChange()), " +
                        "holder.bindingAdapterPosition: ${newHolder.bindingAdapterPosition}]"
            )
            return super.animateChange(oldHolder, newHolder, preInfo, postInfo)
        }
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
    //      here multiple ongoing sub-animations which is no longer a case.
    data class AnimatorInfo(
        val animator: CollapseExpandValueAnimator
    )

    class CollapseExpandValueAnimator : ValueAnimator() {

        var cancelRequested = false

        /**
         * Flags the reason af anim cancellation.
         * Did the cancellation occur as part of animation sequence?
         *
         * @see AnimStateListener.onPostAnim
         */
        var cancelRequestedToStartSubsequentAnim = false

        fun cancel(cancelToStartNextAnim: Boolean) {
            this.cancelRequestedToStartSubsequentAnim = cancelToStartNextAnim
            this.cancelRequested = true
            super.cancel()
        }

        override fun cancel() {
            cancel(false)
        }

        fun isLastChainAnimation() =
            !cancelRequested || (/*cancelRequested && */!cancelRequestedToStartSubsequentAnim)

    }

    companion object {
        private const val TAG = "CollapseItemAnimator"
    }

}