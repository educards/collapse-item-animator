# Collapse Item Animator

[![GitHub release](https://img.shields.io/github/v/release/educards/collapse-item-animator?include_prereleases&style=flat-square)](https://github.com/educards/collapse-item-animator/releases)
[![GitHub license](https://img.shields.io/github/license/educards/collapse-item-animator?style=flat-square)](https://github.com/educards/collapse-item-animator/blob/main/LICENSE)

Custom [RecyclerView.ItemAnimator](https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.ItemAnimator) based on `DefaultItemAnimator`.

## Integration Steps

(For working example see [collapse-item-animator-demo](https://github.com/educards/collapse-item-animator/tree/main/collapse-item-animator-demo).)

* Make your `Adapter` implement `CollapseAnimAdapter`.
  For example implementation see [DemoAdapter](https://github.com/educards/collapse-item-animator/blob/main/collapse-item-animator-demo/src/main/java/com/educards/collapseitemanimator/demo/DemoAdapter.kt).

* As part of your `Adapter`'s data setting procedure:
  1. Invoke [CollapseAnimAdapter.notifyBeforeDataSet()](https://github.com/educards/collapse-item-animator/blob/main/collapse-item-animator/src/main/java/com/educards/collapseitemanimator/CollapseAnimAdapter.kt)
  2. Set new data
  3. Invoke [CollapseAnimAdapter.setItemAnimInfo(...)](https://github.com/educards/collapse-item-animator/blob/main/collapse-item-animator/src/main/java/com/educards/collapseitemanimator/CollapseAnimAdapter.kt).
     This tells the [CollapseItemAnimator](https://github.com/educards/collapse-item-animator/blob/main/collapse-item-animator/src/main/java/com/educards/collapseitemanimator/CollapseItemAnimator.kt),
     how to perform collapse-expand animation of your items.
  4. Invoke [CollapseAnimAdapter.notifyAfterDataSet()](https://github.com/educards/collapse-item-animator/blob/main/collapse-item-animator/src/main/java/com/educards/collapseitemanimator/CollapseAnimAdapter.kt)

* Define a `View` which will be used as a list item by `onCreateViewHolder(...)` and `onBindViewHolder(...)` methods of your `Adapter`.
  Your `View` needs to implement [CollapseAnimView](https://github.com/educards/collapse-item-animator/blob/main/collapse-item-animator/src/main/java/com/educards/collapseitemanimator/CollapseAnimView.kt) interface and invoke its default methods correctly.
  Here you have two options:
  * Use predefined [CollapseAnimFrameLayout](https://github.com/educards/collapse-item-animator/blob/main/collapse-item-animator/src/main/java/com/educards/collapseitemanimator/CollapseAnimFrameLayout.kt)
    if `FrameLayout` is suits you as a base for your list item.
  * Or create custom implementation of `CollapseAnimView` if `FrameLayout` does not suit you.

* Define a background color for your list item `View`
  (use `transparent` if no custom color is desired - `android:background="@android:color/transparent"`,
  for details see issue https://github.com/educards/collapse-item-animator/issues/1).

  ```
        <com.educards.collapseitemanimator.CollapseAnimFrameLayout
            android:id="@+id/item"
            android:background="@android:color/transparent"
            android:layout_width="match_parent"
            android:layout_height="wrap_content">

            <!-- child views here -->

        </com.educards.collapseitemanimator.CollapseAnimFrameLayout>
  ```

* Unset `clipChildren` of your `RecyclerView`.
  ```
        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/recycler_view"
            android:clipChildren="false"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />
  ```

* Define `collapseAnimClipOffsetY` (optional).
  Collapse animation is performed by "clipping" and positioning the `Bitmap` of list item view
  in its expanded state. If your list item have vertical paddings/margins set, use the `collapseAnimClipOffsetY`
  to position the clipping window properly with respect to these paddings/margins.
  Value of `collapseAnimClipOffsetY` should match the sum of all vertical paddings/margins.
  ```
        <com.educards.collapseitemanimator.CollapseAnimFrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
            xmlns:app="http://schemas.android.com/apk/res-auto"
            xmlns:tools="http://schemas.android.com/tools"
            android:background="@android:color/transparent"
            app:collapseAnimClipOffsetY="@dimen/list_item_vertical_padding"    <====
            android:layout_width="match_parent"
            android:layout_height="wrap_content">
            <TextView
                android:id="@+id/text_view"
                android:text="text"
                android:ellipsize="end"
                android:paddingVertical="@dimen/list_item_vertical_padding"    <====
                android:gravity="center_vertical"
                android:layout_width="match_parent"
                android:layout_height="wrap_content" />
        </com.educards.collapseitemanimator.CollapseAnimFrameLayout>
  ```

## Design constraints

### Support of homogenous ExpansionState

The current design of `CollapseAnimAdapter` supports 2 states (`ExpansionState`s) of its data:
1. all items are expanded
2. all items are collapsed
 
It doesn't matter which of `Adapter` items are animated (animation with `CollapseItemAnimator` is
optionally defined by providing `ItemAnimInfo` to `Adapter.setData(...)`). The point here is, that
2 items with different `ExpansionState` can't exist during a single call to `Adapter.setData(...)`.

In other words items with different states can't be mixed so that *the whole set of items* is
at any given time considered `collapsed` or `expanded`.

For details see:
* `CollapseAnimAdapter.expansionState`
* `CollapseAnimAdapter.setAnimInfo`

## License
```
Copyright 2023 Educards Learning, SL

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
