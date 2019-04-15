package com.demoss.mypaint.util.recycler

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.RecyclerView

class SimpleSwipeItemCallback(
    private val onItemSwipeLeft: ((Int) -> Unit)? = null,
    private val onItemSwipeRight: ((Int) -> Unit)? = null
) : ItemTouchHelper.SimpleCallback(
    0, when {
        onItemSwipeLeft != null && onItemSwipeRight == null -> LEFT
        onItemSwipeLeft == null && onItemSwipeRight != null -> RIGHT
        else /*onItemSwipeLeft != null && onItemSwipeRight != null*/-> LEFT or RIGHT
    }
) {

    fun constructor() {

    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        when (direction) {
            ItemTouchHelper.RIGHT -> onItemSwipeRight?.invoke(viewHolder.adapterPosition)
            ItemTouchHelper.LEFT -> onItemSwipeLeft?.invoke(viewHolder.adapterPosition)
        }
    }
}