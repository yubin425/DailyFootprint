package com.example.dailyfootprint.ui.friends

import FirebaseManager
import FirebaseManager.databaseReference
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dailyfootprint.databinding.ActivityAddFriendBinding
import com.example.dailyfootprint.model.Friend
import com.example.dailyfootprint.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class AddFriendActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFriendBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var myName : String? = ""
        var searchName : String? = ""

        val userId = FirebaseManager.getUID()
        getName(userId,
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
            searchName = binding.searchEditText.text.toString()
            // 검색한 이름이 본인의 이름일 경우
            if (searchName == myName) {
                binding.searchResult.visibility = View.GONE
                binding.searchFailedText.visibility = View.VISIBLE
            }
            else {
                // Firebase에 검색한 이름이 존재하는지 확인
                searchPerson(searchName!!,
                    callback = { isMatched ->
                        if (isMatched) {
                            binding.searchResultName.text = searchName
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

        }

        binding.requestButton.setOnClickListener {
            val otherName = searchName.toString()
            getUserCode(otherName,
                callback = {searchCode ->
                    requestFriend(userId, searchCode.toString(),
                        callback = {isDone ->
                            if (isDone == 1) {
                                Toast.makeText(this, "친구를 요청했습니다.", Toast.LENGTH_SHORT).show()
                            }
                            else if (isDone == 2) {
                                Toast.makeText(this, "이미 친구인 상태입니다.", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                Toast.makeText(this, "친구 수락 대기 중입니다.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onError = {error ->
                            Log.w("Error: ", error.message)
                        })
                },
                onError = {error ->
                    Log.w("Error: ", error.message)
                }
            )
        }
    }

    private fun getName(userCode: String, callback: (String?) -> Unit, onError: (DatabaseError) -> Unit) {
        val userRef = databaseReference.child("user/$userCode/userName")

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.getValue(String::class.java)
                callback(userName.toString())

            }

            override fun onCancelled(databaseError: DatabaseError) {
                onError(databaseError)
            }
        })
    }

    private fun searchPerson(searchName: String, callback: (Boolean) -> Unit, onError: (DatabaseError) -> Unit) {
        val userRef = FirebaseManager.userDatabaseReference // 변경해야 할 경로

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var isMatched = false
                // user 리스트 탐색
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

    private fun getUserCode(userCode: String, callback: (String?) -> Unit, onError: (DatabaseError) -> Unit) {
        val userRef = FirebaseManager.userDatabaseReference // 변경해야 할 경로

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // user 리스트 탐색
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    user?.let {
                        // 이름과 일치하는 유저를 찾을 경우 해당 유저 코드 전달
                        if (userCode == it.userName) {
                            callback(it.userCode)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onError(databaseError)
            }
        })
    }

    private fun requestFriend(myCode: String, friendCode: String, callback: (Int?) -> Unit, onError: (DatabaseError) -> Unit) {
        val friendsRef = databaseReference.child("friends")
        var alreadySent = false
        var alreadyFriend = false

        friendsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // friends 리스트 탐색
                for (friendSnapshot in snapshot.children) {
                    val friend = friendSnapshot.getValue(Friend::class.java)
                    friend?.let {
                        // 이미 친구 관계이거나 친구 수락 대기 중인 관계에 대한 처리
                        if ((myCode == it.requestFriend && friendCode == it.receiveFriend) || (myCode == it.receiveFriend && friendCode == it.requestFriend)) {
                            // 이미 친구 관계인 경우
                            if (it.isAccept == true) {
                                alreadyFriend = true
                                callback(2)
                            }
                            // 친구 수락 대기 중인 경우
                            else {
                                callback(3)
                            }
                        }
                    }
                }
                // 친구 요청을 보내는 단계
                if (!alreadySent && !alreadyFriend) {
                    val newFriendRef = friendsRef.push()
                    val newFriend = Friend (
                    requestFriend = myCode,
                    receiveFriend = friendCode,
                    isAccept = false
                    )
                    newFriendRef.setValue(newFriend)
                        .addOnSuccessListener {
                            // 데이터 추가 성공
                            val newFriendId = newFriendRef.key
                            Log.w("add: ", "success")
                        }
                        .addOnFailureListener {
                            // 데이터 추가 실패
                            Log.w("add: ", "fail")
                        }
                    callback(1)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onError(databaseError)
            }
        })
    }
}