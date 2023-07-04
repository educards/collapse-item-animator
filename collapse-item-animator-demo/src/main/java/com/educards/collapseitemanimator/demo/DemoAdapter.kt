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

import android.app.Activity
import android.view.ViewGroup
import com.educards.collapseitemanimator.CollapseAnimAdapter
import com.educards.collapseitemanimator.CollapseAnimFrameLayout

class DemoAdapter(
    private val activity: Activity
) : CollapseAnimAdapter() {

    private var data: List<String>? = null

    var dataState: DataState? = null
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollapseAnimViewHolder {
        val rootView = activity.layoutInflater.inflate(R.layout.list_item, null) as CollapseAnimFrameLayout
        return CollapseAnimViewHolder(rootView, rootView.findViewById(R.id.text_view))
    }

    override fun onBindViewHolder(holder: CollapseAnimViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.textView.text = data?.get(position)
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    fun setData(
        dataState: DataState,
        data: List<String>,
        animInfoList: List<CollapseAnimInfo>?
    ) {
        this.dataState = dataState
        this.data = data
        setAnimInfo(animInfoList)

        // We can't just call notifyDataSetChanged() here,
        // because the expand/collapse animation would not be triggered correctly.
        // Discussion: https://stackoverflow.com/a/76234207/915756
        notifyItemRangeChanged(0, data.size)
    }

    /**
     * Defines the state of Adapter data from the
     * [collapse animation][com.educards.collapseitemanimator.CollapseItemAnimator]
     * point of view
     */
    enum class DataState(
        val asBoolean: Boolean
    ) {
        EXPANDED(true),
        COLLAPSED(false)
    }

}