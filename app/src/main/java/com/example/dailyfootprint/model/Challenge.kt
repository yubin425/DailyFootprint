package com.example.dailyfootprint.model

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class Challenge(
    val challengeCode : String,
    val challengeName : String,
    val challengeOwner : String,
    val location : String,
    val position: ArrayList<Double> = arrayListOf(0.0, 0.0),
    val goal : Int,
    val successTime: ArrayList<Int> = arrayListOf(0, 0, 0, 0, 0, 0, 0)

): Serializable {
    constructor() : this("", "", "","", arrayListOf(0.0, 0.0), 0, arrayListOf(0, 0, 0, 0, 0, 0, 0))
}

