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

/**
 * Data class providing the details of how exactly the
 * collapse/expand animation should be performed.
 *
 * @see collapsedStateVisibleFirstLine
 * @see collapsedStateVisibleLinesCount
 */
data class CollapsedStateInfo(

    /**
     * Index of the first visible line of the animated [android.widget.TextView]
     * in its collapsed state.
     */
    val collapsedStateVisibleFirstLine: Int,

    /**
     * Number of visible lines of the animated [android.widget.TextView]
     * in its collapsed state.
     */
    val collapsedStateVisibleLinesCount: Int

)