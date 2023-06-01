# Collapse Item Animator

[![GitHub release](https://img.shields.io/github/v/release/educards/collapse-item-animator?include_prereleases&style=flat-square)](https://github.com/educards/collapse-item-animator/releases)
[![GitHub license](https://img.shields.io/github/license/educards/collapse-item-animator?style=flat-square)](https://github.com/educards/collapse-item-animator/blob/main/LICENSE)

Custom [RecyclerView.ItemAnimator](https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.ItemAnimator) based on `DefaultItemAnimator`.

## Integration Steps

(For working example see [collapse-item-animator-demo](https://github.com/educards/collapse-item-animator/tree/main/collapse-item-animator-demo).)

* Make your `Adapter` extend `CollapseAnimAdapter`.

* As part of your data setting procedure:
  1. Invoke one of the [CollapseAnimAdapter.setCollapseAnimInfo(...)](https://github.com/educards/collapse-item-animator/blob/main/collapse-item-animator/src/main/java/com/educards/collapseitemanimator/CollapseAnimAdapter.kt) methods.
     This tells the [animator](https://github.com/educards/collapse-item-animator/blob/main/collapse-item-animator/src/main/java/com/educards/collapseitemanimator/CollapseItemAnimator.kt),
     details about the animation such as whether your current data are in expanded or collapsed target state of the animation.
     (example: [DemoAdapter.setData(...)](https://github.com/educards/collapse-item-animator/blob/main/collapse-item-animator-demo/src/main/java/com/educards/collapseitemanimator/demo/DemoAdapter.kt)).
  2. Invoke one of `Adapter.notify*Changed()` method for those items (positions/indices)
     which were also enumerated in `animInfo` metadata of the preceding `setCollapseAnimInfo(animInfo)` call.

     ```
     // Example:

     class MyActivity ... {

         override fun onCreate(savedInstanceState: Bundle?) {

             // Perform the collapse animation of item #4.
             // Collapse to the first line while keeping 2 lines visible.
             val animInfoList = listOf(CollapseAnimInfo(
                     expanded = false,
                     itemIndex = 3,
                     firstLineIndex = 0,
                     linesCount = 2
                 )
             )

             val myData = ... // acquire adapter data

             // Update adapter data and perform collapse animation
             // for dedicated items.
             myAdapter.setData(myData, animInfo)
         }

     }

     class MyAdapter : CollapseAnimAdapter() {

         fun setData(data: List<...>, animInfoList: List<CollapseAnimInfo>?) {
             this.data = data
             setCollapseAnimInfo(animInfoList)

             // Correctly notify according to changes in your data.
             // Calling one of notifyItem*Changed(*) methods is required
             // for those items which are listed in `animInfoList`.
             // Otherwise the collapse animation might not be performed correctly.
             // For this task consider using [DiffUtil](https://developer.android.com/reference/androidx/recyclerview/widget/DiffUtil).

             TODO notify*(*)
         }

     }
     ```

* In the aforementioned anim metadata specify the target state of the collapse animation
  ([CollapseAnimAdapter.CollapseAnimInfo](https://github.com/educards/collapse-item-animator/blob/main/collapse-item-animator/src/main/java/com/educards/collapseitemanimator/CollapseAnimAdapter.kt)).

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
