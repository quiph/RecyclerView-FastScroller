package com.qtalk.sample.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller
import com.qtalk.sample.R
import com.qtalk.sample.adapters.ProgrammingLanguagesAdapter
import kotlinx.android.synthetic.main.fragment_programming_languages.view.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.Locale

class ProgrammingLanguagesFragment : Fragment() {

    private val programmingLanguages: List<String> by lazy {
        Json.decodeFromString<List<String>>(
            requireActivity().assets.open("programming_languages.json").use {
                it.reader().readText()
            }
        ).sortedBy { it.toLowerCase(Locale.ROOT) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_programming_languages, container, false).apply {

            recyclerView.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )

            fab.setOnClickListener {
                with(fastScroller) {
                    val tmp = handleWidth
                    handleWidth = handleHeight
                    handleHeight = tmp

                    fastScrollDirection =
                        if (fastScrollDirection == RecyclerViewFastScroller.FastScrollDirection.HORIZONTAL) {
                            recyclerView.layoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                            RecyclerViewFastScroller.FastScrollDirection.VERTICAL
                        } else {
                            recyclerView.layoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                            RecyclerViewFastScroller.FastScrollDirection.HORIZONTAL
                        }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.recyclerView.adapter = ProgrammingLanguagesAdapter(programmingLanguages)
    }
}
