package com.qtalk.sample.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller
import com.qtalk.sample.R
import com.qtalk.sample.adapters.AdvancedAdapter
import com.qtalk.sample.data.Country
import kotlinx.android.synthetic.main.fragment_advanced.view.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class AdvancedFragment : Fragment() {

    private val countriesList: List<Country> by lazy {
        Json.decodeFromString<List<Country>>(
            activity?.assets?.open("countries.json")?.use { it.reader().readText() }
                ?: error("Cannot read asset")
        ).sorted()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_advanced, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = AdvancedAdapter(
            activity,
            countriesList,
            with(view.fastScroller as RecyclerViewFastScroller) {
                handleDrawable
            })

        with(view.advanced_recycler_view) {
            this.adapter = adapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        adapter.notifyDataSetChanged()
    }
}