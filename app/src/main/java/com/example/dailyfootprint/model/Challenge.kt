package com.example.dailyfootprint.model

data class Challenge(
    val challengeCode: String = "",
    val challengeName: String = "",
    val challengeOwner: String = "",
    val location: String = "",
    val position: List<Float> = List(2) { 0.0F },
    val goal: Int = 0,
    val successTime: List<Int> = List(7) {0}
)
