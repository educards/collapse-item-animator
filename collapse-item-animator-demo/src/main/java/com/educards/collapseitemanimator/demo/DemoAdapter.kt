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

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.educards.collapseitemanimator.AnimInfo
import com.educards.collapseitemanimator.ItemAnimInfo
import com.educards.collapseitemanimator.ExpansionState
import com.educards.collapseitemanimator.CollapseAnimAdapter
import com.educards.collapseitemanimator.CollapseAnimFrameLayout
import com.educards.collapseitemanimator.CollapseAnimViewHolder
import com.educards.collapseitemanimator.DefaultStreamingNotifyExecutor
import com.educards.collapseitemanimator.StreamingNotifyExecutor
import java.util.SortedSet
import java.util.TreeMap
import java.util.TreeSet

class DemoAdapter(

    context: Context,

    /**
     * A cyclic reference to [RecyclerView] which
     * is used to access view holders based
     * on their adapter position (see [findViewHolderForAdapterPosition]).
     */
    private val recyclerView: RecyclerView

) : RecyclerView.Adapter<DemoAdapter.ViewHolder>(),
    CollapseAnimAdapter {

    init {
        setupStableIds()
    }

    private val highlightBackgroundColor =
        ResourcesCompat.getColor(context.resources, R.color.colorMayaBlueDarker, context.theme)
    private val highlightForegroundColor =
        Color.BLACK

    /**
     * Single-purpose offscreen instance of [TextView] type of [ViewHolder]
     * to make measurements in [onBindViewHolder].
     */
    private lateinit var offscreenTextViewHolder: ViewHolder

    private var data: List<String>? = null

    override val itemAnimInfoMap = TreeMap<Int, ItemAnimInfo>()

    /**
     * Animation metadata ([itemAnimInfoMap]) is by design only "animation scoped".
     *
     * However, in this demo we want to highlight lines which stay visible
     * during each phase of collapse/expand animation.
     * Therefore in this field we keep the copy of [itemAnimInfoMap] which
     * is used in [onBindViewHolder] to highlight aforementioned lines.
     *
     * @see AnimInfo.collapsedStateVisibleFirstLine
     * @see AnimInfo.collapsedStateVisibleLinesCount
     */
    private val persistentAnimInfoMap = TreeMap<Int, AnimInfo>()

    override var dataExpansionState: ExpansionState? = null

    override var previousItemCount = -1

    override var streamingNotifyExecutor: StreamingNotifyExecutor = DefaultStreamingNotifyExecutor()

    override val suppressNextAnimCycleSet: SortedSet<Int> = TreeSet()

    override fun findViewHolderForAdapterPosition(position: Int) =
        recyclerView.findViewHolderForAdapterPosition(position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rootView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false) as CollapseAnimFrameLayout
        return ViewHolder(rootView, rootView.findViewById(R.id.text_view))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        onBindPostTransitionItemAnimInfo(holder, position)

        val spannableString = SpannableString(data?.get(position))

        val animInfo = persistentAnimInfoMap[position]
        if (animInfo != null) {

            // highlight animated lines
            when (dataExpansionState) {
                ExpansionState.EXPANDED -> {
                    val layout = getOffscreenLayoutForText(spannableString)
                    spannableString.setSpan(
                        BackgroundColorSpan(highlightBackgroundColor),
                        layout.getLineStart(animInfo.collapsedStateVisibleFirstLine),
                        layout.getLineEnd(animInfo.collapsedStateVisibleFirstLine + animInfo.collapsedStateVisibleLinesCount - 1),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannableString.setSpan(
                        ForegroundColorSpan(highlightForegroundColor),
                        layout.getLineStart(animInfo.collapsedStateVisibleFirstLine),
                        layout.getLineEnd(animInfo.collapsedStateVisibleFirstLine + animInfo.collapsedStateVisibleLinesCount - 1),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                ExpansionState.COLLAPSED -> {
                    spannableString.setSpan(BackgroundColorSpan(highlightBackgroundColor), 0, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableString.setSpan(ForegroundColorSpan(highlightForegroundColor), 0, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                else -> {
                    error("Unhandled case [$dataExpansionState]")
                }
            }
        }

        holder.textView.text = spannableString
    }

    private fun getOffscreenLayoutForText(text: CharSequence): android.text.Layout {
        // prepare "offscreen" view just for sake to perform measurements
        if (!this::offscreenTextViewHolder.isInitialized) {
            offscreenTextViewHolder = onCreateViewHolder(recyclerView, -1)
        }
        // bind data
        offscreenTextViewHolder.textView.text = text
        // measure (this will initialize 'layout' of the child)
        recyclerView.layoutManager?.measureChild(offscreenTextViewHolder.itemView, 0, 0)
        // set background span based on measurements
        return offscreenTextViewHolder.textView.layout
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    protected fun setupStableIds() {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int) =
        super<CollapseAnimAdapter>.getItemId(position)

    fun setData(
        data: List<String>,
        dataExpansionState: ExpansionState,
        itemAnimInfoList: List<ItemAnimInfo>?
    ) {
        onPreData(dataExpansionState)
        this.data = data
        setItemAnimInfoList(itemAnimInfoList)
        notifyAfterDataSet()
    }

    override fun setItemAnimInfoList(itemAnimInfoList: List<ItemAnimInfo>?) {
        super.setItemAnimInfoList(itemAnimInfoList)

        persistentAnimInfoMap.clear()
        itemAnimInfoList?.forEach { itemAnimInfo ->
            persistentAnimInfoMap[itemAnimInfo.itemIndexPostTransition] = itemAnimInfo.animInfo
        }
    }

    open class ViewHolder(
        override val rootView: CollapseAnimFrameLayout,
        override val textView: TextView,
    ) : RecyclerView.ViewHolder(rootView),
        CollapseAnimViewHolder {
        override var viewExpansionState: ExpansionState? = null
        override var itemAnimInfo: ItemAnimInfo? = null
    }

}