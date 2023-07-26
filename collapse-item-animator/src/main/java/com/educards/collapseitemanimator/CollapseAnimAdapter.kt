package com.educards.collapseitemanimator

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import java.util.SortedMap

/**
 * An interface implemented by [RecyclerView.Adapter] supporting the collapse animation.
 *
 * @see setItemAnimInfo
 * @see onBindPreTransitionItemAnimInfo
 * @see onBindPostTransitionItemAnimInfo
 * @see onPreData
 * @see notifyAfterDataSet
 */
interface CollapseAnimAdapter {

    /**
     * Animation details of animated items.
     * * `key`: target position of item after transition (post-transition phase)
     *          regardless of the transition direction (expanded <-> collapsed)
     * * `value`: [ItemAnimInfo]
     * @see setItemAnimInfo
     */
    val itemAnimInfo: SortedMap<Int, ItemAnimInfo>

    /**
     * Current [ExpansionState] of the whole [Adapter] (of all its items).
     *
     * TODO This is a place for potential improvement.
     *      The current design of `CollapseAnimAdapter` supports only 2 states:
     *      * all items are expanded (few of them with expand animation)
     *      * all items are collapsed (few of them with collapse animation)
     *      See [README] for details.
     */
    var dataExpansionState: ExpansionState?

    var previousItemCount: Int

    var streamingNotifyExecutor: StreamingNotifyExecutor

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
        val itemAnimInfo = itemAnimInfo[positionAfterTransition]
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
        itemAnimInfo.clear()
        itemAnimInfoList?.forEach { itemAnimInfo ->
            // "pre-transition" setup
            onBindPreTransitionItemAnimInfo(itemAnimInfo)
            // "transition" & "post-transition" setup
            this.itemAnimInfo[itemAnimInfo.itemIndexAfterTransition] = itemAnimInfo
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
        dataExpansionState?.let { expansionState ->
            itemAnimInfo[position]?.itemId
                ?: getItemIdStaticItems(expansionState, position)
        } ?: error("'expansionState' undefined, did you invoke 'onPreData'?")

    /**
     * Returns ID of those items, which are not being
     * animated during collapse-expand transition.
     */
    fun getItemIdStaticItems(expansionState: ExpansionState, position: Int) =
        when (expansionState) {
            ExpansionState.EXPANDED -> {
                position.toLong()
            }
            ExpansionState.COLLAPSED -> {
                Int.MAX_VALUE.toLong() + position
            }
        }

    fun onPreData(dataExpansionState: ExpansionState) {
        this as Adapter<*>
        this.previousItemCount = itemCount
        this.dataExpansionState = dataExpansionState
    }

    fun notifyAfterDataSet() {
        this as Adapter<*>
        checkAnimSetup()
        streamingNotifyExecutor.doNotify(this, itemAnimInfo, previousItemCount, itemCount)
    }

    /**
     * Checks animation metadata for misconfiguration (fail-fast).
     */
    private fun checkAnimSetup() {
        if (dataExpansionState == null) {
            error("Adapter's dataExpansionState is required to be set.")
        }
        itemAnimInfo.forEach { entry ->
            if (entry.value.itemTargetExpansionState != dataExpansionState) {
                error(
                    "ExpansionState of all Adapter items is by design " +
                            "required to be homogenous. See README file for details [" +
                            "adapter.expansionState: $dataExpansionState, " +
                            "animInfo.itemTargetExpansionState: ${entry.value.itemTargetExpansionState}, " +
                            "animInfo.itemIndexBeforeTransition: ${entry.value.itemIndexBeforeTransition}, " +
                            "animInfo.itemIndexAfterTransition: ${entry.value.itemIndexAfterTransition}]"
                )
            }
        }
    }

}