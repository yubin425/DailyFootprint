package com.example.dailyfootprint.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.dailyfootprint.R
import com.example.dailyfootprint.databinding.FragmentDashboardBinding
import com.google.firebase.Firebase
import com.google.firebase.database.database

class ChallengeviewActivity : AppCompatActivity() {
    private lateinit var binding: FragmentDashboardBinding
    val firebaseDatabaseUrl =
        "https://dailyfootprint-aeac7-default-rtdb.asia-southeast1.firebasedatabase.app/"
    val database = Firebase.database(firebaseDatabaseUrl)
    val challRef = database.reference.child("challenges")
    private var currentChallenge: String? = null
    /*
    val bundle = Bundle()
    val fragment = DashboardFragment()
    fragment.arguments = bundle
    */


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        //setContentView(R.layout.fragment_dashboard)
        // 뷰 바인딩
        binding =FragmentDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmentContainer = findViewById<ConstraintLayout>(R.id.fragment_container)
        if (fragmentContainer != null) {
            Log.d("ChallengeActivity", "fragment_container found")
        } else {
            Log.e("ChallengeActivity", "fragment_container not found")
        }

        currentChallenge = intent.getStringExtra("challengeCode")
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
            // ChallengeActivity의 멤버 변수로 설정된 currentChallenge를 사용
       //   val delChallenge = currentChallenge
            Log.d("ChallengeActivity", "Deleting Challenge: $currentChallenge")

            if (currentChallenge != null) {
                // 파이어베이스에서 챌린지 삭제
               //al challengeRef = challRef.child(delChallenge)
                challRef.child(currentChallenge!!).removeValue()

                Toast.makeText(this, "챌린지가 삭제되었습니다", Toast.LENGTH_SHORT).show()
            }

            mAlertDialog.dismiss()
            finish()

                /*
                fragmentTransaction.replace(
                    R.id.fragment_container,
                    DashboardFragment()
                )
                fragmentTransaction.commit()

            addDashboardFragment(fragmentTransaction)
                 */

        }

        val noButton = mDialogView.findViewById<Button>(R.id.delete_redbtn)
        noButton.setOnClickListener {
            mAlertDialog.dismiss()
            finish()

/*
            fragmentTransaction.replace(
                R.id.fragment_container,
                DashboardFragment()
            )
            fragmentTransaction.commit()
*/
        }


        val dialogContainer = mDialogView.findViewById<LinearLayout>(R.id.dial)
        dialogContainer.setOnClickListener {
            mAlertDialog.dismiss()
            finish()
            /*
            fragmentTransaction.replace(
                R.id.fragment_container,
                DashboardFragment()
            )
            fragmentTransaction.commit()

            addDashboardFragment(fragmentTransaction)
             */

        }


        mAlertDialog.setView(mDialogView)
        mAlertDialog.show()

    }

    /*
    private fun addDashboardFragment(fragmentTransaction: FragmentTransaction) {
        val dashboardFragment = DashboardFragment()
        fragmentTransaction.replace(R.id.fragment_container, dashboardFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()


        val existingFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (existingFragment != null) {
            // If DashboardFragment is already in the container, remove it.
            fragmentTransaction.remove(existingFragment)
        }

        // Add a new instance of DashboardFragment to fragment_container
        fragmentTransaction.replace(
            R.id.fragment_container,
            DashboardFragment()
        )
        fragmentTransaction.addToBackStack(null)

        fragmentTransaction.commit()


    }
*/
}