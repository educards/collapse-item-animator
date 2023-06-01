package com.educards.collapseitemanimator

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * @see setAnimInfo
 */
abstract class CollapseAnimAdapter : RecyclerView.Adapter<CollapseAnimAdapter.CollapseAnimViewHolder>() {

    init {
        setupStableIds()
    }

    private var collapseAnimInfoMap: Map<Int, CollapseAnimInfo>? = null

    open class CollapseAnimViewHolder(
        val rootView: CollapseAnimFrameLayout,
        val textView: TextView,
    ) : RecyclerView.ViewHolder(rootView) {

        var collapseAnimInfo: CollapseAnimInfo? = null

        fun isCustomAnimated() = collapseAnimInfo != null

    }

    protected fun setupStableIds() {
        setHasStableIds(true)
    }

    override fun onBindViewHolder(holder: CollapseAnimViewHolder, position: Int) {

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

    data class CollapseAnimInfo(
        val expanded: Boolean,
        val itemIndex: Int,
        val firstLineIndex: Int,
        val linesCount: Int
    )

}