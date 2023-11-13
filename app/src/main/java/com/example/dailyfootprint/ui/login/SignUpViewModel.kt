package com.example.dailyfootprint.ui.login

import FirebaseManager.firebaseDatabase
import FirebaseManager.userDatabaseReference
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dailyfootprint.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class SignUpViewModel : ViewModel() {
    private var _name = MutableLiveData<String>()
    private var _isDuplicate = MutableLiveData<Boolean>()
    private var _isValid = MutableLiveData<Boolean>()
    private val _isUserAdded = MutableLiveData<Boolean>()

    val isUserAdded: LiveData<Boolean>
        get() = _isUserAdded
    val name : LiveData<String>
        get() = _name

    val isDuplicate : LiveData<Boolean>
        get() = _isDuplicate

    val isValid : LiveData<Boolean>
        get() = _isValid

    init {
        _name.value = ""
        _isDuplicate.value = true
        _isValid.value = false
        _isUserAdded.value = false
    }

    fun checkDuplicate(input : String) {
        // 버튼을 클릭하면 이 함수 실행됨
        userDatabaseReference.orderByChild("name").equalTo(input)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.childrenCount.toInt() == 0) {
                        // 이름 사용 가능
                        _isDuplicate.value = false
                        _name.value = input
                        Log.w("name is able",input)
                    } else {
                        // 이름이 이미 존재함
                        // 여기에서 사용자에게 메시지 표시 등의 작업 수행
                        _isDuplicate.value = true
                        Log.w("name is not able",input)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // 쿼리 중 오류 발생
                    // 여기에서 오류 처리 작업 수행
                    Log.w("error :",error.details)
                }
            })

    }

    fun checkValid(input : String) {
        // editText 에서 입력받은 string이 유효한지 확인 (빈칸, 10자 이상, 특수문자 일 경우에는 idValid 값을 false로)
        _isValid.value = !input.isBlank() && input.length >= 10 && !containsSpecialCharacter(input)
        // 새로운 텍스트가 입력되었다는 뜻이기 때문에 중복확인 초기화
        _isValid.value = true
        Log.w("name is valid", input)
    }

    private fun containsSpecialCharacter(input: String): Boolean {
        // Implement your logic to check if the input contains special characters.
        // Return true if it contains special characters, false otherwise.
        // You can customize this based on your validation criteria.
        val regex = Regex("[!@#\$%^&*(),.?\":{}|<>]")
        return regex.containsMatchIn(input)
    }

    fun addUser() {
        val uid = FirebaseManager.getFirebaseAuthInstance().uid
        if(uid != null) {
            val newUser = User (
                userCode = uid,
                userName = _name.value!!,
                succesData = ArrayList<String>(),
                friendList = ArrayList<String>()
            )
            userDatabaseReference.child(uid).setValue(newUser)
                .addOnCompleteListener {
                    _isUserAdded.value = true
                    Log.w("add", "success")
                }
                .addOnFailureListener {
                    _isUserAdded.value = false
                    Log.w("add", "fail")
                }
        }
        Log.w("add", "error")


    }



}