# Collapse Item Animator

Custom RecyclerView animator based on DefaultItemAnimator.

## Integration Steps
(For working example see [collapse-item-animator-demo](https://github.com/educards/collapse-item-animator/tree/main/collapse-item-animator-demo).)

* Make your `Adapter` extend `CollapseAnimAdapter`.
* Make sure, that data setter of your custom `Adapter` provide and set also animation
  metadata which tells the [animator](https://github.com/educards/collapse-item-animator/blob/main/collapse-item-animator/src/main/java/com/educards/collapseitemanimator/CollapseItemAnimator.kt),
  whether your current data represent expanded or collapsed target state of the collapse/expand animation.
  * You need to invoke: [CollapseAnimAdapter.setAnimInfo(...)](https://github.com/educards/collapse-item-animator/blob/main/collapse-item-animator/src/main/java/com/educards/collapseitemanimator/CollapseAnimAdapter.kt) methods
  * For example see: [DemoAdapter.setData(...)](https://github.com/educards/collapse-item-animator/blob/main/collapse-item-animator-demo/src/main/java/com/educards/collapseitemanimator/demo/DemoAdapter.kt)

* Define a `View` which will be used as a list item in `onCreateViewHolder(...)` and `onBindViewHolder(...)` methods of your `Adapter`.
  Your custom `View` needs to implement `CollapseAnimView` interface and invoke its default methods correctly.
  * Use predefined [CollapseAnimFrameLayout](https://github.com/educards/collapse-item-animator/blob/main/collapse-item-animator/src/main/java/com/educards/collapseitemanimator/CollapseAnimFrameLayout.kt)
    if `FrameLayout` is suitable as a base for your list item.
  * Or create custom implementation of `CollapseAnimView` if `FrameLayout` does not suit you.

* Define a background color for the `View` used as a list item
  (use `transparent` if no custom color is desired - `android:background="@android:color/transparent"`).
  ```
        <com.educards.collapseitemanimator.CollapseAnimFrameLayout
            android:id="@+id/item"
            android:background="@android:color/transparent"
            android:layout_width="match_parent"
            android:layout_height="wrap_content">

            <!-- child views here -->

        </com.educards.collapseitemanimator.CollapseAnimFrameLayout>
  ```

* `RecyclerView`'s `clipChildren` attribute
  ```
        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/recycler_view"
            android:clipChildren="false"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />
  ```
