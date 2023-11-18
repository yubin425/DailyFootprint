package com.example.dailyfootprint.ui.friendAlert

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.dailyfootprint.R
import com.example.dailyfootprint.databinding.ActivityFriendAlertBinding
import com.example.dailyfootprint.databinding.ActivitySignUpBinding
import com.example.dailyfootprint.ui.friendAlert.FriendAlertViewModel

class FriendAlertActivity : AppCompatActivity() {
    private val viewModel by viewModels<FriendAlertViewModel>()
    private lateinit var binding: ActivityFriendAlertBinding
    private lateinit var adaptor: RecyclerFriendAlertAdaptor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendAlertBinding.inflate(layoutInflater)
        adaptor = RecyclerFriendAlertAdaptor(viewModel)
        binding.friendalertAlertList.adapter = adaptor
        // LiveData 관찰 및 어댑터 업데이트
        viewModel.friendAlertList.observe(this, { friendAlertList ->
            adaptor.items = friendAlertList
            adaptor.notifyDataSetChanged() // 어댑터에 데이터가 변경되었음을 알림
        })

        binding.testButton.setOnClickListener {
            viewModel.testFunction()
        }


        setContentView(binding.root)
    }
}