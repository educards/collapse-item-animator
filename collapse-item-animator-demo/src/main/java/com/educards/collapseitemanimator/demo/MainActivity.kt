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

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.educards.collapseitemanimator.AnimTargetState
import com.educards.collapseitemanimator.AnimInfo
import com.educards.collapseitemanimator.CollapsedStateInfo
import com.educards.collapseitemanimator.CollapseItemAnimator
import com.educards.collapseitemanimator.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.inflate(layoutInflater, R.layout.activity_main, null, false)
    }

    private var adapter: DemoAdapter? = null

    private var previousAnimDirection: AnimTargetState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initRecyclerView()
        initAdapterData()
        initSwitchButton()
    }

    private fun initRecyclerView() {

        this.adapter = DemoAdapter(layoutInflater, binding.recyclerView)

        binding.recyclerView.adapter = this.adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.itemAnimator = CollapseItemAnimator().apply {
            if (DEBUG) {
                changeDuration = DEBUG_ANIM_DURATION_MS
                moveDuration = DEBUG_ANIM_DURATION_MS
                removeDuration = DEBUG_ANIM_DURATION_MS
                addDuration = DEBUG_ANIM_DURATION_MS
            }
        }
    }

    private fun initAdapterData() {
        switchAdapterData(false)
    }

    private fun initSwitchButton() {
        updateSwitchButtonText(getOppositeAnimDirection(previousAnimDirection))
        binding.switchButton.setOnClickListener {
            switchAdapterData(true)
            updateSwitchButtonText(getOppositeAnimDirection(previousAnimDirection))
        }
    }

    private fun updateSwitchButtonText(animDir: AnimTargetState) {
        binding.switchButton.text = animDir.name
    }

    private fun switchAdapterData(animate: Boolean) {

        // detect new state (switch)
        val nextAnimDirection = getOppositeAnimDirection(previousAnimDirection)
        previousAnimDirection = nextAnimDirection

        // generate and set data
        val nextData = when (nextAnimDirection) {
            AnimTargetState.EXPANDED -> generateExpandedSampleData()
            AnimTargetState.COLLAPSED -> generateCollapsedSampleData()
        }

        if (animate) {
            adapter?.setData(
                nextData,
                listOf(
                    AnimInfo(
                        animItemIndex = 3,
                        animTargetState = nextAnimDirection,
                        collapsedStateInfo = CollapsedStateInfo(1, 2)
                    )
                )
            )

        } else {
            adapter?.setData(nextData, null)
        }
    }

    private fun getOppositeAnimDirection(animDirection: AnimTargetState?) =
        animDirection?.getOpposite() ?: AnimTargetState.EXPANDED

    private fun generateCollapsedSampleData() = listOf(

        "p apsoidj fpoaisj dfpoikaksj dpfoija sdpofij aspoidj",
        "qoiu h3uroeiquh mn apsoijd fpoiajs dfpoijas",
        "Dolor",
        "aspdoij fpoij;lcxkvnmzx,mcnvz.,xmn\ncvopqiwfpoijasdlkfj.z,",
        "Amet",

        "p apsoidj fpoaisj dfpoikaksj dpfoija sdpofij aspoidj",
        "qoiu h3uroeiquh mn apsoijd fpoiajs dfpoijas",
        "Dolor",
        "aspdoij fpoij;lcxkvnmzx,mcnvz.,xmn\ncvopqiwfpoijasdlkfj.z,",
        "Amet",

        "p apsoidj fpoaisj dfpoikaksj dpfoija sdpofij aspoidj",
        "qoiu h3uroeiquh mn apsoijd fpoiajs dfpoijas",
        "Dolor",
        "aspdoij fpoij;lcxkvnmzx,mcnvz.,xmn\ncvopqiwfpoijasdlkfj.z,",
        "Amet",

        "p apsoidj fpoaisj dfpoikaksj dpfoija sdpofij aspoidj",
        "qoiu h3uroeiquh mn apsoijd fpoiajs dfpoijas",
        "Dolor",
        "aspdoij fpoij;lcxkvnmzx,mcnvz.,xmn\ncvopqiwfpoijasdlkfj.z,",
        "Amet",

        "p apsoidj fpoaisj dfpoikaksj dpfoija sdpofij aspoidj",
        "qoiu h3uroeiquh mn apsoijd fpoiajs dfpoijas",
        "Dolor",
        "aspdoij fpoij;lcxkvnmzx,mcnvz.,xmn\ncvopqiwfpoijasdlkfj.z,",
        "Amet",

        "p apsoidj fpoaisj dfpoikaksj dpfoija sdpofij aspoidj",
        "qoiu h3uroeiquh mn apsoijd fpoiajs dfpoijas",
        "Dolor",
        "aspdoij fpoij;lcxkvnmzx,mcnvz.,xmn\ncvopqiwfpoijasdlkfj.z,",
        "Amet",

        "p apsoidj fpoaisj dfpoikaksj dpfoija sdpofij aspoidj",
        "qoiu h3uroeiquh mn apsoijd fpoiajs dfpoijas",
        "Dolor",
        "aspdoij fpoij;lcxkvnmzx,mcnvz.,xmn\ncvopqiwfpoijasdlkfj.z,",
        "Amet",
    )

    fun generateExpandedSampleData() = listOf(

        "Lorem poiasj dfpoija sdpofij aspdoijf pasoidjf\npoaisjd fpoiajsd fpoija sdpfoij aspdoifj apsoidjf\np apsoidj fpoaisj dfpoikaksj dpfoija sdpofij aspoidj",
        "Ipsum oapisuduh jfoiajush dflkjlkm,nx\nbcvk,nxbcnmcxb,mcnvb x,m.cn v.,mxc nv.,mh\nqoiu h3uroeiquh mn apsoijd fpoiajs dfpoijas\ndpofij aspdoikkfj paosidjf",
        "Dolor",
        "Sit aspdoifj oi asdpofij apsoidjf poijs dfpoij\naspdoij fpoij;lcxkvnmzx,mcnvz.,xmn\ncvopqiwfpoijasdlkfj.z,\nxxmcnv.zm,xncvoq asdcvnmkzxnckvlakjsnd",
        "Amet",

        "Lorem poiasj dfpoija sdpofij aspdoijf pasoidjf\npoaisjd fpoiajsd fpoija sdpfoij aspdoifj apsoidjf\np apsoidj fpoaisj dfpoikaksj dpfoija sdpofij aspoidj",
        "Ipsum oapisuduh jfoiajush dflkjlkm,nx\nbcvk,nxbcnmcxb,mcnvb x,m.cn v.,mxc nv.,mh\nqoiu h3uroeiquh mn apsoijd fpoiajs dfpoijas\ndpofij aspdoikkfj paosidjf",
        "Dolor",
        "Sit aspdoifj oi asdpofij apsoidjf poijs dfpoij\naspdoij fpoij;lcxkvnmzx,mcnvz.,xmn\ncvopqiwfpoijasdlkfj.z,\nxxmcnv.zm,xncvoq asdcvnmkzxnckvlakjsnd",
        "Amet",

        "Lorem poiasj dfpoija sdpofij aspdoijf pasoidjf\npoaisjd fpoiajsd fpoija sdpfoij aspdoifj apsoidjf\np apsoidj fpoaisj dfpoikaksj dpfoija sdpofij aspoidj",
        "Ipsum oapisuduh jfoiajush dflkjlkm,nx\nbcvk,nxbcnmcxb,mcnvb x,m.cn v.,mxc nv.,mh\nqoiu h3uroeiquh mn apsoijd fpoiajs dfpoijas\ndpofij aspdoikkfj paosidjf",
        "Dolor",
        "Sit aspdoifj oi asdpofij apsoidjf poijs dfpoij\naspdoij fpoij;lcxkvnmzx,mcnvz.,xmn\ncvopqiwfpoijasdlkfj.z,\nxxmcnv.zm,xncvoq asdcvnmkzxnckvlakjsnd",
        "Amet",

        "Lorem poiasj dfpoija sdpofij aspdoijf pasoidjf\npoaisjd fpoiajsd fpoija sdpfoij aspdoifj apsoidjf\np apsoidj fpoaisj dfpoikaksj dpfoija sdpofij aspoidj",
        "Ipsum oapisuduh jfoiajush dflkjlkm,nx\nbcvk,nxbcnmcxb,mcnvb x,m.cn v.,mxc nv.,mh\nqoiu h3uroeiquh mn apsoijd fpoiajs dfpoijas\ndpofij aspdoikkfj paosidjf",
        "Dolor",
        "Sit aspdoifj oi asdpofij apsoidjf poijs dfpoij\naspdoij fpoij;lcxkvnmzx,mcnvz.,xmn\ncvopqiwfpoijasdlkfj.z,\nxxmcnv.zm,xncvoq asdcvnmkzxnckvlakjsnd",
        "Amet",

        "Lorem poiasj dfpoija sdpofij aspdoijf pasoidjf\npoaisjd fpoiajsd fpoija sdpfoij aspdoifj apsoidjf\np apsoidj fpoaisj dfpoikaksj dpfoija sdpofij aspoidj",
        "Ipsum oapisuduh jfoiajush dflkjlkm,nx\nbcvk,nxbcnmcxb,mcnvb x,m.cn v.,mxc nv.,mh\nqoiu h3uroeiquh mn apsoijd fpoiajs dfpoijas\ndpofij aspdoikkfj paosidjf",
        "Dolor",
        "Sit aspdoifj oi asdpofij apsoidjf poijs dfpoij\naspdoij fpoij;lcxkvnmzx,mcnvz.,xmn\ncvopqiwfpoijasdlkfj.z,\nxxmcnv.zm,xncvoq asdcvnmkzxnckvlakjsnd",
        "Amet",

        "Lorem poiasj dfpoija sdpofij aspdoijf pasoidjf\npoaisjd fpoiajsd fpoija sdpfoij aspdoifj apsoidjf\np apsoidj fpoaisj dfpoikaksj dpfoija sdpofij aspoidj",
        "Ipsum oapisuduh jfoiajush dflkjlkm,nx\nbcvk,nxbcnmcxb,mcnvb x,m.cn v.,mxc nv.,mh\nqoiu h3uroeiquh mn apsoijd fpoiajs dfpoijas\ndpofij aspdoikkfj paosidjf",
        "Dolor",
        "Sit aspdoifj oi asdpofij apsoidjf poijs dfpoij\naspdoij fpoij;lcxkvnmzx,mcnvz.,xmn\ncvopqiwfpoijasdlkfj.z,\nxxmcnv.zm,xncvoq asdcvnmkzxnckvlakjsnd",
        "Amet",

        "Lorem poiasj dfpoija sdpofij aspdoijf pasoidjf\npoaisjd fpoiajsd fpoija sdpfoij aspdoifj apsoidjf\np apsoidj fpoaisj dfpoikaksj dpfoija sdpofij aspoidj",
        "Ipsum oapisuduh jfoiajush dflkjlkm,nx\nbcvk,nxbcnmcxb,mcnvb x,m.cn v.,mxc nv.,mh\nqoiu h3uroeiquh mn apsoijd fpoiajs dfpoijas\ndpofij aspdoikkfj paosidjf",
        "Dolor",
        "Sit aspdoifj oi asdpofij apsoidjf poijs dfpoij\naspdoij fpoij;lcxkvnmzx,mcnvz.,xmn\ncvopqiwfpoijasdlkfj.z,\nxxmcnv.zm,xncvoq asdcvnmkzxnckvlakjsnd",
        "Amet",

        )

    companion object {
        private const val DEBUG = true
        private const val DEBUG_ANIM_DURATION_MS = 2500L
    }

}