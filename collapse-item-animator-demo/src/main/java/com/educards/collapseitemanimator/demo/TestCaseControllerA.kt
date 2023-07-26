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

import com.educards.collapseitemanimator.DefaultStreamingNotifyExecutor

class TestCaseControllerA : TestCaseController() {

    override val testCaseName = "A"

    override val streamingNotifyExecutor = DefaultStreamingNotifyExecutor()

//    override val streamingNotifyExecutor = object : NotifyHelper() {
//        override fun notifyAfterDataSet(
//            adapter: RecyclerView.Adapter<*>,
//            itemAnimInfoMap: Map<Int, ItemAnimInfo>,
//            previousItemCount: Int,
//            currentItemCount: Int
//        ) {
//            if (previousItemCount <= 0) {
//                super.notifyAfterDataSet(adapter, itemAnimInfoMap, previousItemCount, currentItemCount)
//            } else if (previousItemCount > currentItemCount) {
//                // expanded -> collapsed
////                adapter.notifyItemChanged(3)
////                adapter.notifyItemMoved(3, 1)
////                adapter.notifyItemRangeRemoved(0, 1)
////                adapter.notifyItemRangeInserted(0, 1)
////                adapter.notifyItemRangeRemoved(2, 3)
////                adapter.notifyItemRangeInserted(2, 1)
//
//                adapter.notifyItemRangeRemoved(0, 3)
//                adapter.notifyItemRangeInserted(0, 2)
//                adapter.notifyItemChanged(2)
//                adapter.notifyItemRangeRemoved(3, 1)
//                adapter.notifyItemMoved(2, 1)
//
//
//            } else {
//                // collapsed -> expanded
//                adapter.notifyItemChanged(1)
//                adapter.notifyItemMoved(1, 3)
//                adapter.notifyItemRangeRemoved(0, 3)
//                adapter.notifyItemRangeInserted(0, 3)
//                adapter.notifyItemRangeInserted(4, 1)
//            }
//        }
//    }

    override fun getItemAnimInfoList() = listOf(
        ItemAnimTestInfo(3, 1, 1, 1)
    )

    override fun generateExpandedSampleData() = listOf(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer posuere magna est.",
        "Etiam aliquam sem eget mauris eleifend, id egestas velit sagittis. Vestibulum a velit at dui bibendum ornare at vitae nisl. Pellentesque vestibulum hendrerit lectus vitae bibendum.",
        "Integer mattis blandit ornare.",
        "This item is animated with CollapseItemAnimator. This item is animated with CollapseItemAnimator. This item is animated with CollapseItemAnimator. ",
        "Pellentesque non leo nisi. Donec pretium felis in ex bibendum, et rutrum odio convallis. Curabitur bibendum imperdiet justo accumsan tincidunt."
    )

    override fun generateCollapsedSampleData() = listOf(
        "Cras congue ante sed ultricies posuere.",
        "This item is animated with CollapseItemAnimator.",
        "Proin congue velit sit amet erat condimentum, quis pellentesque dui posuere. Praesent sed nisi sed justo elementum blandit."
    )

}