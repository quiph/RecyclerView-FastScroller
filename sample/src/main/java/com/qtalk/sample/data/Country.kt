package com.qtalk.sample.data

import com.google.gson.annotations.SerializedName

data class Country (
        @SerializedName(CountryParams.COUNTRY_NAME) val countryName : String,
        @SerializedName(CountryParams.POPULATION) val population : Long
)
object CountryParams{
    const val COUNTRY_NAME = "country"
    const val POPULATION = "population"
}