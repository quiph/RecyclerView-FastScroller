package com.qtalk.sample.adapters

import android.content.Context
import android.provider.ContactsContract
import android.support.v7.widget.RecyclerView
import android.telephony.PhoneNumberUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller
import com.qtalk.sample.R
import kotlinx.android.synthetic.main.recycler_view_list_item.view.*
import java.util.regex.Pattern

class ContactsAdapter(private val mContext: Context?, private val mNameList: List<String>) : RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder>(),RecyclerViewFastScroller.OnPopupTextUpdate
{
    override fun onChange(position: Int): CharSequence {
        return if(mNameList[position].matches(Patterns.PHONE.toRegex()))
            "#"
        else
            mNameList[position][0].toString().toUpperCase()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        return ContactsAdapter.ContactsViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_view_list_item, parent, false))
    }

    override fun getItemCount(): Int {
       return mNameList.size
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        holder.mTextView.text = mNameList.get(position)

    }

    class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mTextView: TextView = itemView.text_view
    }

}