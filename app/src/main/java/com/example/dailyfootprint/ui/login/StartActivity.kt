package com.example.dailyfootprint.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.dailyfootprint.R
import com.example.dailyfootprint.databinding.ActivityMainBinding
import com.example.dailyfootprint.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("MyApp", "이것은 디버그 메시지2 inflate")
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startviewStartButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)

            Log.d("MyApp", "이것은 디버그 메시지3 startActivity")
            startActivity(intent)
        }
    }
}