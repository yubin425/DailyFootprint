package com.example.dailyfootprint.ui.challenge

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.dailyfootprint.databinding.ActivityChallengeBinding
import com.example.dailyfootprint.model.Challenge
import com.example.dailyfootprint.R
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.database.*

class ChallengeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChallengeBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var placesClient: PlacesClient
    private lateinit var locationEditText: EditText

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

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_api_key))
        }
        placesClient = Places.createClient(this)

        locationEditText = findViewById(R.id.challengeview_location_edittext)
        binding.challengeviewSearchButton.setOnClickListener {
            performPlaceAPI()
        }

        binding.challengeviewNameEdittext.addTextChangedListener {
            updateAddButtonState()
        }
        binding.challengeviewLocationEdittext.addTextChangedListener {
            updateAddButtonState()
        }

        binding.challengeviewAddButton.setOnClickListener {
            saveValues()
        }
        binding.challengeviewCancelButton.setOnClickListener {
            finish()
        }
    }

    private fun updateAddButtonState() {
        val challengeName = binding.challengeviewNameEdittext.text.toString()
        val location = binding.challengeviewLocationEdittext.text.toString()

        val isAddButtonEnabled = challengeName.isNotEmpty() && location.isNotEmpty()
        binding.challengeviewAddButton.isEnabled = isAddButtonEnabled
    }

    private fun performPlaceAPI() {
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(this)

        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    companion object {
        const val AUTOCOMPLETE_REQUEST_CODE = 1001
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.let {
                val place = Autocomplete.getPlaceFromIntent(it)
                binding.challengeviewLocationEdittext.setText(place.address)
                /*
                val mapIntent = Intent(this, MapsActivity::class.java)
                mapIntent.putExtra("placeName", place.name)
                mapIntent.putExtra("placeAddress", place.address)

                startActivity(mapIntent)
                 */
            }
        }
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