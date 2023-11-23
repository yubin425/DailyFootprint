package com.example.dailyfootprint.ui.friendAlert

import FirebaseManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dailyfootprint.model.Friend
import com.example.dailyfootprint.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.random.Random

class FriendAlertViewModel : ViewModel(){
    private var _friendAlertList = MutableLiveData<ArrayList<FriendAlertInfo>>()

    val friendAlertList : LiveData<ArrayList<FriendAlertInfo>>
        get() = _friendAlertList

    init {
        getFriendAlertList()
    }

    fun getFriendAlertList() {
        // 서버에서 값 불러오기
        Log.w("getFriendAlertList","함수 들어옴")
        val userId = FirebaseManager.getUID()
        val friendRequestsRef = FirebaseManager.databaseReference.child("friend")
        Log.w("getFriendAlertList : UID",userId)
        friendRequestsRef.orderByChild("receiveFriend").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val friendRequests = mutableListOf<Friend>()
                    snapshot.children.forEach {
                        it.getValue(Friend::class.java)?.let { request ->
                            friendRequests.add(request)
                        }
                    }

                    // 각 친구 요청에 대해 사용자 이름 가져오기
                    val friendAlertInfoList = ArrayList<FriendAlertInfo>()
                    friendRequests.forEach { request ->
                        getUserInfo(request.requestFriend) { userName ->
                            friendAlertInfoList.add(FriendAlertInfo(request.requestFriend, userName))
                            // LiveData 업데이트
                            _friendAlertList.value = friendAlertInfoList
                            Log.w("getFriendAlertList","list added")
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // 오류 처리
                    Log.w("getFriendAlertList","error")
                    Log.w("getFriendAlertList error detail",databaseError.details)
                }
            })
    }

    private fun getUserInfo(userCode: String, callback: (String) -> Unit) {
        val userRef = FirebaseManager.userDatabaseReference.child("$userCode/userName")
        Log.w("getUserInfo",userCode)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.w("getUserInfo snapshot: ", snapshot.toString())
                val userName = snapshot.getValue(String::class.java)
                Log.w("getUserInfo name : ", userName.toString())
                callback(userName.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 오류 처리
                Log.w("getUserInfo error : ",databaseError.details)
            }
        })
    }

    fun acceptFriend(friendCode : String) {
        // 현재 ArrayList의 값을 가져온다
        val currentList = friendAlertList.value ?: arrayListOf()
        val uid = FirebaseManager.getUID()

        // 입력받은 friendCode와 일치하는 요소를 찾아 삭제
        currentList.removeAll { it.requestFriend == friendCode }

        // LiveData 업데이트
        _friendAlertList.value = currentList


        // 파이어베이스에서 친구 요청 정보 업데이트
        val databaseReference = FirebaseManager.databaseReference.child("friend")
        updateFriendRequest(databaseReference, uid, friendCode)

        // 각 사용자의 친구 목록에 상대방 추가
        addFriendToUser(uid, friendCode)
        addFriendToUser(friendCode, uid)
    }

    private fun updateFriendRequest(databaseRef: DatabaseReference, receiveFriend: String, requestFriend: String) {
        databaseRef.orderByChild("requestFriend").equalTo(requestFriend)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val friendRequest = childSnapshot.getValue(Friend::class.java)
                        if (friendRequest?.receiveFriend == receiveFriend) {
                            childSnapshot.ref.removeValue()
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // 오류 처리
                }
            })
    }

    private fun addFriendToUser(userId: String, friendId: String) {
        val userRef = FirebaseManager.userDatabaseReference.child("$userId/friendList")
        userRef.push().setValue(friendId)
            .addOnSuccessListener {
                // 성공적으로 추가됨
            }
            .addOnFailureListener {
                // 실패 처리
            }
    }

    fun rejectFriend(friendCode : String) {
        // 로컬 정보를 업데이트
        // 현재 ArrayList의 값을 가져온다
        val currentList = friendAlertList.value ?: arrayListOf()

        // 입력받은 friendCode와 일치하는 요소를 찾아 삭제
        currentList.removeAll { it.requestFriend == friendCode }

        // LiveData 업데이트
        _friendAlertList.value = currentList

        // 파이어베이스에서 삭제
        val databaseReference = FirebaseManager.databaseReference.child("friend")
        databaseReference.orderByChild("requestFriend").equalTo(friendCode)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val friendRequest = childSnapshot.getValue(Friend::class.java)
                        if (friendRequest?.receiveFriend == FirebaseManager.getUID()) {
                            childSnapshot.ref.removeValue()
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // 오류 처리
                }
            })
    }


}

data class FriendAlertInfo(
    val requestFriend : String,
    val friendName : String
)