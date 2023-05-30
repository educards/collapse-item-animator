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

    private var animInfoMap: Map<Int, CollapseAnimInfo>? = null

    open class CollapseAnimViewHolder(
        val rootView: CollapseAnimFrameLayout,
        val textView: TextView,
    ) : RecyclerView.ViewHolder(rootView) {

        var animInfo: CollapseAnimInfo? = null

        fun isCustomAnimated() = animInfo != null

    }

    protected fun setupStableIds() {
        setHasStableIds(true)
    }

    override fun onBindViewHolder(holder: CollapseAnimViewHolder, position: Int) {

        // TODO Documentation
        holder.animInfo = null
        animInfoMap?.let { animInfoMap ->
            if (animInfoMap.contains(position)) {
                holder.animInfo = animInfoMap[position]
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    protected fun setAnimInfo(animInfoList: List<CollapseAnimInfo>?) {
        this.animInfoMap = animInfoList?.associate { Pair(it.itemIndex, it) }
    }

    protected fun setAnimInfo(animInfoMap: Map<Int, CollapseAnimInfo>?) {
        this.animInfoMap = animInfoMap
    }

    data class CollapseAnimInfo(
        val expanded: Boolean,
        val itemIndex: Int,
        val firstLineIndex: Int,
        val linesCount: Int
    )

}