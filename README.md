# Collapse Item Animator

Custom RecyclerView animator based on DefaultItemAnimator.

## Integration Steps
(For working example see [collapse-item-animator-demo](https://github.com/educards/collapse-item-animator/tree/main/collapse-item-animator-demo).)

* Make your `Adapter` extend `CollapseAnimAdapter`.
* Make sure, that together with your custom data you also provide and set animation
  metadata which tells the animator, whether your custom data represent
  expanded or collapse state of the animation.
  * see [CollapseAnimAdapter.setAnimInfo(...)](https://github.com/educards/collapse-item-animator/blob/main/collapse-item-animator/src/main/java/com/educards/collapseitemanimator/CollapseAnimAdapter.kt) methods
  * for usage, see [DemoAdapter.setData(...)](https://github.com/educards/collapse-item-animator/blob/main/collapse-item-animator-demo/src/main/java/com/educards/collapseitemanimator/demo/DemoAdapter.kt)

* Define a background color for the view used to render list item
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
