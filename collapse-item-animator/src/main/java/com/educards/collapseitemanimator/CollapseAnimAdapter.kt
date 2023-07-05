package com.educards.collapseitemanimator

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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
abstract class CollapseAnimAdapter(

    /**
     * A cyclic reference to [RecyclerView] which uses this `Adapter`.
     *
     * Adapter needs [RecyclerView] to access view holders based
     * on their adapter position (see [setAnimInfoForCurrentHolder]).
     */
    val recyclerView: RecyclerView

) : RecyclerView.Adapter<CollapseAnimAdapter.ViewHolder>() {

    init {
        setupStableIds()
    }

    private var pendingAnimInfoMap = mutableMapOf<Int, Pair<AnimTargetState, CollapsedStateInfo>>()

    open class ViewHolder(
        override val rootView: CollapseAnimFrameLayout,
        override val textView: TextView,
    ) : RecyclerView.ViewHolder(rootView),
        CollapseAnimViewHolder {
        override var animTargetState: AnimTargetState? = null
        override var collapsedStateInfo: CollapsedStateInfo? = null
    }

    protected fun setupStableIds() {
        setHasStableIds(true)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setPendingAnimInfo(holder, position)
    }

    /**
     * Propagates pending anim info further to [holder].
     */
    protected open fun setPendingAnimInfo(holder: ViewHolder, position: Int) {
        val pendingAnimInfo = pendingAnimInfoMap[position]
        holder.animTargetState = pendingAnimInfo?.first
        holder.collapsedStateInfo = pendingAnimInfo?.second
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    protected fun resetAnimInfo() {
        pendingAnimInfoMap.clear()
    }

    protected fun setAnimInfoForCurrentHolder(
        animItemIndex: Int,
        animTargetState: AnimTargetState,
        collapsedStateInfo: CollapsedStateInfo?
    ) {
        val currentViewHolder = recyclerView.findViewHolderForAdapterPosition(animItemIndex)
        if (currentViewHolder is CollapseAnimViewHolder) {
            currentViewHolder.animTargetState = animTargetState
            currentViewHolder.collapsedStateInfo = collapsedStateInfo
        }
    }

    protected fun setAnimInfoForPendingHolder(animInfo: AnimInfo) {
        pendingAnimInfoMap[animInfo.animItemIndex] = Pair(
            animInfo.animTargetState,
            animInfo.collapsedStateInfo
        )
    }

}