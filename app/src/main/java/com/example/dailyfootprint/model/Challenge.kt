package com.example.dailyfootprint.model

data class Challenge(
    val challengeCode : String,
    val challengeName : String,
    val challengeOwner : String,
    val position : Array<Float> = Array(2) { 0.0F },
    val goal : Int,
    val successTime : Array<Int> = Array(7) {0}
)
