package com.qtalk.sample.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller
import com.qtalk.sample.R
import com.qtalk.sample.data.Country
import kotlinx.android.synthetic.main.recycler_view_list_item.view.*

class AdvancedAdapter(private val mContext : Context?, private val countriesList : List<Country>, private val handleDrawable : Drawable?) : RecyclerView.Adapter<AdvancedAdapter.ViewHolder>()
        , RecyclerViewFastScroller.OnPopupViewUpdate {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.mTextView?.text = countriesList[position].countryName
    }

    override fun getItemCount() = countriesList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        return ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_view_list_item, parent, false))
    }

    override fun onUpdate(position: Int, popupTextView: TextView) {
        popupTextView.background.colorFilter = PorterDuffColorFilter(getColor(power = position),
                PorterDuff.Mode.SRC_IN)
       // todo update handle
        handleDrawable?.colorFilter = PorterDuffColorFilter(getColor( power = position),
                PorterDuff.Mode.SRC_IN)
        popupTextView.text = countriesList[position].countryName[0].toString()
    }

    private fun getColor(power: Int): Int {
        val H = (itemCount - power) * 100/itemCount
        val S = 1 // Saturation
        val V = 0.8 // Value
        return Color.HSVToColor(floatArrayOf(H.toFloat(), S.toFloat(), V.toFloat()))
    }

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val mTextView: TextView? = view.text_view
    }
}