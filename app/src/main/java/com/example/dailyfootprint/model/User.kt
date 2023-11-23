package com.example.dailyfootprint.model

data class User(
    val userCode : String,
    val userName : String,
    val successData : ArrayList<String>,
    val friendList : ArrayList<String>
)
