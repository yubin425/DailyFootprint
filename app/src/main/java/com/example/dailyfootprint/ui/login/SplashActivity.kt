package com.example.dailyfootprint.ui.login

import FirebaseManager.userDatabaseReference
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.example.dailyfootprint.MainActivity
import com.example.dailyfootprint.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class SplashActivity : AppCompatActivity() {
    private val SPLASH_DELAY: Long = 3000 // 3초
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        // 3초 후에 checkFirebaseAccount 함수 호출
        Handler().postDelayed({
            checkFirebaseAccount()
        }, SPLASH_DELAY)
    }

    fun gotoMainScreen() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    fun gotoStartScreen() {
        startActivity(Intent(this, StartActivity::class.java))
    }

    fun gotoSignUpScreen() {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    private fun checkFirebaseAccount() {

        val currentUser = FirebaseManager.getFirebaseAuthInstance().currentUser

        Log.d("MyApp", "이것은 디버그 메시지 1")
        if (currentUser != null) {
            // 이미 로그인한 사용자인 경우
            val uid = currentUser.uid

            // Realtime Database에서 사용자 정보 확인
            userDatabaseReference.child(uid).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Log.w("already exist","in user")
                        // 이미 등록된 사용자인 경우
                        gotoMainScreen()
                    } else {
                        Log.w("already exist","not in user")
                        // 등록되지 않은 사용자인 경우
                        gotoSignUpScreen()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // 데이터베이스 조회 실패 시 처리
                    Log.w("onCancelled","error")
                }
            })
        } else {
            // 로그인하지 않은 경우
            // 여기서 로그인 처리를 진행하고, 로그인 성공 후 위의 로직을 반복합니다.
            Log.w("not already exist","in user")
            gotoStartScreen()

        }

    }
}