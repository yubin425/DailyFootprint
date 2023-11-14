package com.example.dailyfootprint.model

data class User(
    val userCode : String,
    val userName : String,
    val succesData : ArrayList<String>,
    val friendList : ArrayList<String>
)
