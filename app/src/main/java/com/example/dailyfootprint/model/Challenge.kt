package com.example.dailyfootprint.model

import androidx.annotation.Keep

@Keep
data class Challenge(
    val challengeCode : String,
    val challengeName : String,
    val challengeOwner : String,
    //val position : Array<Float> = Array(2) { 0.0F },
    val position: ArrayList<Float> = arrayListOf(0.0F, 0.0F),
    val goal : Int,
    //val successTime : Array<Int> = Array(7) {0}
    val successTime: ArrayList<Int> = arrayListOf(0, 0, 0, 0, 0, 0, 0)

) {
    constructor() : this("", "", "", arrayListOf(0.0F, 0.0F), 0, arrayListOf(0, 0, 0, 0, 0, 0, 0))
}

