/*
 * Copyright © 2023 Educards Learning, SL.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.educards.collapseitemanimator.demo

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.educards.collapseitemanimator.AnimInfo
import com.educards.collapseitemanimator.AnimTargetState
import com.educards.collapseitemanimator.CollapseAnimAdapter
import com.educards.collapseitemanimator.CollapseAnimFrameLayout
import com.educards.collapseitemanimator.CollapseAnimViewHolder
import com.educards.collapseitemanimator.CollapsedStateInfo

class DemoAdapter(

    private val layoutInflater: LayoutInflater,

    /**
     * A cyclic reference to [RecyclerView] which
     * is used to access view holders based
     * on their adapter position (see [findViewHolderForAdapterPosition]).
     */
    private val recyclerView: RecyclerView

) : RecyclerView.Adapter<DemoAdapter.ViewHolder>(),
    CollapseAnimAdapter {

    init {
        setupStableIds()
    }

    private var data: List<String>? = null

    override val pendingAnimInfoMap = mutableMapOf<Int, Pair<AnimTargetState, CollapsedStateInfo>>()

    override fun findViewHolderForAdapterPosition(position: Int) =
        recyclerView.findViewHolderForAdapterPosition(position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rootView = layoutInflater.inflate(R.layout.list_item, null) as CollapseAnimFrameLayout
        return ViewHolder(rootView, rootView.findViewById(R.id.text_view))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setPendingAnimInfo(holder, position)
        holder.textView.text = data?.get(position)
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    protected fun setupStableIds() {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setData(
        data: List<String>,
        animInfoList: List<AnimInfo>?
    ) {
        this.data = data

        setAnimInfo(animInfoList)

        // We can't just call notifyDataSetChanged() here,
        // because the expand/collapse animation would not be triggered correctly.
        // Discussion: https://stackoverflow.com/a/76234207/915756
        notifyItemRangeChanged(0, data.size)
    }

    open class ViewHolder(
        override val rootView: CollapseAnimFrameLayout,
        override val textView: TextView,
    ) : RecyclerView.ViewHolder(rootView),
        CollapseAnimViewHolder {
        override var animTargetState: AnimTargetState? = null
        override var collapsedStateInfo: CollapsedStateInfo? = null
    }

}