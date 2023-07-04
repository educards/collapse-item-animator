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

import android.widget.TextView

/**
 * An interface required to be implemented by those
 * [androidx.recyclerview.widget.RecyclerView.ViewHolder]s
 * support "collapse item animation".
 */
interface CollapseAnimViewHolder {

    val rootView: CollapseAnimFrameLayout

    /**
     * "Collapse item animation" is (currently) able to collapse/expand [TextView]s only.
     * This is the [TextView] instance to animate. It is located inside [rootView].
     */
    val textView: TextView

    /**
     * Meta information defining how the views wrapped
     * by this [ViewHolder] will be animated.
     */
    var collapseAnimInfo: CollapseAnimInfo?

    fun isCustomAnimated() = collapseAnimInfo != null

}