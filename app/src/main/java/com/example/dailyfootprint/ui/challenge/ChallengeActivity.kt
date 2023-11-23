package com.example.dailyfootprint.ui.challenge

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.dailyfootprint.model.Challenge
import com.example.dailyfootprint.MainActivity
import com.example.dailyfootprint.R
import com.example.dailyfootprint.databinding.ActivityChallengeBinding

class ChallengeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChallengeBinding
    private var saveValues : ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChallengeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.challengeviewNameEdittext.setOnClickListener {
            checkChallengeName()
        }
        binding.challengeviewSearchButton.setOnClickListener {
            checkLocation()
        }
        binding.challengeviewSelectSpinner.setOnClickListener {
            checkSpinnerValue()
        }
        binding.challengeviewAddButton.setOnClickListener {
            saveValues()
            moveToNextView()
        }
    }

    private fun checkChallengeName() {
        val challengeName = binding.challengeviewNameEdittext.text.toString()

        if (challengeName.isNotEmpty()) {
            showToast("입력값: $challengeName")
        } else {
            showToast("이름을 확인하세요.")
        }
    }

    private fun checkLocation() {
        // GPS
    }

    private fun checkSpinnerValue() {
        val selectedValue = binding.challengeviewSelectSpinner.selectedItem.toString()

        if (selectedValue.isNotEmpty()) {
            //
        } else {
            showToast("수행 주기를 선택해주세요.")
        }
    }

    private fun saveValues() {
        val challengeName = binding.challengeviewNameEdittext.text.toString()
        val locationValue = binding.challengeviewLocationEdittext.text.toString()
        val spinnerValue = binding.challengeviewSelectSpinner.selectedItem.toString()

        saveValues.add("ChallengeName: $challengeName")
        saveValues.add("LocationValue: $locationValue")
        saveValues.add("SpinnerValue: $spinnerValue")
    }

    private fun moveToNextView() {
        // val intent = Intent(this@ChallengeActivity, NextActivity::class.java)
        // intent.putStringArrayListExtra("saveValues", saveValues)
        // startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}