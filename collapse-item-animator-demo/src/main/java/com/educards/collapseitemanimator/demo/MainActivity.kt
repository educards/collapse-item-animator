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
import android.view.LayoutInflater
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.educards.collapseitemanimator.ExpansionState
import com.educards.collapseitemanimator.CollapseItemAnimator
import com.educards.collapseitemanimator.DefaultStreamingNotifyExecutor
import com.educards.collapseitemanimator.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.inflate(layoutInflater, R.layout.activity_main, null, false)
    }

    private val availableTests = arrayOf(
        TestCaseControllerA(),
        TestCaseControllerB(),
        TestCaseControllerC(),
        TestCaseControllerD(),
        TestCaseControllerE(),
    )

    private lateinit var adapter: DemoAdapter

    private lateinit var testCaseController: TestCaseController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initRecyclerView()
        initSwitchButton()
        initTestCasesButtons()
        initAnimSpeedSlider()
        initTestNotifyExecutor()
    }

    private fun initRecyclerView() {

        adapter = DemoAdapter(this, binding.recyclerView)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.itemAnimator = CollapseItemAnimator(binding.recyclerView, adapter)
    }

    private fun initAdapterData() {
        updateNotifyExecutor(adapter, testCaseController, binding.notifyExecutorProd.isChecked)
        testCaseController.initOrSwitchAdapterData(adapter)
    }

    private fun initSwitchButton() {
        binding.switchButton.setOnClickListener {
            testCaseController.initOrSwitchAdapterData(adapter)
            updateSwitchButtonText(testCaseController.getNextActionTitle())
        }
    }

    private fun initTestCasesButtons() {

        // Instantiate RadioButton for each test case
        // and add it to layout.
        availableTests.forEach { testCaseController ->
            val testCaseRadioButton = LayoutInflater.from(this).inflate(R.layout.test_case_radio_button, null, false) as RadioButton
            testCaseRadioButton.id = testCaseController.viewId
            testCaseRadioButton.text = testCaseController.testCaseName
            binding.testCasesRadioGroup.addView(testCaseRadioButton)
        }

        // Update data when test case is switched.
        binding.testCasesRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            testCaseController = availableTests.first { it.viewId == checkedId }
            initAdapterData()
            updateSwitchButtonText(testCaseController.getNextActionTitle())
        }

        // Check the first test case (default).
        binding.testCasesRadioGroup.findViewById<RadioButton>(
            availableTests.first().viewId
        ).isChecked = true
    }

    private fun updateSwitchButtonText(animDir: ExpansionState) {
        binding.switchButton.text = if (animDir == ExpansionState.EXPANDED) "Expand" else "Collapse"
    }

    private fun initAnimSpeedSlider() {

        binding.animSpeedSlider.addOnChangeListener { slider, value, fromUser ->
            val itemAnimator = binding.recyclerView.itemAnimator
            if (itemAnimator is SimpleItemAnimator) {
                val animDurationMs = value.toLong()
                with(itemAnimator) {
                    changeDuration = animDurationMs
                    moveDuration = animDurationMs
                    removeDuration = animDurationMs
                    addDuration = animDurationMs
                }
            }
        }

        // setup init (default) value
        binding.animSpeedSlider.value = COLLAPSE_ANIM_DEFAULT_DURATION_MS
    }

    private fun initTestNotifyExecutor() {

        binding.notifyExecutorsRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            updateNotifyExecutor(adapter, testCaseController, checkedId == R.id.notify_executor_prod)
        }

        // Select prod by default
        binding.notifyExecutorsRadioGroup.findViewById<RadioButton>(
            R.id.notify_executor_prod
        ).isChecked = true
    }

    private fun updateNotifyExecutor(adapter: DemoAdapter, testCaseController: TestCaseController, prod: Boolean) {
        if (prod) {
            adapter.streamingNotifyExecutor = DefaultStreamingNotifyExecutor()
        } else {
            adapter.streamingNotifyExecutor = testCaseController.hardwiredNotifyExecutor
        }
    }

    companion object {
        private const val COLLAPSE_ANIM_DEFAULT_DURATION_MS = 450f
    }

}