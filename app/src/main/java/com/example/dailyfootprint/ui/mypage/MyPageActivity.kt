package com.example.dailyfootprint.ui.mypage

import FirebaseManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.dailyfootprint.R
import com.example.dailyfootprint.databinding.ActivityMyPageBinding
import com.example.dailyfootprint.ui.login.StartActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MyPageActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMyPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mypageviewLogoutButton.setOnClickListener {
            // 로그아웃 버튼 클릭
            // 로그아웃 처리
            FirebaseManager.authInstance.signOut()
            // StartActivity로 이동
            navigateToStartActivity()
        }

        binding.mypageviewQuitButton.setOnClickListener {
            val currentUser = FirebaseManager.authInstance.currentUser
            if (currentUser != null) {
                deleteUserAccount(currentUser.uid)
            }
        }


    }

    private fun deleteUserAccount(uid: String) {
        // 현재 사용자의 User 객체 삭제
        FirebaseManager.databaseReference.child("user").child(uid).removeValue()

        // 다른 모든 사용자의 friendList에서 현재 사용자의 UID 제거
        FirebaseManager.databaseReference.child("user").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    val friendList = userSnapshot.child("friendList").children.toList()
                    if (friendList.any { it.getValue(String::class.java) == uid }) {
                        val updatedList = friendList.filter { it.getValue(String::class.java) != uid }
                            .map { it.getValue(String::class.java)!! }
                        userSnapshot.ref.child("friendList").setValue(updatedList)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 오류 처리
            }
        })

        // 파이어베이스 인증에서 사용자 계정 삭제
        FirebaseManager.authInstance.currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // 탈퇴 성공, StartActivity로 이동
                navigateToStartActivity()
            } else {
                // 탈퇴 실패, 에러 메시지 표시
                Toast.makeText(this, "계정 탈퇴에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun navigateToStartActivity() {
        val intent = Intent(this, StartActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}