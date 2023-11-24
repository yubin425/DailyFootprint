package com.example.dailyfootprint.ui.challenge

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dailyfootprint.R
import com.example.dailyfootprint.databinding.FragmentChallengeBinding

class ChallengeFragment : Fragment(R.layout.fragment_challenge) {

    private lateinit var binding: FragmentChallengeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChallengeBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.cancelButton.setOnClickListener {
            val intent = Intent(activity, ChallengeActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}