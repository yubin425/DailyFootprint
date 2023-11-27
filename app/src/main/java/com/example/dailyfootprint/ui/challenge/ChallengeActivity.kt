package com.example.dailyfootprint.ui.challenge

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.dailyfootprint.databinding.ActivityChallengeBinding
import com.example.dailyfootprint.model.Challenge
import com.example.dailyfootprint.R
import com.example.dailyfootprint.ui.Map.MapsActivity
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.database.*
import java.util.UUID

class ChallengeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChallengeBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var placesClient: PlacesClient
    private lateinit var locationEditText: EditText
    private lateinit var challenge: Challenge
    private val TAG = "ChallengeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChallengeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "onCreate")

        // 초기화
        databaseReference = FirebaseDatabase.getInstance().reference

        val spinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.periods,
            android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.challengeviewSelectSpinner.adapter = spinnerAdapter

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyB_7LSzaKbT7-EhBo7-qzl6APfc7uFczfs")
        }
        placesClient = Places.createClient(this)

        locationEditText = findViewById(R.id.challengeview_location_edittext)
        binding.challengeviewSearchButton.setOnClickListener {
            finish()
            val intent = Intent(this@ChallengeActivity, MapsActivity::class.java)
            startActivity(intent)
        }
        val name = intent.getStringExtra("name")
        locationEditText.setText(name)

        binding.challengeviewNameEdittext.addTextChangedListener {
            updateAddButtonState()
        }
        binding.challengeviewLocationEdittext.addTextChangedListener {
            updateAddButtonState()
        }
        binding.challengeviewAddButton.setOnClickListener {
            saveValues()
            Log.d(TAG, "ADD BUTTON CLICKED.")
        }
        binding.challengeviewCancelButton.setOnClickListener {
            finish()
            Log.d(TAG, "CANCEL BUTTON CLICKED.")
        }
    }

    private fun updateAddButtonState() {
        val challengeName = binding.challengeviewNameEdittext.text.toString()
        val location = binding.challengeviewLocationEdittext.text.toString()

        val isAddButtonEnabled = challengeName.isNotEmpty() && location.isNotEmpty()
        binding.challengeviewAddButton.isEnabled = isAddButtonEnabled
    }



    private fun saveValues() {
        val challengeName = binding.challengeviewNameEdittext.text.toString()
        val locationValue = binding.challengeviewLocationEdittext.text.toString()
        val spinnerValue = binding.challengeviewSelectSpinner.selectedItem.toString()

        fun convertStringToInt(spinnerValue: String): Int {
            return when (spinnerValue) {
                "주 1일" -> 1
                "주 2일" -> 2
                "주 3일" -> 3
                "주 4일" -> 4
                "주 5일" -> 5
                "주 6일" -> 6
                "주 7일" -> 7

                else ->0
            }
        }
        val intSpinnerValue = convertStringToInt(spinnerValue)

        //val challengeKey = databaseReference.child("challenges").push().key
        if (challengeName.isNotEmpty()) {
            val newChallenge = Challenge(
                challengeCode = UUID.randomUUID().toString(),
                challengeName = challengeName,
                challengeOwner = FirebaseManager.getUID(),
                location = locationValue,
                position = arrayListOf(37.7749F, -122.4194F),     // <-- 실제 좌표값 넣도록 수정해야 함
                goal = convertStringToInt(spinnerValue),
                successTime = arrayListOf(0, 0, 0, 0, 0, 0, 0)
            )
            val challengeRef = databaseReference.child("challenges")
            challengeRef.child(newChallenge.challengeCode).setValue(newChallenge)
            finish()
        } else {
            showToast("챌린지 이름을 입력해주세요.")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}