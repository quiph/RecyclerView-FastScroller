package com.qtalk.sample.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller
import com.qtalk.sample.R
import com.qtalk.sample.adapters.AdvancedAdapter
import com.qtalk.sample.data.Country
import com.qtalk.sample.data.CountryParams
import kotlinx.android.synthetic.main.fragment_advanced.view.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import java.util.*

class AdvancedFragment : Fragment(){

    private val countriesList : ArrayList<Country> = ArrayList()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_advanced, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = AdvancedAdapter(activity, countriesList, with(view.fast_scroller as RecyclerViewFastScroller){
            handleDrawable
        })
        with(view){
           with(advanced_recycler_view){
               layoutManager = LinearLayoutManager(activity)
               this.adapter = adapter
               addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
           }
       }
        loadJSONFromAsset()
        countriesList.sort()
        adapter.notifyDataSetChanged()
    }

    // load dummy countries json
    // countries.json file outsourced from "https://github.com/samayo/country-json"
    private fun loadJSONFromAsset() {
        var json: String? = null
        try {
            val inputStream: InputStream? = activity?.assets?.open("countries.json")
            inputStream?.let {
                val size : Int  = it.available()
                val buffer = ByteArray(size)
                it.read(buffer)
                it.close()
                json = String(buffer, Charset.defaultCharset())
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        try {
            val obj = JSONArray(json)
            for (i in 0 until obj.length()) {
                parseObjectAndAddCountry(obj[i] as JSONObject)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
    private fun parseObjectAndAddCountry(jsonObject: JSONObject){
        countriesList.add(Country(jsonObject.getString(CountryParams.COUNTRY_NAME),jsonObject.getLong(CountryParams.POPULATION)))
    }
}