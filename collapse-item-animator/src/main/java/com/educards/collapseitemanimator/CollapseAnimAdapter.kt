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
 * * it provides [setAnimInfo] methods to set animation metadata which
 *   are expected to be defined prior to launch of the animation
 *
 * @see setAnimInfo
 */
abstract class CollapseAnimAdapter : RecyclerView.Adapter<CollapseAnimAdapter.ViewHolder>() {

    init {
        setupStableIds()
    }

    private var collapseAnimInfoMap: Map<Int, CollapseAnimInfo>? = null

    open class ViewHolder(
        override val rootView: CollapseAnimFrameLayout,
        override val textView: TextView,
    ) : RecyclerView.ViewHolder(rootView),
        CollapseAnimViewHolder {

        override var collapseAnimInfo: CollapseAnimInfo? = null

        override fun isCustomAnimated() = collapseAnimInfo != null

    }

    protected fun setupStableIds() {
        setHasStableIds(true)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // TODO Documentation
        holder.collapseAnimInfo = getCollapseAnimInfo(position)
    }

    fun getCollapseAnimInfo(position: Int) = collapseAnimInfoMap?.get(position)

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    protected fun setAnimInfo(animInfoList: List<CollapseAnimInfo>?) {
        this.collapseAnimInfoMap = animInfoList?.associate { Pair(it.itemIndex, it) }
    }

    protected fun setAnimInfo(animInfoMap: Map<Int, CollapseAnimInfo>?) {
        this.collapseAnimInfoMap = animInfoMap
    }

}