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

data class AnimInfo(

    /**
     * Index of the animated item
     * from Adapter's point of view.
     */
    val animItemIndex: Int,

    /**
     * Target animation state.
     */
    val animTargetState: AnimTargetState,

    /**
     * Animation details.
     */
    val collapsedStateInfo: CollapsedStateInfo

) {

    fun getIfAnimDirOrNull(animDir: AnimTargetState) =
        if (this.animTargetState == animDir) this else null

}