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

package com.educards.collapseitemanimator

import androidx.recyclerview.widget.RecyclerView

/**
 * [StreamingNotifyExecutor] computes and invokes sequence of `notify*`
 * methods in a way which
 * * properly triggers collapse/expand animations
 * * doesn't corrupt streaming aspect of data
 */
interface StreamingNotifyExecutor {

    /**
     * Computes and executes the sequence of `adapter.notify*` methods
     * to correctly trigger collapse/expand animation.
     *
     * ## Streamed lists
     * This implementation supports streaming:
     * the pre- and post-transition data are not required
     * to compute the proper sequence of `notify*` invocations.
     *
     * ## Notes
     *
     * ### Note on [DiffUtil][androidx.recyclerview.widget.DiffUtil]
     * If streaming would not be a required feature of `CollapseItemAnimator`,
     * then we would be able to use built-in [DiffUtil][androidx.recyclerview.widget.DiffUtil] to generate the
     * `notify*` sequence.
     *
     * ### Note on [Adapter.notifyDataSetChanged()][androidx.recyclerview.widget.RecyclerView.Adapter.notifyDataSetChanged]
     * We also can't just call [notifyDataSetChanged()][androidx.recyclerview.widget.RecyclerView.Adapter.notifyDataSetChanged],
     * because in order to trigger the collapse/expand animation
     * the [notifyItemChanged(Int)][androidx.recyclerview.widget.RecyclerView.Adapter.notifyItemChanged]
     * and [notifyItemMoved(Int, Int)][androidx.recyclerview.widget.RecyclerView.Adapter.notifyItemMoved]
     * methods need to be invoked correctly for animated items
     * ([Stackoverflow discussion](https://stackoverflow.com/a/76234207/915756)).
     */
    fun doNotify(
        adapter: RecyclerView.Adapter<*>,
        itemAnimInfoMap: Map<Int, ItemAnimInfo>,
        previousItemCount: Int,
        currentItemCount: Int
    )

}