package com.example.dailyfootprint.ui.challenge

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.dailyfootprint.databinding.ActivityChallengeBinding
import com.example.dailyfootprint.model.Challenge
import com.example.dailyfootprint.R
import com.google.firebase.database.*

class ChallengeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChallengeBinding
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChallengeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.periods,
            android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.challengeviewSelectSpinner.adapter = spinnerAdapter

        binding.challengeviewNameEdittext.addTextChangedListener {
            updateAddButtonState()
        }
        binding.challengeviewLocationEdittext.addTextChangedListener {
            updateAddButtonState()
        }
        binding.challengeviewSearchButton.setOnClickListener {
            //
        }
        binding.challengeviewAddButton.setOnClickListener {
            saveValues()
        }
    }

    private fun updateAddButtonState() {
        val challengeName = binding.challengeviewNameEdittext.text.toString()
        val location = binding.challengeviewLocationEdittext.text.toString()
        val isSearchButtonEnabled = location.isNotEmpty()

        binding.challengeviewSearchButton.isEnabled = isSearchButtonEnabled

        val isAddButtonEnabled = challengeName.isNotEmpty() && isSearchButtonEnabled
        binding.challengeviewAddButton.isEnabled = isAddButtonEnabled
    }

    private fun saveValues() {
        val challengeName = binding.challengeviewNameEdittext.text.toString()
        val locationValue = binding.challengeviewLocationEdittext.text.toString()
        val spinnerValue = binding.challengeviewSelectSpinner.selectedItem.toString()

        if (challengeName.isNotEmpty()) {
            val challengeRef = databaseReference.child("challenge")
            val newChallengeRef = challengeRef.push()
            val newChallenge = Challenge(
                challengeName = challengeName,
                challengeLocation = locationValue,
                goal = spinnerValue
            )

            newChallengeRef.setValue(newChallenge)
                .addOnSuccessListener {
                    showToast("챌린지가 추가되었습니다.")
                    finish()
                }
                .addOnFailureListener {
                    showToast("챌린지 추가에 실패했습니다.")
                }
        } else {
            showToast("챌린지 이름을 입력해주세요.")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}