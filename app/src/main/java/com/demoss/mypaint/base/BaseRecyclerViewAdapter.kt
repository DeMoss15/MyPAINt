package com.demoss.mypaint.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<T, VH : BaseRecyclerViewAdapter.BaseViewHolder<T>> : RecyclerView.Adapter<VH>() {

    abstract val viewHolderFactory: (view: View) -> VH
    abstract val layoutResId: Int
    abstract var differ: AsyncListDiffer<T>

    // RV Adapter functions ============================================================================================
    final override fun getItemCount(): Int = differ.currentList.size

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = viewHolderFactory(
        LayoutInflater.from(parent.context)
            .inflate(layoutResId, parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bindData(differ.currentList[position])

    // DiffUtil applying ===============================================================================================
    fun dispatchData(list: List<T>) = differ.submitList(list)

    // Abstract classes ================================================================================================
    abstract class BaseDiffUtilItemCallback<T> : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem
    }

    abstract class BaseViewHolder<T>(val view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bindData(item: T)
    }
}