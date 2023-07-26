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

class TestCaseControllerD : TestCaseController() {

    override val testCaseName = "D"

    override val streamingNotifyExecutor = DefaultStreamingNotifyExecutor()

    override fun getItemAnimInfoList() = listOf(
        ItemAnimTestInfo(0, 3, 1, 1),
        ItemAnimTestInfo(1, 5, 1, 1)
    )

    override fun generateExpandedSampleData() = listOf(
        "This item is animated with CollapseItemAnimator. This item is animated with CollapseItemAnimator. This item is animated with CollapseItemAnimator. ",
        "This item is animated with CollapseItemAnimator. This item is animated with CollapseItemAnimator. This item is animated with CollapseItemAnimator. ",
    )

    override fun generateCollapsedSampleData() = listOf(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer posuere magna est.",
        "Etiam aliquam sem eget mauris eleifend, id egestas velit sagittis. Vestibulum a velit at dui bibendum ornare at vitae nisl. Pellentesque vestibulum hendrerit lectus vitae bibendum.",
        "Integer mattis blandit ornare.",
        "This item is animated with CollapseItemAnimator.",
        "Sed eu neque arcu. Etiam eleifend dui augue, id tempus felis aliquam id.",
        "This item is animated with CollapseItemAnimator.",
    )

}