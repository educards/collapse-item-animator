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
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.educards.collapseitemanimator.CollapseAnimAdapter
import com.educards.collapseitemanimator.AnimTargetState
import com.educards.collapseitemanimator.AnimInfo
import com.educards.collapseitemanimator.CollapseAnimFrameLayout

class DemoAdapter(
    private val layoutInflater: LayoutInflater,
    recyclerView: RecyclerView
) : CollapseAnimAdapter(recyclerView) {

    private var data: List<String>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rootView = layoutInflater.inflate(R.layout.list_item, null) as CollapseAnimFrameLayout
        return ViewHolder(rootView, rootView.findViewById(R.id.text_view))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.textView.text = data?.get(position)
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
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

    private fun setAnimInfo(animInfoList: List<AnimInfo>?) {
        resetAnimInfo()
        animInfoList?.forEach { animInfo ->

            // "Out-animation" setup
            setAnimInfoForCurrentHolder(
                animInfo.animItemIndex,
                animInfo.animTargetState.getOpposite(),
                animInfo
                    .getIfAnimDirOrNull(AnimTargetState.COLLAPSED)
                    ?.collapsedStateInfo
            )

            // "In-animation" setup
            setAnimInfoForPendingHolder(animInfo)
        }
    }

}