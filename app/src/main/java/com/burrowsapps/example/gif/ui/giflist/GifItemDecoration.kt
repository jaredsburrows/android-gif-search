package com.burrowsapps.example.gif.ui.giflist

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView ItemDecoration for custom space divider.
 */
class GifItemDecoration(
  private val offSet: Int,
  private val columns: Int
) : RecyclerView.ItemDecoration() {
  override fun getItemOffsets(
    outRect: Rect,
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State
  ) {
    super.getItemOffsets(outRect, view, parent, state)

    val position = parent.getChildLayoutPosition(view)
    val dataSize = state.itemCount

    // Apply inner right
    if (position % columns < columns - 1) outRect.right = offSet

    // Apply inner left
    if (position % columns > 0) outRect.left = offSet

    // Apply top padding
    if (position < columns) {
      outRect.bottom = 0 // Make the top of the RecyclerView have no padding
    } else {
      outRect.top = offSet
    }

    // Apply bottom padding
    if (position >= dataSize || position >= dataSize - columns) {
      outRect.bottom = 0 // Make the bottom of the RecyclerView have no padding
    } else {
      outRect.bottom = offSet
    }
  }
}
