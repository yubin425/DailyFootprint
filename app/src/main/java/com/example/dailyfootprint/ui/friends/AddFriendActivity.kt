package com.example.dailyfootprint.ui.friends

import FirebaseManager
import FirebaseManager.databaseReference
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dailyfootprint.R
import com.example.dailyfootprint.databinding.ActivityAddFriendBinding
import com.example.dailyfootprint.model.Friend
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
        var requestId : String? = ""

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

        // 검색 버튼을 눌렀을 때의 동작
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
                        // Firebase에 등록된 이름일 경우
                        if (isMatched) {
                            binding.searchResultName.text = searchName
                            binding.searchResult.visibility = View.VISIBLE
                            binding.searchFailedText.visibility = View.GONE
                            // 해당 사용자의 코드 가져오기
                            getUserCode(searchName.toString(),
                                callback = {searchCode ->
                                    requestId = searchCode
                                    // 친구 요청을 보내도 되는 사용자인지 확인
                                    checkIsRequest(userId, searchCode.toString(),
                                        callback = {isDone ->
                                            // 이미 친구 관계인 경우
                                            if (isDone == 2) {
                                                Toast.makeText(this, "이미 친구인 상태입니다.", Toast.LENGTH_SHORT).show()
                                                requestButtonDisable()
                                            }
                                            // 친구요청 수락을 하지 않은 경우
                                            else if (isDone == 3) {
                                                Toast.makeText(this, "친구 수락 대기 중입니다.", Toast.LENGTH_SHORT).show()
                                                requestButtonDisable()
                                            }
                                            // 친구 요청을 보내도 되는 경우
                                            else {
                                                requestButtonEnable()
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
                        // Firebase에 등록되지 않은 이름인 경우
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

        // 친구요청 버튼을 눌렀을 때의 동작
        binding.requestButton.setOnClickListener {
            requestFriend(userId, requestId.toString())
            Toast.makeText(this, "친구 요청을 보냈습니다.", Toast.LENGTH_SHORT).show()
            requestButtonDisable()
        }
    }

    // 친구요청 버튼 활성화 함수
    private fun requestButtonEnable() {
        binding.requestButton.isEnabled = true
        binding.requestButton.setBackgroundResource(R.drawable.round_green_button)
    }

    // 친구요청 버튼 비활성화 함수
    private fun requestButtonDisable() {
        binding.requestButton.isEnabled = false
        binding.requestButton.setBackgroundResource(R.drawable.round_gray_button)
    }

    // 사용자 코드를 입력받은 뒤 사용자의 이름을 가져오는 함수
    private fun getName(userCode: String, callback: (String?) -> Unit, onError: (DatabaseError) -> Unit) {
        val userNameRef = databaseReference.child("user/$userCode/userName")

        userNameRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.getValue(String::class.java)
                callback(userName.toString())

            }

            override fun onCancelled(databaseError: DatabaseError) {
                onError(databaseError)
            }
        })
    }

    // 입력받은 이름이 Firebase에 등록된 이름인지 확인하는 함수
    private fun searchPerson(searchName: String, callback: (Boolean) -> Unit, onError: (DatabaseError) -> Unit) {
        val userRef = FirebaseManager.userDatabaseReference

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var isMatched = false
                // user 리스트 탐색
                for (userSnapshot in snapshot.children) {
                    val userName = userSnapshot.child("userName").getValue(String::class.java)
                    if (searchName == userName) {
                        isMatched = true
                    }
                }
                callback(isMatched)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onError(databaseError)
            }
        })
    }

    // 사용자의 이름을 입력받은 뒤 사용자의 코드를 가져오는 함수
    private fun getUserCode(userName: String, callback: (String?) -> Unit, onError: (DatabaseError) -> Unit) {
        val userRef = FirebaseManager.userDatabaseReference

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // user 리스트 탐색
                for (userSnapshot in snapshot.children) {
                    val nameInFirebase = userSnapshot.child("userName").getValue(String::class.java)
                    val codeInFirebase = userSnapshot.child("userCode").getValue(String::class.java)

                    // 이름과 일치하는 유저를 찾을 경우 해당 유저 코드 전달
                    if (userName == nameInFirebase) {
                        callback(codeInFirebase)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onError(databaseError)
            }
        })
    }

    // 친구 요청을 보내도 되는지 확인하는 함수
    private fun checkIsRequest(myCode: String, friendCode: String, callback: (Int?) -> Unit, onError: (DatabaseError) -> Unit) {
        val friendsRef = databaseReference.child("friends")
        // 친구 수락 대기 중인지, 이미 친구 관계인지 확인한 뒤에 친구 요청을 보내기 위한 변수. 각 검사가 끝날 때마다 checkCount값이 1 증가한다.

        friendsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // friends 리스트 탐색 - 친구 수락 대기 중인 관계 처리
                for (friendSnapshot in snapshot.children) {
                    val request = friendSnapshot.child("requestFriend").getValue(String::class.java)
                    val receive = friendSnapshot.child("receiveFriend").getValue(String::class.java)

                    if ((myCode == request && friendCode == receive) || (myCode == receive && friendCode == request)) {
                        Log.w("request", "waiting")
                        callback(3)
                        return
                    }
                }
                // user 탐색 - 이미 친구 관계일 경우 처리
                val userRef = databaseReference.child("user/${FirebaseManager.getUID()}/friendList")
                userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (userFriendListSnapshot in snapshot.children) {
                            val friendCodeInFirebase = userFriendListSnapshot.getValue(String::class.java)
                            if (friendCode == friendCodeInFirebase) {
                                Log.w("already", "friend")
                                callback(2)
                                return
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.w("fail: ", "fail to search friendList")
                    }
                })
                callback(1)
            }


            override fun onCancelled(databaseError: DatabaseError) {
                onError(databaseError)
            }
        })
    }

    // Firebase에 친구 요청 데이터를 전송하는 함수
    private fun requestFriend(myCode: String, friendCode: String) {
        val friendsRef = databaseReference.child("friends")
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
        Log.w("add: ", "error")
    }
}