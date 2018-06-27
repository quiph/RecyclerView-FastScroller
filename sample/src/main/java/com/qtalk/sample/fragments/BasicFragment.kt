package com.qtalk.sample.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller
import com.qtalk.sample.R
import com.qtalk.sample.adapters.BasicAdapter
import kotlinx.android.synthetic.main.fragment_basic.view.*

class BasicFragment : Fragment(){

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mRecyclerViewAdapter : BasicAdapter
    private lateinit var mLayoutManager : RecyclerView.LayoutManager
    private lateinit var mFastScroller : RecyclerViewFastScroller

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_basic, container, false)
        mRecyclerView = view.basic_recycler_view
        mFastScroller = view.fast_scroller
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            mLayoutManager = LinearLayoutManager(activity)
            mRecyclerViewAdapter = BasicAdapter(activity)
            mRecyclerView.layoutManager = mLayoutManager
            mRecyclerView.adapter = mRecyclerViewAdapter
    }
}