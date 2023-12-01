package com.example.dailyfootprint.ui.login

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.example.dailyfootprint.MainActivity
import com.example.dailyfootprint.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    var mGoogleSignInClient : GoogleSignInClient? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    fun googleLogin() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        googleLoginLauncher.launch(signInIntent)
    }

    var googleLoginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        Log.d(TAG, "여긴가")
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            getGoogleInfo(task)
            Log.d(TAG, "여긴가2")
        }
        else{
            Log.d(TAG, result.resultCode.toString())
        }
    }
    fun getGoogleInfo(completedTask: Task<GoogleSignInAccount>) {
        try {
            val TAG = "구글 로그인 결과"
            val account = completedTask.getResult(ApiException::class.java)
            Log.d(TAG, account.id!!)
            loginUser(account)
        }
        catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }

    private fun loginUser(acct: GoogleSignInAccount) {
        // val credential = GoogleAuthProvider.getCredential(acct.id, null)
        val idToken = acct.idToken
        when {
            idToken != null -> {
                // Got an ID token from Google. Use it to authenticate
                // with Firebase.
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val isNewUser = task.result?.additionalUserInfo?.isNewUser
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success")
                            if (isNewUser == true) {
                                // 새로운 사용자인 경우: 회원가입 화면으로 이동
                                startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
                            } else {
                                // 기존 사용자인 경우: 홈 화면으로 이동
                                Log.w("로그인 성공", "user")
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.exception)

                        }
                    }
            }
            else -> {
                // Shouldn't happen.
                Log.d(TAG, "No ID token!")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("886906283637-5363vlhbjnraj5ggbumv7ro815kjqfof.apps.googleusercontent.com")
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        Log.d(TAG, "googlesignin")
        auth = FirebaseAuth.getInstance()

        Log.d(TAG, "firebaseauth")
        binding.loginviewGoogleButton.setOnClickListener {
            googleLogin()

            Log.d(TAG, "엥?")
        }
    }
}