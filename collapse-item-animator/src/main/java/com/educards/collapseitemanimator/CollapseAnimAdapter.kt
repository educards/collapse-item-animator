package com.educards.collapseitemanimator

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import java.util.SortedMap

/**
 * A base class for [RecyclerView.Adapter] supporting
 * the collapse animation.
 *
 * This class exists just for a convenience. It is perfectly
 * valid to implement the aspects of this class into any other
 * custom [RecyclerView.Adapter] if class inheritance is an option.
 *
 * The base point of this class is that:
 * * it provides view holder which already implements [CollapseAnimViewHolder]
 * * it provides [setPendingAnimInfo] methods to set animation metadata which
 *   are expected to be defined prior to launch of the animation
 *
 * @see setPendingAnimInfo
 */
interface CollapseAnimAdapter {

    val animInfoMap: SortedMap<Int, AnimInfo>

    var animTargetState: AnimTargetState?

    var previousItemCount: Int

    fun setPendingAnimInfo(holder: ViewHolder, positionAfterTransition: Int) {
        val pendingAnimInfo = animInfoMap[positionAfterTransition]
        setAnimInfo(holder, pendingAnimInfo?.animTargetState, pendingAnimInfo?.collapsedStateInfo)
    }

    /**
     * Invoked before the transition (animation)
     * is launched, but after animation metadata together with new data
     * are already set, to propagate anim info further to [holder].
     *
     * This is needed because in the current implementation
     * the view holder serves (in some sense) as a "DTO" to propagate
     * data between this `Adapter` and `Animator` (namely [CollapseItemAnimator]).
     *
     * This method needs to be invoked for both
     * pre & post transition view holders.
     *
     * @see setAnimInfoForCurrentHolder
     * @see setPendingAnimInfo
     */
    fun setAnimInfo(
        holder: ViewHolder,
        animTargetState: AnimTargetState?,
        collapsedStateInfo: CollapsedStateInfo?
    ) {
        if (holder is CollapseAnimViewHolder) {
            holder.animTargetState = animTargetState
            holder.collapsedStateInfo = collapsedStateInfo
        } else {
            error("Unexpected ViewHolder type [${holder::class.simpleName}]")
        }
    }

    fun setAnimInfo(animInfoList: List<AnimInfo>?) {
        animInfoMap.clear()
        animInfoList?.forEach { animInfo ->
            // "Out-animation" setup
            setAnimInfoForCurrentHolder(animInfo)
            // "In-animation" setup
            setAnimInfoForPendingHolder(animInfo)
        }
    }

    /**
     * Invoked before the transition (before animation is started)
     * to set anim info to current view holder.
     *
     * This is needed because in the current implementation
     * the view holder serves (in some sense) as a "DTO" to propagate
     * data between this `Adapter` and `Animator` (namely [CollapseItemAnimator]).
     */
    fun setAnimInfoForCurrentHolder(animInfo: AnimInfo) {
        val currentViewHolder = findViewHolderForAdapterPosition(animInfo.itemIndexBeforeTransition)
        if (currentViewHolder is CollapseAnimViewHolder) {
            currentViewHolder.animTargetState = animInfo.animTargetState.getOpposite()
            currentViewHolder.collapsedStateInfo =
                if (animInfo.animTargetState == AnimTargetState.COLLAPSED) {
                    animInfo.collapsedStateInfo
                } else null
        }
    }

    fun setAnimInfoForPendingHolder(animInfo: AnimInfo) {
        animInfoMap[animInfo.itemIndexAfterTransition] = animInfo
    }

    fun findViewHolderForAdapterPosition(position: Int): ViewHolder?

    fun getItemId(position: Int) =
        animTargetState?.let { animTargetState ->
            animInfoMap[position]?.itemIdAfterTransition
                ?: getItemId(animTargetState, position)
        } ?: error("'animTargetState' undefined")

    private fun getItemId(animTargetState: AnimTargetState, position: Int) =
        when (animTargetState) {
            AnimTargetState.EXPANDED -> {
                position.toLong()
            }
            AnimTargetState.COLLAPSED -> {
                Int.MAX_VALUE.toLong() + position
            }
        }

    fun notifyBeforeDataSet() {
        this as Adapter<*>
        previousItemCount = itemCount
    }

    fun notifyAfterDataSet() {
        this as Adapter<*>

        // We can't just call notifyDataSetChanged() here,
        // because the expand/collapse animation would not be triggered correctly.
        // Discussion: https://stackoverflow.com/a/76234207/915756

        // First take care of animated collapse/expand items.
        // We want to deal with them first, because they are transformed in a way which doesn't change
        // total items count (animated items are never removed or inserted, they can be only moved).
        // Because of this approach we can later deal with 'remove'/'insert' sequentially
        // by simply processing all the "gaps" between animated items.
        animInfoMap.forEach {
            val animInfo = it.value
            notifyItemChanged(animInfo.itemIndexBeforeTransition)
            if (animInfo.isItemMoved()) {
                notifyItemMoved(
                    animInfo.itemIndexBeforeTransition,
                    animInfo.itemIndexAfterTransition
                )
            }
        }

        // Now walk through the "gaps" between animated items.
        var previousEntry: Map.Entry<Int, AnimInfo>? = null
        animInfoMap.forEach { nextEntry ->
            val positionStart = (previousEntry?.key ?: -1) +1
            val itemCount = nextEntry.key - positionStart
            notifyItemRangeRemoved(positionStart, itemCount)
            notifyItemRangeInserted(positionStart, itemCount)
            previousEntry = nextEntry
        }
        // Process the "gap" from last animated item to the last item of the list.
        val positionStart = (previousEntry?.key ?: -1) +1
        if (positionStart < this.itemCount) {
            val itemCount = this.itemCount - positionStart
            notifyItemRangeRemoved(positionStart, itemCount)
            notifyItemRangeInserted(positionStart, itemCount)
        }

        if (previousItemCount > itemCount) {
            // Notify which tail items of the old list were removed
            notifyItemRangeRemoved(itemCount, previousItemCount - itemCount)
        } else if (itemCount > previousItemCount) {
            // Notify which tail items of the new list were inserted
            notifyItemRangeInserted(itemCount, itemCount - previousItemCount)
        }

    }

}