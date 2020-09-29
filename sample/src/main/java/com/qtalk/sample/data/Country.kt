package com.qtalk.sample.data

import kotlinx.serialization.Serializable

@Serializable
data class Country(
    val country: String,
    val population: Long
) : Comparable<Country> {
    override fun compareTo(other: Country): Int =
        this.population.compareTo(other.population)
}