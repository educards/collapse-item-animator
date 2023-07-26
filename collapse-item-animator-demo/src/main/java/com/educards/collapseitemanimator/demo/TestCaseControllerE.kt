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

import androidx.recyclerview.widget.RecyclerView
import com.educards.collapseitemanimator.DefaultStreamingNotifyExecutor
import com.educards.collapseitemanimator.ItemAnimInfo

class TestCaseControllerE : TestCaseController() {

    override val testCaseName = "E"

    override val hardwiredNotifyExecutor = object : DefaultStreamingNotifyExecutor() {
        override fun doNotify(
            adapter: RecyclerView.Adapter<*>,
            itemAnimInfoMap: Map<Int, ItemAnimInfo>,
            previousItemCount: Int,
            currentItemCount: Int
        ) {
            if (previousItemCount <= 0) {
                super.doNotify(adapter, itemAnimInfoMap, previousItemCount, currentItemCount)
            } else if (previousItemCount > currentItemCount) {
                adapter.notifyItemRangeRemoved(0, 2)
                adapter.notifyItemChanged(0)
                adapter.notifyItemRangeRemoved(1, 1)
                adapter.notifyItemChanged(1)
                adapter.notifyItemRangeRemoved(2, 1)
                adapter.notifyItemChanged(2)
                adapter.notifyItemMoved(0, 2)
                adapter.notifyItemMoved(0, 2)
            } else {
                adapter.notifyItemChanged(0)
                adapter.notifyItemRangeInserted(1, 1)
                adapter.notifyItemChanged(2)
                adapter.notifyItemRangeInserted(3, 2)
                adapter.notifyItemChanged(5)
                adapter.notifyItemRangeInserted(6, 1)
                adapter.notifyItemMoved(0, 6)
                adapter.notifyItemMoved(1, 2)
            }
        }
    }

    override fun getItemAnimInfoList() = listOf(
        ItemAnimTestInfo(0, 6, 1, 1),
        ItemAnimTestInfo(1, 2, 1, 1),
        ItemAnimTestInfo(2, 4, 1, 1),
    )

    override fun generateExpandedSampleData() = listOf(
        "This item is animated with CollapseItemAnimator. This item is animated with CollapseItemAnimator. This item is animated with CollapseItemAnimator. ",
        "This item is animated with CollapseItemAnimator. This item is animated with CollapseItemAnimator. This item is animated with CollapseItemAnimator. ",
        "This item is animated with CollapseItemAnimator. This item is animated with CollapseItemAnimator. This item is animated with CollapseItemAnimator. ",
    )

    override fun generateCollapsedSampleData() = listOf(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer posuere magna est.",
        "Etiam aliquam sem eget mauris eleifend, id egestas velit sagittis. Vestibulum a velit at dui bibendum ornare at vitae nisl. Pellentesque vestibulum hendrerit lectus vitae bibendum.",
        "This item is animated with CollapseItemAnimator.",
        "Pellentesque non leo nisi. Donec pretium felis in ex bibendum, et rutrum odio convallis. Curabitur bibendum imperdiet justo accumsan tincidunt.",
        "This item is animated with CollapseItemAnimator.",
        "Proin congue velit sit amet erat condimentum, quis pellentesque dui posuere. Praesent sed nisi sed justo elementum blandit.",
        "This item is animated with CollapseItemAnimator."
    )

}