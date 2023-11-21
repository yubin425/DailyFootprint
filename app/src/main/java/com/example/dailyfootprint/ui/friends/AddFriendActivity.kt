package com.example.dailyfootprint.ui.friends

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.dailyfootprint.databinding.ActivityAddFriendBinding

class AddFriendActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFriendBinding

    private val persons = arrayOf("John", "Jane", "Doe", "Alice", "Bob")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchButton.setOnClickListener {
            val searchText = binding.searchEditText.text.toString()
            if (searchPerson(searchText)) {
                binding.searchResultName.text = searchText
                binding.searchResult.visibility = View.VISIBLE
                binding.searchFailedText.visibility = View.GONE
            } else {
                binding.searchResult.visibility = View.GONE
                binding.searchFailedText.visibility = View.VISIBLE
            }
        }
    }

    private fun searchPerson(name: String): Boolean {
        for (person in persons) {
            if (person.equals(name, ignoreCase = true)) {
                return true
            }
        }
        return false
    }
}