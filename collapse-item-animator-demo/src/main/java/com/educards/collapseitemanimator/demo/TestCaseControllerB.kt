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

class TestCaseControllerB : TestCaseController() {

    override val testCaseName = "B"

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
                adapter.notifyItemRangeRemoved(0, 14)
                adapter.notifyItemRangeInserted(0, 11)
                adapter.notifyItemChanged(11)
                adapter.notifyItemRangeRemoved(12, 1)
                adapter.notifyItemRangeInserted(12, 1)
                adapter.notifyItemMoved(11, 1)
            } else {
                adapter.notifyItemRangeRemoved(0, 1)
                adapter.notifyItemRangeInserted(0, 1)
                adapter.notifyItemChanged(1)
                adapter.notifyItemRangeRemoved(2, 11)
                adapter.notifyItemRangeInserted(2, 14)
                adapter.notifyItemMoved(1, 14)
            }
        }
    }

    override fun getItemAnimInfoList() = listOf(
        ItemAnimTestInfo(14, 1, 10, 1)
    )

    override fun generateExpandedSampleData() = listOf(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer posuere magna est.",
        "Etiam aliquam sem eget mauris eleifend, id egestas velit sagittis. Vestibulum a velit at dui bibendum ornare at vitae nisl. Pellentesque vestibulum hendrerit lectus vitae bibendum.",
        "Integer mattis blandit ornare.",
        "Pellentesque non leo nisi. Donec pretium felis in ex bibendum, et rutrum odio convallis. Curabitur bibendum imperdiet justo accumsan tincidunt.",
        "Pellentesque non leo nisi. Donec pretium felis in ex bibendum, et rutrum odio convallis. Curabitur bibendum imperdiet justo accumsan tincidunt.",
        "Pellentesque non leo nisi. Donec pretium felis in ex bibendum, et rutrum odio convallis. Curabitur bibendum imperdiet justo accumsan tincidunt.",
        "Pellentesque non leo nisi. Donec pretium felis in ex bibendum, et rutrum odio convallis. Curabitur bibendum imperdiet justo accumsan tincidunt.",
        "Pellentesque non leo nisi. Donec pretium felis in ex bibendum, et rutrum odio convallis. Curabitur bibendum imperdiet justo accumsan tincidunt.",
        "Pellentesque non leo nisi. Donec pretium felis in ex bibendum, et rutrum odio convallis. Curabitur bibendum imperdiet justo accumsan tincidunt.",
        "Pellentesque non leo nisi. Donec pretium felis in ex bibendum, et rutrum odio convallis. Curabitur bibendum imperdiet justo accumsan tincidunt.",
        "Pellentesque non leo nisi. Donec pretium felis in ex bibendum, et rutrum odio convallis. Curabitur bibendum imperdiet justo accumsan tincidunt.",
        "Pellentesque non leo nisi. Donec pretium felis in ex bibendum, et rutrum odio convallis. Curabitur bibendum imperdiet justo accumsan tincidunt.",
        "Pellentesque non leo nisi. Donec pretium felis in ex bibendum, et rutrum odio convallis. Curabitur bibendum imperdiet justo accumsan tincidunt.",
        "Pellentesque non leo nisi. Donec pretium felis in ex bibendum, et rutrum odio convallis. Curabitur bibendum imperdiet justo accumsan tincidunt.",
        "Item animated with CollapseItemAnimator.\nItem animated with CollapseItemAnimator.\nItem animated with CollapseItemAnimator.\n" +
                "Item animated with CollapseItemAnimator.\nItem animated with CollapseItemAnimator.\nItem animated with CollapseItemAnimator.\n" +
                "Item animated with CollapseItemAnimator.\nItem animated with CollapseItemAnimator.\nItem animated with CollapseItemAnimator.\n" +
                "Item animated with CollapseItemAnimator.\nItem animated with CollapseItemAnimator.\nItem animated with CollapseItemAnimator.\n" +
                "Item animated with CollapseItemAnimator.\nItem animated with CollapseItemAnimator.\nItem animated with CollapseItemAnimator.\n" +
                "Item animated with CollapseItemAnimator.\nItem animated with CollapseItemAnimator.\nItem animated with CollapseItemAnimator.\n" +
                "Item animated with CollapseItemAnimator.\nItem animated with CollapseItemAnimator.\nItem animated with CollapseItemAnimator.\n" +
                "Item animated with CollapseItemAnimator.\nItem animated with CollapseItemAnimator.\nItem animated with CollapseItemAnimator.\n" +
                "Item animated with CollapseItemAnimator.\nItem animated with CollapseItemAnimator.\nItem animated with CollapseItemAnimator.\n" +
                "Item animated with CollapseItemAnimator.\nItem animated with CollapseItemAnimator.\nItem animated with CollapseItemAnimator.\n" +
                "Item animated with CollapseItemAnimator.\nItem animated with CollapseItemAnimator.\nItem animated with CollapseItemAnimator.",
        "Proin congue velit sit amet erat condimentum, quis pellentesque dui posuere. Praesent sed nisi sed justo elementum blandit."
    )

    override fun generateCollapsedSampleData() = listOf(
        "Sed eu neque arcu. Etiam eleifend dui augue, id tempus felis aliquam id.",
        "Item animated with CollapseItemAnimator.",
        "Nulla pellentesque lobortis eros, sed luctus magna convallis eu. Nulla a augue fermentum, lacinia dolor in, semper ligula.",
        "Nulla pellentesque lobortis eros, sed luctus magna convallis eu. Nulla a augue fermentum, lacinia dolor in, semper ligula.",
        "Nulla pellentesque lobortis eros, sed luctus magna convallis eu. Nulla a augue fermentum, lacinia dolor in, semper ligula.",
        "Nulla pellentesque lobortis eros, sed luctus magna convallis eu. Nulla a augue fermentum, lacinia dolor in, semper ligula.",
        "Nulla pellentesque lobortis eros, sed luctus magna convallis eu. Nulla a augue fermentum, lacinia dolor in, semper ligula.",
        "Nulla pellentesque lobortis eros, sed luctus magna convallis eu. Nulla a augue fermentum, lacinia dolor in, semper ligula.",
        "Nulla pellentesque lobortis eros, sed luctus magna convallis eu. Nulla a augue fermentum, lacinia dolor in, semper ligula.",
        "Nulla pellentesque lobortis eros, sed luctus magna convallis eu. Nulla a augue fermentum, lacinia dolor in, semper ligula.",
        "Nulla pellentesque lobortis eros, sed luctus magna convallis eu. Nulla a augue fermentum, lacinia dolor in, semper ligula.",
        "Nulla pellentesque lobortis eros, sed luctus magna convallis eu. Nulla a augue fermentum, lacinia dolor in, semper ligula.",
        "Nulla pellentesque lobortis eros, sed luctus magna convallis eu. Nulla a augue fermentum, lacinia dolor in, semper ligula."
    )

}