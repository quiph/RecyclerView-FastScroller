package com.qtalk.sample.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.qtalk.sample.R
import com.qtalk.sample.adapters.BasicAdapter
import kotlinx.android.synthetic.main.fragment_basic.view.*
import kotlinx.coroutines.*


class BasicFragment : Fragment() {

    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    private var swipeJob: Job? = null

    private var horizontalDividerItemDecoration: DividerItemDecoration? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_basic, container, false).apply {
            fab.setOnClickListener {
                if (basic_recycler_view.layoutManager is GridLayoutManager) {
                    basic_recycler_view.layoutManager = LinearLayoutManager(context)
                    basic_recycler_view.removeItemDecoration(horizontalDividerItemDecoration!!)
                    fab.setImageResource(R.drawable.ic_action_view_as_grid)
                } else {
                    basic_recycler_view.layoutManager = GridLayoutManager(context, 2)
                    basic_recycler_view.addItemDecoration(horizontalDividerItemDecoration!!)
                    fab.setImageResource(R.drawable.ic_action_view_as_list)
                }
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        horizontalDividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL)

        with(view) {
            with(this.basic_recycler_view) {
                adapter = BasicAdapter(activity)
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }

            with(this.swipe_refresh_layout) {
                swipeRefreshLayout = this
                setOnRefreshListener {
                    swipeJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(3000)

                        if (swipeRefreshLayout?.isRefreshing == true)
                            swipeRefreshLayout?.isRefreshing = false
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        swipeJob?.cancel()
        super.onDestroyView()
    }
}