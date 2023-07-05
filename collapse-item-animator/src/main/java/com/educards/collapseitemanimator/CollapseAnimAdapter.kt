package com.educards.collapseitemanimator

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

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

    val pendingAnimInfoMap: MutableMap<Int, Pair<AnimTargetState, CollapsedStateInfo>>

    /**
     * Propagates pending anim info further to [holder].
     */
    fun setPendingAnimInfo(holder: ViewHolder, position: Int) {
        if (holder is CollapseAnimViewHolder) {
            val pendingAnimInfo = pendingAnimInfoMap[position]
            holder.animTargetState = pendingAnimInfo?.first
            holder.collapsedStateInfo = pendingAnimInfo?.second
        }
    }

    fun setAnimInfo(animInfoList: List<AnimInfo>?) {
        resetAnimInfo()
        animInfoList?.forEach { animInfo ->
            // "Out-animation" setup
            setAnimInfoForCurrentHolder(animInfo)
            // "In-animation" setup
            setAnimInfoForPendingHolder(animInfo)
        }
    }

    fun resetAnimInfo() {
        pendingAnimInfoMap.clear()
    }

    fun setAnimInfoForCurrentHolder(animInfo: AnimInfo) {
        val currentViewHolder = findViewHolderForAdapterPosition(animInfo.animItemIndex)
        if (currentViewHolder is CollapseAnimViewHolder) {
            currentViewHolder.animTargetState = animInfo.animTargetState.getOpposite()
            currentViewHolder.collapsedStateInfo =
                if (animInfo.animTargetState == AnimTargetState.COLLAPSED) {
                    animInfo.collapsedStateInfo
                } else null
        }
    }

    fun setAnimInfoForPendingHolder(animInfo: AnimInfo) {
        pendingAnimInfoMap[animInfo.animItemIndex] = Pair(
            animInfo.animTargetState,
            animInfo.collapsedStateInfo
        )
    }

    fun findViewHolderForAdapterPosition(position: Int): ViewHolder?

}