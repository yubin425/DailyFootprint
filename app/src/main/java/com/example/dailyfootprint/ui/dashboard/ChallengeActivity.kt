package com.example.dailyfootprint.ui.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dailyfootprint.R
import com.example.dailyfootprint.databinding.ActivityChallengeBinding
import com.example.dailyfootprint.databinding.DelChallengeDialogBinding

class ChallengeActivity : AppCompatActivity() {
    private lateinit var binding: DelChallengeDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_challenge)
        // 뷰 바인딩
        binding =DelChallengeDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showPopup()
    }

    private fun showPopup() {
        // Dialog만들기
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.del_challenge_dialog, null)
        val textView: TextView = mDialogView.findViewById(R.id.delMsg)
        textView.text = "삭제하시겠습니까?"

        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .create()

        val mAlertDialog = mBuilder

        val okButton = mDialogView.findViewById<Button>(R.id.delete_grbtn)
        okButton.setOnClickListener {


            //파이어베이스, 챌린지 삭제
            Toast.makeText(this, "챌린지가 삭제되었습니다", Toast.LENGTH_SHORT).show()
            mAlertDialog.dismiss()

            fragmentTransaction.replace(
                R.id.fragment_container,
                DashboardFragment()
            )
            fragmentTransaction.commit()

        }

        val noButton = mDialogView.findViewById<Button>(R.id.delete_redbtn)
        noButton.setOnClickListener {
            mAlertDialog.dismiss()

            fragmentTransaction.replace(
                R.id.fragment_container,
                DashboardFragment()
            )
            fragmentTransaction.commit()
        }

        val dialogContainer = mDialogView.findViewById<LinearLayout>(R.id.dial)
        dialogContainer.setOnClickListener {
            mAlertDialog.dismiss()
            fragmentTransaction.replace(
                R.id.fragment_container,
                DashboardFragment()
            )
            fragmentTransaction.commit()
        }


        mAlertDialog.setView(mDialogView)
        mAlertDialog.show()

    }
}