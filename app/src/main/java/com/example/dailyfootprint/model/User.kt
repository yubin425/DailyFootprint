package com.example.dailyfootprint.model

data class User(
    val userCode : String,
    val userName : String,
    val succesData : Array<String>,
    val friendList : Array<String>
)
