package com.example.dailyfootprint.ui.friends

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dailyfootprint.databinding.ActivityAddFriendBinding

class AddFriendActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFriendBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}