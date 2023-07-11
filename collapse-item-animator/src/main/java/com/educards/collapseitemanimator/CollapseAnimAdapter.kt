package com.educards.collapseitemanimator

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import java.lang.Integer.min
import java.util.SortedMap

/**
 * An interface implemented by [RecyclerView.Adapter] supporting the collapse animation.
 *
 * @see setItemAnimInfo
 * @see onBindPreTransitionItemAnimInfo
 * @see onBindPostTransitionItemAnimInfo
 * @see notifyBeforeDataSet
 * @see notifyAfterDataSet
 */
interface CollapseAnimAdapter {

    /**
     * Animation details of animated items.
     * * `key`: position of item after transition (post-transition phase)
     * * `value`: [ItemAnimInfo]
     * @see setItemAnimInfo
     */
    val itemAnimInfoMap: SortedMap<Int, ItemAnimInfo>

    /**
     * Current [ExpansionState] of the whole [Adapter] (of all its items).
     *
     * TODO This is a place for potential improvement.
     *      The current design of `CollapseAnimAdapter` supports only 2 states:
     *      * all items are expanded (few of them with expand animation)
     *      * all items are collapsed (few of them with collapse animation)
     *      See [README] for details.
     */
    var expansionState: ExpansionState?

    var previousItemCount: Int

    /**
     * Invoked from `onBindViewHolder` to setup view (wrapped by [holder])
     * to it's post-transition state.
     *
     * Note: `onBindViewHolder` is called by the framework prior to taking "snapshot"
     * of the post-transition state ([CollapseItemAnimator.recordPostLayoutInformation]).
     * The pre-transition setup of the view is done in [onBindPreTransitionItemAnimInfo], which
     * is invoked before taking "snapshot" of pre-transition view state ([CollapseItemAnimator.recordPreLayoutInformation]).
     */
    fun onBindPostTransitionItemAnimInfo(holder: ViewHolder, positionAfterTransition: Int) {
        val itemAnimInfo = itemAnimInfoMap[positionAfterTransition]
        onBindAnimInfo(holder, itemAnimInfo?.itemTargetExpansionState, itemAnimInfo?.animInfo)
    }

    /**
     * Propagates [AnimInfo] further to bound [holder].
     *
     * Invocation of this method for both pre- & post-transition view holders is required,
     * because in the current implementation of [CollapseItemAnimator]
     * the view holder serves (in some sense) as a "DTO" to propagate
     * data between `Adapter` and [CollapseItemAnimator].
     *
     * @see onBindPreTransitionItemAnimInfo
     * @see onBindPostTransitionItemAnimInfo
     */
    private fun onBindAnimInfo(
        holder: ViewHolder,
        viewExpansionState: ExpansionState?,
        collapsedStateAnimInfo: AnimInfo?
    ) {
        if (holder is CollapseAnimViewHolder) {
            holder.viewExpansionState = viewExpansionState
            holder.animInfo = collapsedStateAnimInfo
        } else {
            error("Unexpected ViewHolder type [${holder::class.simpleName}]")
        }
    }

    /**
     * Invoked immediately after new [Adapter]'s data are provided to setup animation.
     */
    fun setItemAnimInfo(itemAnimInfoList: List<ItemAnimInfo>?) {
        itemAnimInfoMap.clear()
        itemAnimInfoList?.forEach { itemAnimInfo ->
            // "pre-transition" setup
            onBindPreTransitionItemAnimInfo(itemAnimInfo)
            // "transition" & "post-transition" setup
            itemAnimInfoMap[itemAnimInfo.itemIndexAfterTransition] = itemAnimInfo
        }
    }

    /**
     * Invoked immediately after new [Adapter]'s data are provided
     * to setup view (associated with [itemAnimInfo]) to it's pre-transition state.
     *
     * Note:
     * This method is invoked prior to taking "snapshot" of the pre-transition
     * state of the associated [itemAnimInfo] view (before [CollapseItemAnimator.recordPostLayoutInformation]).
     * On the other hand, [onBindPostTransitionItemAnimInfo] is called by the framework to setup view
     * prior to taking "snapshot" of its post-transition state ([CollapseItemAnimator.recordPostLayoutInformation]).
     */
    private fun onBindPreTransitionItemAnimInfo(itemAnimInfo: ItemAnimInfo) {
        val currentViewHolder = findViewHolderForAdapterPosition(itemAnimInfo.itemIndexBeforeTransition)
        if (currentViewHolder is CollapseAnimViewHolder) {

            // This method is invoked in pre-animation phase, therefore
            // the current view's `ExpansionState` represents the initial
            // (and therefor opposite) state of the `itemTargetExpansionState`.
            val currentViewExpansionState = itemAnimInfo.itemTargetExpansionState.getOpposite()

            onBindAnimInfo(
                currentViewHolder,
                currentViewExpansionState,

                // Because both anim directions ...
                //    (collapsed -> expanded)
                //    (expanded  -> collapsed)
                // ... are animated by rendering bitmap of the expanded view,
                // we provide AnimInfo (which holds also bitmap rendering details)
                // only for expanded view (since this is the view being always animated).
                if (currentViewExpansionState == ExpansionState.EXPANDED) {
                    itemAnimInfo.animInfo
                } else null
            )
        }
    }

    fun findViewHolderForAdapterPosition(position: Int): ViewHolder?

    fun getItemId(position: Int) =
        expansionState?.let { animTargetState ->
            itemAnimInfoMap[position]?.itemId
                ?: getItemIdStaticItems(animTargetState, position)
        } ?: error("'animTargetState' undefined")

    /**
     * Returns ID of those items, which are not being
     * animated during collapse-expand transition.
     */
    fun getItemIdStaticItems(animTargetState: ExpansionState, position: Int) =
        when (animTargetState) {
            ExpansionState.EXPANDED -> {
                position.toLong()
            }
            ExpansionState.COLLAPSED -> {
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
        itemAnimInfoMap.forEach {
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
        var previousEntry: Map.Entry<Int, ItemAnimInfo>? = null
        itemAnimInfoMap.forEach { nextEntry ->
            val positionStart = (previousEntry?.key ?: -1) +1
            val itemCount = nextEntry.key - positionStart
            if (itemCount > 0) {
                notifyItemRangeRemoved(positionStart, itemCount)
                notifyItemRangeInserted(positionStart, itemCount)
            }
            previousEntry = nextEntry
        }
        // Process the "gap" from last animated item to the last item of the list.
        val positionStart = (previousEntry?.key ?: -1) +1
        val lastCommonIndex = min(previousItemCount, this.itemCount)
        if (positionStart < lastCommonIndex) {
            val itemCount = lastCommonIndex - positionStart
            notifyItemRangeRemoved(positionStart, itemCount)
            notifyItemRangeInserted(positionStart, itemCount)
        }

        if (previousItemCount > itemCount) {
            // Notify which tail items of the old list were removed
            notifyItemRangeRemoved(itemCount, previousItemCount - itemCount)
        } else if (itemCount > previousItemCount) {
            // Notify which tail items of the new list were inserted
            notifyItemRangeInserted(previousItemCount, itemCount - previousItemCount)
        }

    }

}