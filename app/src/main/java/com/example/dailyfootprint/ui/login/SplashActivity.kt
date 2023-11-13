package com.example.dailyfootprint.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.dailyfootprint.MainActivity
import com.example.dailyfootprint.R
import com.google.firebase.auth.FirebaseAuth

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

    private fun checkFirebaseAccount() {
        val firebaseManager = FirebaseManager.getFirebaseAuthInstance()

        // 파이어베이스 계정 정보 확인 로직 추가
        if (firebaseManager.getCurrentUser() != null) {
            // 계정이 있으면 홈 화면으로 이동
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // 계정이 없으면 초기 화면으로 이동
            startActivity(Intent(this, StartActivity::class.java))
        }

        finish() // 현재 액티비티 종료
    }
}