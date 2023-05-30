# Collapse Item Animator

Custom RecyclerView animator based on DefaultItemAnimator.

## Integration
(for example see `collapse-item-animator-demo`)

* Define a background color for the view used to render list item
  (use `transparent` if no custom color is desired - `android:background="@android:color/transparent"`)

* `RecyclerView`'s `clipChildren` attribute
  ```
        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/recycler_view"
            android:clipChildren="false"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />
  ```
