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
    fun onBindPostTransitionItemAnimInfo(holder: ViewHolder, positionPostTransition: Int) {
        val itemAnimInfo = itemAnimInfo[positionPostTransition]
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
            this.itemAnimInfo[itemAnimInfo.itemIndexPostTransition] = itemAnimInfo
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
        val currentViewHolder = findViewHolderForAdapterPosition(itemAnimInfo.itemIndexPreTransition)
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
                            "animInfo.itemIndexPreTransition: ${entry.value.itemIndexPreTransition}, " +
                            "animInfo.itemIndexPostTransition: ${entry.value.itemIndexPostTransition}]"
                )
            }
        }
    }

    override fun onPostAnim(
        recyclerView: RecyclerView,
        animator: CollapseItemAnimator,
        viewHolder: CollapseAnimViewHolder,
        animSequenceFinished: Boolean,
        animCancelled: Boolean
    ) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onPostAnim [" +
                "holder.hash: ${viewHolder.hashCode()}, " +
                "recyclerView.isComputingLayout: ${recyclerView.isComputingLayout}, " +
                "animSequenceFinished: $animSequenceFinished, " +
                "animCancelled: $animCancelled]")

        if (animSequenceFinished) {
            val itemAnimInfo = viewHolder.itemAnimInfo
            if (itemAnimInfo == null) {
                error("'animInfo' expended for ViewHolder which just finished collapsed/expand animation")
            }

            val postTransitionIndex = itemAnimInfo.itemIndexPostTransition

            // Animation for this item is done.
            // Now clear animation metadata, because they are scoped for
            // animation phase only. Without this we would risk that item
            // would be animated again after subsequent call to notifyItemChanged.
            // TODO Wrap this in some clearMetadata method?
            itemAnimInfoMap.remove(postTransitionIndex)
            viewHolder.viewExpansionState = null
            viewHolder.itemAnimInfo = null

            // TODO Describe all this mess around
            //      'suppressNextAnimCycle' and the need to
            //      call 'notifyItemChanged' (because we've just
            //      cleared the animation metadata, because animation
            //      has finished, and as a consequence item ID has changed
            //      (because it's been part of anim metadata),
            //      and we need to tell the framework about this ID change
            //      somehow without triggering another animation.
            //      So we make the framework know about this by combination of
            //      'suppressNextAnimCycle' and 'notifyItemChanged'.
            //      Another option could be to disable animations using
            //      animator.supportsChangeAnimations=false, but we've encountered
            //      some issues related to asynchronous nature of API (view.post{...})
            //      which got messy really fast.

            // TODO Is there a way to do this without this 'recyclerView.isComputingLayout' fork?
            if (recyclerView.isComputingLayout) {
                // Needs to be called in post to prevent:
                // java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling
                // By the end of animation the scroll might be still in progress.
                viewHolder.rootView.post {
                    this as Adapter<*>
                    viewHolder.suppressNextAnimCycle = true
                    notifyItemChanged(postTransitionIndex)
                }
            } else {
                this as Adapter<*>
                viewHolder.suppressNextAnimCycle = true
                notifyItemChanged(postTransitionIndex)
            }

        }

    }

    companion object {
        private const val TAG = "CollapseAnimAdapter"
    }

}