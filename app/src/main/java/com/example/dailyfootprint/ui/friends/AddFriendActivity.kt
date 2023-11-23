package com.example.dailyfootprint.ui.friends

import FirebaseManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.dailyfootprint.databinding.ActivityAddFriendBinding
import com.example.dailyfootprint.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class AddFriendActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFriendBinding

    private val persons = arrayOf("John", "Jane", "Doe", "Alice", "Bob")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var myName : String? = ""

        val userId = FirebaseManager.getUID()
        FirebaseManager.getName(userId,
            callback = { userName ->
                // 데이터 가져오기 성공 시 호출
                myName = userName
                binding.cardViewUserName.text = userName
            },
            onError = { error ->
                // 데이터 가져오기 실패 시 호출
                Log.w("Error: ", error.message)
            }
        )



        binding.searchButton.setOnClickListener {
            val searchText = binding.searchEditText.text.toString()
            // 검색한 이름이 본인의 이름일 경우
            if (searchText == myName) {
                binding.searchResult.visibility = View.GONE
                binding.searchFailedText.visibility = View.VISIBLE
            }
            else {
                // Firebase에 검색한 이름이 존재하는지 확인
                searchPerson(searchText,
                    callback = { isMatched ->
                        if (isMatched) {
                            binding.searchResultName.text = searchText
                            binding.searchResult.visibility = View.VISIBLE
                            binding.searchFailedText.visibility = View.GONE
                        }
                        else {
                            binding.searchResult.visibility = View.GONE
                            binding.searchFailedText.visibility = View.VISIBLE
                        }
                    },
                    onError = {error ->
                        Log.w("Error: ", error.message)
                    }
                )
            }

            /*if (searchPerson(searchText)) {
                binding.searchResultName.text = searchText
                binding.searchResult.visibility = View.VISIBLE
                binding.searchFailedText.visibility = View.GONE
            } else {
                binding.searchResult.visibility = View.GONE
                binding.searchFailedText.visibility = View.VISIBLE
            }*/
        }
    }

    /*private fun searchPerson(searchName: String, callback: (String) -> Unit, onError: (DatabaseError) -> Unit) {
        val userRef = FirebaseManager.userDatabaseReference // 변경해야 할 경로
        Log.w("userRef:", userRef.toString())

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userCodes = mutableListOf<String>()

                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    user?.let {
                        if (searchName == it.userName) {
                            callback(it.userName)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 데이터를 읽어오는 데 실패하거나 취소된 경우 호출됩니다.
                onError(databaseError)
            }
        })
    }*/
    private fun searchPerson(searchName: String, callback: (Boolean) -> Unit, onError: (DatabaseError) -> Unit) {
        val userRef = FirebaseManager.userDatabaseReference // 변경해야 할 경로
        Log.w("userRef:", userRef.toString())

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userCodes = mutableListOf<String>()
                var isMatched = false

                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    user?.let {
                        if (searchName == it.userName) {
                            isMatched = true
                        }
                    }
                }

                callback(isMatched)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onError(databaseError)
            }
        })
    }
}