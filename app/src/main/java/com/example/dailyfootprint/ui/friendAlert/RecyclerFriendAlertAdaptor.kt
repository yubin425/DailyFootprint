package com.example.dailyfootprint.ui.friendAlert

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyfootprint.databinding.AlertlistItemBinding
import com.example.dailyfootprint.model.Friend

class RecyclerFriendAlertAdaptor(private val viewModel: FriendAlertViewModel) : RecyclerView.Adapter<RecyclerFriendAlertAdaptor.ViewHolder>() {

    var items = ArrayList<FriendAlertInfo>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AlertlistItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding, viewModel)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    // 각 항목에 필요한 기능을 구현
    class ViewHolder(val binding: AlertlistItemBinding,val viewModel: FriendAlertViewModel) : RecyclerView.ViewHolder(binding.root) {
        fun bind(friendAlertInfo : FriendAlertInfo) {
            binding.friendalertListName.text = friendAlertInfo.friendName

            binding.friendalertListAcceptButton.setOnClickListener {
                // 친구 수락 버튼을 눌렀을 경우
                viewModel.acceptFriend(friendAlertInfo.requestFriend)
            }

            binding.friendalertListRejectButton.setOnClickListener {
                // 친구 거절 버튼을 눌렀을 경우
                viewModel.rejectFriend(friendAlertInfo.requestFriend)
            }
        }
    }
}