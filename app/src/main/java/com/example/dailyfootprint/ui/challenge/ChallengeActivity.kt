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
    private lateinit var latitude: String
    private lateinit var longitude: String
    private val TAG = "ChallengeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChallengeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "onCreate")

        // 초기화
        databaseReference = FirebaseDatabase.getInstance().reference

        // 수행 주기
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

        // MapsActivity.kt에서 위치 정보 전달 받음
        locationEditText = findViewById(R.id.challengeview_location_edittext)
        binding.challengeviewSearchButton.setOnClickListener {
            finish()
            val intent = Intent(this@ChallengeActivity, MapsActivity::class.java)
            startActivity(intent)
        }
        latitude = intent.getStringExtra("latitude").toString()
        longitude = intent.getStringExtra("longitude").toString()
        val name = intent.getStringExtra("name")
        locationEditText.setText(name)

        // 새로 작성 완료한 챌린지를 추가하기
        binding.challengeviewAddButton.isEnabled = true
        binding.challengeviewAddButton.setOnClickListener {
            if (validateInputs()) {
                saveValues()
            }
            Log.d(TAG, "ADD BUTTON CLICKED.")
        }

        // 챌린지 추가하는 것을 취소하고 이전 뷰로 되돌아가기
        binding.challengeviewCancelButton.setOnClickListener {
            finish()
            Log.d(TAG, "CANCEL BUTTON CLICKED.")
        }
    }

    // Edittext를 확인
    private fun validateInputs(): Boolean {
        val challengeName = binding.challengeviewNameEdittext.text.toString()
        val location = binding.challengeviewLocationEdittext.text.toString()

        if (challengeName.isEmpty() && location.isEmpty()) {
            Toast.makeText(this, "이름과 위치를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (challengeName.isEmpty()) {
            Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (location.isEmpty()) {
            Toast.makeText(this, "위치를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun saveValues() {
        val challengeName = binding.challengeviewNameEdittext.text.toString()
        val locationValue = binding.challengeviewLocationEdittext.text.toString()
        val spinnerValue = binding.challengeviewSelectSpinner.selectedItem.toString()

        // String을 Int로 변환
        fun convertStringToInt(spinnerValue: String): Int {
            return when (spinnerValue) {
                "주 1일" -> 1
                "주 2일" -> 2
                "주 3일" -> 3
                "주 4일" -> 4
                "주 5일" -> 5
                "주 6일" -> 6
                "주 7일" -> 7

                else -> 0
            }
        }

        // 정보를 challenge에 저장
        val newChallenge = Challenge(
            challengeCode = UUID.randomUUID().toString(),
            challengeName = challengeName,
            challengeOwner = FirebaseManager.getUID(),
            location = locationValue,
            position = arrayListOf(latitude.toFloat(), longitude.toFloat()),
            goal = convertStringToInt(spinnerValue),
            successTime = arrayListOf(0, 0, 0,  0, 0, 0, 0)
        )
        val challengeRef = databaseReference.child("challenges")
        challengeRef.child(newChallenge.challengeCode).setValue(newChallenge)
        finish()
    }
}