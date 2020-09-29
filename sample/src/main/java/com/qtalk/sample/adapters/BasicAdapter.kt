package com.qtalk.sample.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller
import com.qtalk.sample.R
import kotlinx.android.synthetic.main.recycler_view_list_item.view.*

class BasicAdapter(private val mContext: Context?) :
    RecyclerView.Adapter<BasicAdapter.ViewHolder>(), RecyclerViewFastScroller.OnPopupTextUpdate {

    override fun onChange(position: Int): CharSequence {
        return (position + 1).toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_view_list_item_center, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mTextView.text = (position + 1).toString()
    }

    override fun getItemCount() = 100

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mTextView: TextView = view.text_view
    }
}