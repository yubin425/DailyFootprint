package com.example.dailyfootprint.dao

import com.example.dailyfootprint.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UserDao {
    private var databaseReference: DatabaseReference? = null

    init {
        val db = FirebaseDatabase.getInstance()
        databaseReference = db.getReference("user")
    }

    fun add(user: User?): Task<Void> {
        return databaseReference!!.push().setValue(user)
    }
}