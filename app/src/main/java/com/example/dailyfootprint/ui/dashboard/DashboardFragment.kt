package com.example.dailyfootprint.ui.dashboard

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.example.dailyfootprint.ui.dashboard.ChallengeActivity
import com.example.dailyfootprint.databinding.FragmentDashboardBinding
import com.example.dailyfootprint.databinding.ActivityChallengeBinding


import com.example.dailyfootprint.R
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.example.dailyfootprint.model.Challenge
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class MyAdapter() :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    private val challengeList = mutableListOf<Challenge>()
    private lateinit var userId: String


    private fun initializeUserId() {
        userId = FirebaseManager.getUID()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        public var challengeTitle: TextView = itemView.findViewById(R.id.textView)
        public var location: TextView = itemView.findViewById(R.id.textView2)
        public var numOfChallenge: TextView = itemView.findViewById(R.id.textView3)
        val delChallBtn: Button = itemView.findViewById(R.id.delBtn)
    }

    // 1. Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyAdapter.MyViewHolder {
        // create a new view
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_challenge, parent, false)

        return MyViewHolder(cardView)
    }

    // 2. Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        //holder.challengeTitle.setText("제목입니다")
        holder.location.setText("위치입니다")
        holder.numOfChallenge.setText("달성 목표 수입니다")

        val challenge = challengeList[position]
        holder.challengeTitle.text = challenge.challengeName
        //holder.location.text = challenge.location
        holder.numOfChallenge.text = "목표: ${challenge.goal}"

        //val drawable = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.dashboard_challenge_progressbarstart)
        val progressView = holder.itemView as ViewGroup
        val dayColors = mapOf(
            R.id.bar_sun to challengeList[position].successTime[0],
            R.id.bar_mon to challengeList[position].successTime[1],
            R.id.bar_tue to challengeList[position].successTime[2],
            R.id.bar_wed to challengeList[position].successTime[3],
            R.id.bar_thu to challengeList[position].successTime[4],
            R.id.bar_fri to challengeList[position].successTime[5],
            R.id.bar_sat to challengeList[position].successTime[6]
        )

        Log.d("MyAdapter", "position: $position")
        Log.d("MyAdapter", "dayColors: $dayColors")

        for ((dayViewId, success) in dayColors) {
            val dayView: TextView = progressView.findViewById(dayViewId)
            if (success == 1) {
                if (dayViewId == R.id.bar_sun) {
                    val drawable = ContextCompat.getDrawable(
                        progressView.context,
                        R.drawable.dashboard_challenge_successstart
                    )
                    dayView?.background = drawable
                }

                else if (dayViewId == R.id.bar_sat) {
                    val drawable = ContextCompat.getDrawable(
                        progressView.context,
                        R.drawable.dashboard_challenge_successend
                    )
                    dayView?.background = drawable
                }

                else {
                    dayView?.setBackgroundColor(Color.parseColor("#52B449"))
                }
            }
        }

        // 팝업 창
        holder.delChallBtn.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ChallengeActivity::class.java)
            intent.putExtra("challengeCode", challengeList[position].challengeCode)
            context.startActivity(intent)
        }
    }


    private fun fetchChallenges() {
        val firebaseDatabaseUrl =
            "https://dailyfootprint-aeac7-default-rtdb.asia-southeast1.firebasedatabase.app/"
        val database = Firebase.database(firebaseDatabaseUrl)
        val challRef = database.reference.child("challenges")


        val challOwner = challRef.child("challengeOwner").toString()

        //currentUser 랑 challengeOwner 확인하는거 추가해야함
        Log.d("MyAdapter", "Challenge Owner: ${challOwner}")
        Log.d("MyAdapter", "User ID: $userId")
        //if (userId == challOwner) {
            challRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    challengeList.clear()

                    for (challengeSnapshot in dataSnapshot.children) {
                        val challenge = challengeSnapshot.getValue(Challenge::class.java)
                        challenge?.let {
                            // challengeOwner 값 가져오기
                            val challengeOwner = challengeSnapshot.child("challengeOwner").getValue(String::class.java)

                            // 현재 사용자와 challengeOwner가 같은 경우만 리스트에 추가
                            if (userId == challengeOwner) {
                                challengeList.add(it)
                            }
                        }
                    }

                    //updateDayColors()
                    notifyDataSetChanged()

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Firebase", "데이터 가져오기 실패: ${databaseError.toException()}")
                }
            })

            // updateDayColors 함수 추가

        }
    //}

    fun initialize() {
        initializeUserId()
        fetchChallenges()
    }

    // 3. Return the size of your dataset (invoked by the layout manager)
    //테스트 (수정필요)
    override fun getItemCount(): Int {
        return challengeList.size
    }
}

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewAdapter: MyAdapter
    private lateinit var viewManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as? AppCompatActivity)?.supportActionBar?.hide()

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewManager = LinearLayoutManager(context, VERTICAL, false)

        viewAdapter = MyAdapter()
        viewAdapter.initialize() // 어댑터 초기화 및 데이터 가져오기

        val recyclerView: RecyclerView = binding.recyclerviewMain
        recyclerView.apply {
          layoutManager = viewManager
            adapter = viewAdapter
        }


        /*   챌린지 추가 버튼 클릭 시, 챌린지 추가 화면으로 이동
        val challAddBtn: Button = view.findViewById(R.id.addchallnege_btn)
        challAddBtn.setOnClickListener {
            val addChalFragment
            openFragment()
        }
        */

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        // 데이터베이스 리스너 해제
    }
}