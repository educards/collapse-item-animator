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

class TestCaseControllerB : TestCaseController() {

    override val testCaseName = "B"

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
//
////                adapter.notifyItemChanged(4)
////                adapter.notifyItemMoved(4, 1)
////                adapter.notifyItemRangeRemoved(0, 1)
////                adapter.notifyItemRangeInserted(0, 1)
////                adapter.notifyItemRangeRemoved(2, 4)
////                adapter.notifyItemRangeInserted(2, 1)
//
//                adapter.notifyItemRangeRemoved(0, 4)
//                adapter.notifyItemRangeInserted(0, 1)
//                adapter.notifyItemChanged(1)
//                adapter.notifyItemRangeRemoved(2, 1)
//                adapter.notifyItemRangeInserted(2, 1)
//                //adapter.notifyItemMoved(1, 1)
//
//            } else {
//                // collapsed -> expanded
//
//                // TODO This one fails
////                adapter.notifyItemChanged(1)
////                adapter.notifyItemMoved(1, 4)
////                adapter.notifyItemRangeRemoved(0, 3)
////                adapter.notifyItemRangeInserted(0, 4)
////                adapter.notifyItemRangeInserted(5, 1)
//
//                adapter.notifyItemRangeRemoved(0, 1)
//                adapter.notifyItemRangeInserted(0, 1)
//                adapter.notifyItemChanged(1)
//                adapter.notifyItemRangeRemoved(2, 1)
//                adapter.notifyItemRangeInserted(2, 4)
//
//                adapter.notifyItemMoved(1, 4)
//            }
//        }
//    }

    override fun getItemAnimInfoList() = listOf(
        ItemAnimTestInfo(4, 1, 1, 1)
    )

    override fun generateExpandedSampleData() = listOf(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer posuere magna est.",
        "Etiam aliquam sem eget mauris eleifend, id egestas velit sagittis. Vestibulum a velit at dui bibendum ornare at vitae nisl. Pellentesque vestibulum hendrerit lectus vitae bibendum.",
        "Integer mattis blandit ornare.",
        "Pellentesque non leo nisi. Donec pretium felis in ex bibendum, et rutrum odio convallis. Curabitur bibendum imperdiet justo accumsan tincidunt.",
        "This item is animated with CollapseItemAnimator. This item is animated with CollapseItemAnimator. This item is animated with CollapseItemAnimator. ",
        "Proin congue velit sit amet erat condimentum, quis pellentesque dui posuere. Praesent sed nisi sed justo elementum blandit."
    )

    override fun generateCollapsedSampleData() = listOf(
        "Sed eu neque arcu. Etiam eleifend dui augue, id tempus felis aliquam id.",
        "This item is animated with CollapseItemAnimator.",
        "Nulla pellentesque lobortis eros, sed luctus magna convallis eu. Nulla a augue fermentum, lacinia dolor in, semper ligula."
    )

}