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
 * Animation details bound to a specific item `View`.
 *
 * Note: There is a more generic [AnimInfo] class similar to this one,
 * however [AnimInfo] provides details just for the collapse/expand animation procedure
 * and is not bound to any specific item `View` (see [animInfo] attribute).
 */
data class ItemAnimInfo(

    /**
     * Provides ID of the bound animated view.
     *
     * Note: In order to perform the collapse/expand transition correctly,
     * the view must be bound to the same ID in [both states][ExpansionState].
     */
    val itemId: Long,

    /**
     * Index of the animated item (from Adapter's point of view) before the transition.
     */
    val itemIndexPreTransition: Int,

    /**
     * Index of the animated item (from Adapter's point of view) after the transition.
     */
    val itemIndexPostTransition: Int,

    /**
     * Target [ExpansionState] of the animation.
     */
    val itemTargetExpansionState: ExpansionState,

    /**
     * Collapse/expand animation details.
     */
    val animInfo: AnimInfo

)