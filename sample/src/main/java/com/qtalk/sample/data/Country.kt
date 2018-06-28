package com.qtalk.sample.data

import com.google.gson.annotations.SerializedName

data class Country (
        @SerializedName(CountryParams.COUNTRY_NAME) val countryName : String,
        @SerializedName(CountryParams.POPULATION) val population : Long
) : Comparable<Country>{
    override fun compareTo(other: Country): Int {
        return when{
            // same
            other.population == this.population->{
                 0
             }
            // this is greater
            other.population < this.population ->{
                return 1
            }
            // this is lesser
            else -> return  -1
        }
    }

}
object CountryParams{
    const val COUNTRY_NAME = "country"
    const val POPULATION = "population"
}