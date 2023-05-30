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
import com.educards.collapseitemanimator.CollapseAnimAdapter
import com.educards.collapseitemanimator.CollapseItemAnimator
import com.educards.collapseitemanimator.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.inflate(layoutInflater, R.layout.activity_main, null, false)
    }

    private var adapter: DemoAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initRecyclerView()
        initAdapterData()
        initSwitchButton()
    }

    private fun initRecyclerView() {

        this.adapter = DemoAdapter(this)

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
        switchAdapterData()
    }

    private fun initSwitchButton() {
        updateSwitchButtonText(getNextDataState())
        binding.switchButton.setOnClickListener {
            switchAdapterData()
            updateSwitchButtonText(getNextDataState())
        }
    }

    private fun updateSwitchButtonText(nextDataState: DemoAdapter.DataState) {
        binding.switchButton.text = if (nextDataState == DemoAdapter.DataState.EXPANDED) {
            "Expand"
        } else {
            "Collapse"
        }
    }

    private fun switchAdapterData() {

        // detect new state (switch)
        val newDataState = getNextDataState()

        // generate and set data
        adapter?.setData(
            newDataState,
            when (newDataState) {
                DemoAdapter.DataState.EXPANDED -> generateExpandedData()
                DemoAdapter.DataState.COLLAPSED -> generateCollapsedData()
            },
            generateAnimInfo(newDataState)
        )
    }

    private fun getNextDataState() =
        when (adapter?.dataState) {
            DemoAdapter.DataState.EXPANDED -> DemoAdapter.DataState.COLLAPSED
            DemoAdapter.DataState.COLLAPSED -> DemoAdapter.DataState.EXPANDED
            null -> DemoAdapter.DataState.EXPANDED
        }

    private fun generateCollapsedData() = listOf(

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

    fun generateExpandedData() = listOf(

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

    fun generateAnimInfo(dataState: DemoAdapter.DataState) = listOf(

//                MyAdapter2.AnimInfo(expandedData, 0, 2, 2),
//                MyAdapter2.AnimInfo(expandedData, 1, 2, 1),
//                MyAdapter2.AnimInfo(expandedData, 2, 0, 1),
        CollapseAnimAdapter.CollapseAnimInfo(dataState.asBoolean, 3, 1, 2),
//                MyAdapter2.AnimInfo(expandedData, 4, 0, 1),
//
//                MyAdapter2.AnimInfo(expandedData, 5, 2, 2),
//                MyAdapter2.AnimInfo(expandedData, 6, 2, 1),
//                MyAdapter2.AnimInfo(expandedData, 7, 0, 1),
//                MyAdapter2.AnimInfo(expandedData, 8, 1, 2),
//                MyAdapter2.AnimInfo(expandedData, 9, 0, 1),
//
//                MyAdapter2.AnimInfo(expandedData, 10, 2, 2),
//                MyAdapter2.AnimInfo(expandedData, 11, 2, 1),
//                MyAdapter2.AnimInfo(expandedData, 12, 0, 1),
//                MyAdapter2.AnimInfo(expandedData, 13, 1, 2),
//                MyAdapter2.AnimInfo(expandedData, 14, 0, 1),
//
//                MyAdapter2.AnimInfo(expandedData, 15, 2, 2),
//                MyAdapter2.AnimInfo(expandedData, 16, 2, 1),
//                MyAdapter2.AnimInfo(expandedData, 17, 0, 1),
//                MyAdapter2.AnimInfo(expandedData, 18, 1, 2),
//                MyAdapter2.AnimInfo(expandedData, 19, 0, 1),
//
//                MyAdapter2.AnimInfo(expandedData, 20, 2, 2),
//                MyAdapter2.AnimInfo(expandedData, 21, 2, 1),
//                MyAdapter2.AnimInfo(expandedData, 22, 0, 1),
//                MyAdapter2.AnimInfo(expandedData, 23, 1, 2),
//                MyAdapter2.AnimInfo(expandedData, 24, 0, 1),
//
//                MyAdapter2.AnimInfo(expandedData, 25, 2, 2),
//                MyAdapter2.AnimInfo(expandedData, 26, 2, 1),
//                MyAdapter2.AnimInfo(expandedData, 27, 0, 1),
//                MyAdapter2.AnimInfo(expandedData, 28, 1, 2),
//                MyAdapter2.AnimInfo(expandedData, 29, 0, 1),
//
//                MyAdapter2.AnimInfo(expandedData, 30, 2, 2),
//                MyAdapter2.AnimInfo(expandedData, 31, 2, 1),
//                MyAdapter2.AnimInfo(expandedData, 32, 0, 1),
//                MyAdapter2.AnimInfo(expandedData, 33, 1, 2),
//                MyAdapter2.AnimInfo(expandedData, 34, 0, 1),
    )


    companion object {
        private const val DEBUG = true
        private const val DEBUG_ANIM_DURATION_MS = 2500L
    }

}