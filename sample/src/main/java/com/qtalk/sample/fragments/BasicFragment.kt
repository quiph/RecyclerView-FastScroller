package com.qtalk.sample.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.qtalk.sample.R
import com.qtalk.sample.adapters.BasicAdapter
import kotlinx.android.synthetic.main.fragment_basic.view.*
import kotlinx.coroutines.*

class BasicFragment : Fragment() {

    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    private var swipeJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_basic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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