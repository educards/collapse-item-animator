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

import android.view.View
import com.educards.collapseitemanimator.AnimInfo
import com.educards.collapseitemanimator.ExpansionState
import com.educards.collapseitemanimator.ItemAnimInfo
import com.educards.collapseitemanimator.StreamingNotifyExecutor

abstract class TestCaseController {

    abstract val testCaseName: String

    val viewId = View.generateViewId()

    /**
     * A [StreamingNotifyExecutor] which executes
     * hardwired sequence of `notify*` invocations
     * for sake of testing a test-case scenario.
     */
    abstract val hardwiredNotifyExecutor: StreamingNotifyExecutor

    private var previousAnimDirection: ExpansionState? = null

    fun getNextActionTitle() =
        getOppositeAnimDirection(previousAnimDirection)

    private fun getOppositeAnimDirection(animDirection: ExpansionState?) =
        animDirection?.getOpposite() ?: ExpansionState.EXPANDED

    fun initOrSwitchAdapterData(adapter: DemoAdapter) {

        // detect new state (switch)
        val nextDataExpansionState = getOppositeAnimDirection(previousAnimDirection)
        previousAnimDirection = nextDataExpansionState

        // init current adapter.dataExpansionState
        // if adapter is fresh (without any data so far)
        if (adapter.dataExpansionState == null) {
            adapter.onPreData(getOppositeAnimDirection(nextDataExpansionState))
        }

        // generate and set data
        val nextData = when (nextDataExpansionState) {
            ExpansionState.EXPANDED -> generateExpandedSampleData()
            ExpansionState.COLLAPSED -> generateCollapsedSampleData()
        }

        val itemAnimInfoList = getItemAnimInfoList().map {
            ItemAnimInfo(
                itemId = adapter.getItemId(if (nextDataExpansionState == ExpansionState.EXPANDED) it.animItemCollapsedIndex else it.animItemExpandedIndex),
                itemIndexPreTransition = if (nextDataExpansionState == ExpansionState.EXPANDED) it.animItemCollapsedIndex else it.animItemExpandedIndex,
                itemIndexPostTransition = if (nextDataExpansionState == ExpansionState.EXPANDED) it.animItemExpandedIndex else it.animItemCollapsedIndex,
                itemTargetExpansionState = nextDataExpansionState,
                animInfo = AnimInfo(it.animItemCollapsedVisibleFirstLine, it.animItemCollapsedVisibleLinesCount)
            )
        }

        adapter.setData(nextData, nextDataExpansionState, itemAnimInfoList)
    }

    abstract fun getItemAnimInfoList(): List<ItemAnimTestInfo>

    abstract fun generateExpandedSampleData(): List<String>

    abstract fun generateCollapsedSampleData(): List<String>

    data class ItemAnimTestInfo(
        val animItemExpandedIndex: Int,
        val animItemCollapsedIndex: Int,
        val animItemCollapsedVisibleFirstLine: Int,
        val animItemCollapsedVisibleLinesCount: Int
    )

}