package com.example.dailyfootprint.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import com.example.dailyfootprint.MainActivity
import com.example.dailyfootprint.R
import com.example.dailyfootprint.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private val viewModel by viewModels<SignUpViewModel>()
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 텍스트가 변경될때마다 유효한지 확인
        binding.singupviewNameEdittext.addTextChangedListener {
            viewModel.checkValid(binding.singupviewNameEdittext.text.toString())
        }

        // 중복확인 버튼을 누르고 중복인지 확인
        binding.singupviewDuplicateButton.setOnClickListener {
            viewModel.isDuplicate(binding.singupviewNameEdittext.text.toString())
        }

        binding.singupviewSignupButton.setOnClickListener {
            viewModel.addUser()
        }

        // 중복확인 값에 따라 버튼 활성화,비활성화
        viewModel.isDuplicate.observe(this) { data ->
            if(!data) {
                binding.singupviewSignupButton.isEnabled = true
                binding.singupviewSignupButton.setBackgroundResource(R.drawable.round_green_button)
            } else {
                binding.singupviewSignupButton.isEnabled = false
                binding.singupviewSignupButton.setBackgroundResource(R.drawable.round_gray_button)
            }
        }

        // 유효한 값인지에 따라 중복확인 버튼 활성화, 비활성화
        viewModel.isValid.observe(this) { data ->
            if(data) {
                binding.singupviewDuplicateButton.isEnabled = true
                binding.singupviewDuplicateButton.setBackgroundResource(R.drawable.round_green_button)
            } else {
                binding.singupviewDuplicateButton.isEnabled = false
                binding.singupviewDuplicateButton.setBackgroundResource(R.drawable.round_gray_button)
            }
        }

        viewModel.isUserAdded.observe(this) { isSuccess ->
            if (isSuccess) {
                // addUser가 성공했을 때 다음 화면으로 이동하는 코드를 여기에 추가합니다.
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // addUser가 실패했을 때의 처리를 여기에 추가합니다.
                // 예를 들면, 실패 메시지를 표시하거나 다른 작업을 수행할 수 있습니다.
            }
        }
    }
}