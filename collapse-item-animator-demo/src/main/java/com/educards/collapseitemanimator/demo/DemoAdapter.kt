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
import com.educards.collapseitemanimator.ItemAnimInfo
import com.educards.collapseitemanimator.ExpansionState
import com.educards.collapseitemanimator.CollapseAnimAdapter
import com.educards.collapseitemanimator.CollapseAnimFrameLayout
import com.educards.collapseitemanimator.CollapseAnimViewHolder
import com.educards.collapseitemanimator.DefaultStreamingNotifyExecutor
import com.educards.collapseitemanimator.StreamingNotifyExecutor
import java.util.TreeMap

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

    override val itemAnimInfoMap = TreeMap<Int, ItemAnimInfo>()

    override var dataExpansionState: ExpansionState? = null

    override var previousItemCount = -1

    override var streamingNotifyExecutor: StreamingNotifyExecutor = DefaultStreamingNotifyExecutor()

    override fun findViewHolderForAdapterPosition(position: Int) =
        recyclerView.findViewHolderForAdapterPosition(position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rootView = layoutInflater.inflate(R.layout.list_item, null) as CollapseAnimFrameLayout
        return ViewHolder(rootView, rootView.findViewById(R.id.text_view))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        onBindPostTransitionItemAnimInfo(holder, position)
        holder.textView.text = data?.get(position)
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    protected fun setupStableIds() {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int) =
        super<CollapseAnimAdapter>.getItemId(position)

    fun setData(
        data: List<String>,
        dataExpansionState: ExpansionState,
        animInfoList: List<ItemAnimInfo>?
    ) {
        onPreData(dataExpansionState)
        this.data = data
        setItemAnimInfoList(animInfoList)
        notifyAfterDataSet()
    }

    open class ViewHolder(
        override val rootView: CollapseAnimFrameLayout,
        override val textView: TextView,
    ) : RecyclerView.ViewHolder(rootView),
        CollapseAnimViewHolder {
        override var viewExpansionState: ExpansionState? = null
        override var itemAnimInfo: ItemAnimInfo? = null
        override var suppressNextAnimCycle: Boolean = false
    }

}