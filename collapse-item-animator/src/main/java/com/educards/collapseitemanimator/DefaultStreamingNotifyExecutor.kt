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

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

open class DefaultStreamingNotifyExecutor: StreamingNotifyExecutor {

    override fun doNotify(
        adapter: RecyclerView.Adapter<*>,
        itemAnimInfoMap: Map<Int, ItemAnimInfo>,
        previousItemCount: Int,
        currentItemCount: Int
    ) {
        if (BuildConfig.DEBUG) Log.d(TAG, "doNotify [previousItemCount: $previousItemCount, currentItemCount: $currentItemCount]")

        // TODO Describe the idea behind the algorithm
        //      with 2 phases (scale, move).
        //      Prevents IndexOutOfBoundsException: Inconsistency detected. Invalid view holder adapter positionViewHolder
        //      (https://github.com/educards/collapse-item-animator/issues/6)

        // The algorithm requires `itemAnimInfoList` to be sorted by [ItemAnimInfo.itemIndexPreTransition] in ascending order
        val itemAnimInfoList = itemAnimInfoMap.values.toMutableList()
        itemAnimInfoList.sortBy { it.itemIndexPreTransition }

        // scale
        val scaleRatio = currentItemCount.toDouble() / previousItemCount.toDouble()
        val scaledIndices = mutableListOf<Int>()
        itemAnimInfoList.forEach {
            val newScaledPreTransitionIndex = (it.itemIndexPreTransition * scaleRatio).roundToInt()
            shiftIfNeededAndAppend(scaledIndices, newScaledPreTransitionIndex, currentItemCount - 1)
        }

        if (BuildConfig.DEBUG) {
            debugLogScaledIndices(itemAnimInfoList, scaledIndices)
        }

        // walk through the ItemAnimInfoList items
        var previousItemIndex = -1
        var previousScaledItemIndex = -1
        var itemIndexDelta = 0
        for (i in 0..itemAnimInfoList.lastIndex) {

            // notify remove
            notifyRemoveIfNeeded(adapter, previousItemIndex, itemAnimInfoList[i].itemIndexPreTransition, itemIndexDelta)
            previousItemIndex = itemAnimInfoList[i].itemIndexPreTransition
            // notify insert
            notifyInsertIfNeeded(adapter, previousScaledItemIndex, scaledIndices[i])
            previousScaledItemIndex = scaledIndices[i]
            // notify change
            if (BuildConfig.DEBUG) Log.d(TAG, "notifyItemChanged(${scaledIndices[i]})")
            adapter.notifyItemChanged(scaledIndices[i])

            itemIndexDelta = previousScaledItemIndex - previousItemIndex
        }

        notifyRemoveIfNeeded(adapter, previousItemIndex, previousItemCount, itemIndexDelta)
        notifyInsertIfNeeded(adapter, previousScaledItemIndex, currentItemCount)

        // compute moves
        val targetMoveIndices = itemAnimInfoList.map { it.itemIndexPostTransition }.toMutableList()
        for (i in 0..targetMoveIndices.lastIndex) {
            for (j in (i+1)..targetMoveIndices.lastIndex) {
                val currentFrom = scaledIndices[i]
                val currentTo = targetMoveIndices[i]
                val futureFrom = scaledIndices[j]
                val futureTo = targetMoveIndices[j]

                // How the future move affects
                // target position of the current move?
                if ((futureFrom <= currentTo) && (currentTo < futureTo)) {
                    targetMoveIndices[i] = targetMoveIndices[i] + 1
                }

                // How the current move affects
                // target position of the future move?
                if ((currentFrom < futureFrom) && (futureFrom <= currentTo)) {
                    scaledIndices[j] = scaledIndices[j] - 1
                }
            }
        }
        // move
        for (i in 0..targetMoveIndices.lastIndex) {
            if (scaledIndices[i] != targetMoveIndices[i]) {
                if (BuildConfig.DEBUG) Log.d(TAG, "notifyItemMoved(${scaledIndices[i]}, ${targetMoveIndices[i]})")
                adapter.notifyItemMoved(scaledIndices[i], targetMoveIndices[i])
            }
        }
    }

    private fun debugLogScaledIndices(
        itemAnimInfoList: List<ItemAnimInfo>,
        scaledIndices: MutableList<Int>
    ) {
        val overviewOfShiftedIndices = StringBuilder()
        if (itemAnimInfoList.isEmpty()) {
            overviewOfShiftedIndices.append("-")
        } else {
            overviewOfShiftedIndices.append(" | ")
            itemAnimInfoList.forEachIndexed { index, itemAnimInfo ->
                overviewOfShiftedIndices.append("${itemAnimInfo.itemIndexPreTransition} -> ${scaledIndices[index]}")
                overviewOfShiftedIndices.append(" | ")
            }
        }
        Log.d(TAG, "scaled shifted indices: $overviewOfShiftedIndices")
    }

    private fun notifyRemoveIfNeeded(adapter: RecyclerView.Adapter<*>, fromExclusive: Int, toExclusive: Int, indexDelta: Int) {
        val removeCount = toExclusive - fromExclusive - 1
        if (removeCount > 0) {
            if (BuildConfig.DEBUG) Log.d(TAG, "notifyItemRangeRemoved(${fromExclusive + indexDelta + 1}, $removeCount)")
            adapter.notifyItemRangeRemoved(fromExclusive + indexDelta + 1, removeCount)
        }
    }

    private fun notifyInsertIfNeeded(adapter: RecyclerView.Adapter<*>, fromExclusive: Int, toExclusive: Int) {
        val insertCount = toExclusive - fromExclusive - 1
        if (insertCount > 0) {
            if (BuildConfig.DEBUG) Log.d(TAG, "notifyItemRangeInserted(${fromExclusive + 1}, $insertCount)")
            adapter.notifyItemRangeInserted(fromExclusive + 1, insertCount)
        }
    }

    private fun isLastValueEqual(values: MutableList<Int>, valueToCheck: Int): Boolean {
        val last = values.lastOrNull()
        return last != null && last == valueToCheck
    }

    // TODO Document that "value" is in context of invoking method index
    // TODO Refactor values to indices, valueToAdd as indexToAdd and maxAllowedValue as maxAllowedIndex?
    private fun shiftIfNeededAndAppend(values: MutableList<Int>, valueToAdd: Int, maxAllowedValue: Int) {

        var valueToAdd = if (valueToAdd > maxAllowedValue) valueToAdd - 1 else valueToAdd

        if (isLastValueEqual(values, valueToAdd)) {

            val lastLeftShiftedIndex = dryRunShiftLeft(values)
            if (lastLeftShiftedIndex >= 0) {

                if (values[lastLeftShiftedIndex] > 0) {
                    // Perform shiftLeft before adding the value
                    shiftLeft(values, lastLeftShiftedIndex)
                } else {
                    // 'shiftLeft' of the 'values' list would result in "out of bounds" (value < 0).
                    // Try to perform 'shiftRight' on the 'valueToAdd' (fallback).
                    // This might also fail - if so, we are out of options.
                    if (valueToAdd + 1 > maxAllowedValue) {
                        error("'valueToAdd' is out of bounds [valueToAdd: $valueToAdd, maxAllowedValue: $maxAllowedValue]")
                    }
                    valueToAdd++
                }

            } else {
                // No shift is required.
                // This should however never occur, because the necessity to invoke the whole "shift" code block
                // has already been checked by 'isLastValueEqual(List<Int>, Int)'.
                error("Unexpected invocation")
            }
        }

        values.add(valueToAdd)
    }

    private fun shiftLeft(values: MutableList<Int>, lastLeftShiftedIndex: Int) {
        for (i in values.lastIndex downTo lastLeftShiftedIndex) {
            values[i] = values[i] - 1
        }
    }

    /**
     * Detects which indices (starting from left to right: `N-1, N-2, ..., <return-value>`)
     * needs to be shifted left (decremented by 1) in order to make
     * `values` list contain only non overlapping integers in case the `values` list
     * would be appended with value equal to `value.last()` (the duplicate of its last value).
     * @param values List of ascending integers
     * @return Index of last value which is subject to reduction of 1 (`value - 1`)
     *         or `-1` if no item needs to be decremented.
     */
    private fun dryRunShiftLeft(values: List<Int>): Int {
        var previousShiftedValue = values.lastOrNull()
        var indexOfPreviousShiftedValue = values.lastIndex
        for (i in values.lastIndex downTo 0) {
            val valueToShift = values[i]
            if (valueToShift == previousShiftedValue) {
                previousShiftedValue = valueToShift - 1
                indexOfPreviousShiftedValue = i
            } else {
                break
            }
        }
        return indexOfPreviousShiftedValue
    }

    companion object {
        private const val TAG = "DefStreamingNotifyExec"
    }


    // TODO Move this test cases to src/test and run them with some CI engine.
    //      At time of writing this code we had some unresolved configuration issues
    //      which kept us from creating proper JUnit tests.
//    scaledIndices.clear()
//    scaledIndices.add(5)
//    newScaledPreTransitionIndex = 5
//    shiftIfNeededAndAppend(scaledIndices, newScaledPreTransitionIndex, currentItemCount)
//    Log.d(TAG, "TestCase (0..${currentItemCount-1}): ${scaledIndices.joinToString(", ")}")
//
//    scaledIndices.clear()
//    scaledIndices.add(4)
//    scaledIndices.add(5)
//    newScaledPreTransitionIndex = 5
//    shiftIfNeededAndAppend(scaledIndices, newScaledPreTransitionIndex, currentItemCount)
//    Log.d(TAG, "TestCase (0..${currentItemCount-1}): ${scaledIndices.joinToString(", ")}")
//
//    scaledIndices.clear()
//    scaledIndices.add(2)
//    scaledIndices.add(4)
//    scaledIndices.add(5)
//    newScaledPreTransitionIndex = 5
//    shiftIfNeededAndAppend(scaledIndices, newScaledPreTransitionIndex, currentItemCount)
//    Log.d(TAG, "TestCase (0..${currentItemCount-1}): ${scaledIndices.joinToString(", ")}")
//
//    scaledIndices.clear()
//    scaledIndices.add(2)
//    scaledIndices.add(3)
//    newScaledPreTransitionIndex = 3
//    shiftIfNeededAndAppend(scaledIndices, newScaledPreTransitionIndex, currentItemCount)
//    Log.d(TAG, "TestCase (0..${currentItemCount-1}): ${scaledIndices.joinToString(", ")}")
//
//    scaledIndices.clear()
//    scaledIndices.add(0)
//    scaledIndices.add(1)
//    scaledIndices.add(2)
//    scaledIndices.add(3)
//    newScaledPreTransitionIndex = 3
//    shiftIfNeededAndAppend(scaledIndices, newScaledPreTransitionIndex, currentItemCount)
//    Log.d(TAG, "TestCase (0..${currentItemCount-1}): ${scaledIndices.joinToString(", ")}")
//
//    scaledIndices.clear()
//    scaledIndices.add(0)
//    scaledIndices.add(1)
//    scaledIndices.add(2)
//    scaledIndices.add(3)
//    scaledIndices.add(4)
//    newScaledPreTransitionIndex = 4
//    shiftIfNeededAndAppend(scaledIndices, newScaledPreTransitionIndex, currentItemCount)
//    Log.d(TAG, "TestCase (0..${currentItemCount-1}): ${scaledIndices.joinToString(", ")}")

}