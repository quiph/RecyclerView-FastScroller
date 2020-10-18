package com.qtalk.sample.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller
import com.qtalk.sample.R
import kotlinx.android.synthetic.main.recycler_view_list_item.view.*

class ProgrammingLanguagesAdapter(private val list: List<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    RecyclerViewFastScroller.OnPopupTextUpdate
{

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.text_view.text = list[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_list_item, parent, false))
    }

    override fun getItemCount(): Int =
        list.size

    override fun onChange(position: Int): CharSequence =
        list[position].first().toUpperCase().toString()

}