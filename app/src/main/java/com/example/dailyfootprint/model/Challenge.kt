package com.example.dailyfootprint.model

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class Challenge(
    val challengeCode : String,
    val challengeName : String,
    val challengeOwner : String,
    val position: ArrayList<Float> = arrayListOf(0.0F, 0.0F),
    val goal : Int,
    val successTime: ArrayList<Int> = arrayListOf(0, 0, 0, 0, 0, 0, 0)

): Serializable {
    constructor() : this("", "", "", arrayListOf(0.0F, 0.0F), 0, arrayListOf(0, 0, 0, 0, 0, 0, 0))
}

