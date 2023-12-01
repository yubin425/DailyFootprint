package com.example.dailyfootprint.ui.dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.dailyfootprint.ui.challenge.ChallengeActivity
import com.example.dailyfootprint.databinding.FragmentDashboardBinding
import com.example.dailyfootprint.databinding.ActivityChallengeviewBinding


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
import java.util.Calendar
import java.util.UUID

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
            .inflate(R.layout.activity_challengeview, parent, false)

        return MyViewHolder(cardView)
    }

    // 2. Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        //holder.challengeTitle.setText("제목입니다")
        //holder.location.setText("위치입니다")
        //holder.numOfChallenge.setText("달성 목표 수입니다")

        val challenge = challengeList[position]
        holder.challengeTitle.text = challenge.challengeName
        holder.location.text = challenge.location
        holder.numOfChallenge.text = "목표: ${challenge.goal}"

        //val drawable = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.dashboard_challenge_progressbarstart)
        val progressView = holder.itemView as ViewGroup
        val dayColors = mapOf(
            R.id.bar_mon to challengeList[position].successTime[0],
            R.id.bar_tue to challengeList[position].successTime[1],
            R.id.bar_wed to challengeList[position].successTime[2],
            R.id.bar_thu to challengeList[position].successTime[3],
            R.id.bar_fri to challengeList[position].successTime[4],
            R.id.bar_sat to challengeList[position].successTime[5],
            R.id.bar_sun to challengeList[position].successTime[6],
        )

        Log.d("MyAdapter", "position: $position")
        Log.d("MyAdapter", "dayColors: $dayColors")

        fun getDayViewInt(day: TextView): Int {
            return when (day.id) {
                R.id.bar_mon -> 0
                R.id.bar_tue -> 1
                R.id.bar_wed -> 2
                R.id.bar_thu -> 3
                R.id.bar_fri -> 4
                R.id.bar_sat -> 5
                R.id.bar_sun -> 6
                else -> 0
            }
        }

        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1
        for ((dayViewId, success) in dayColors) {
            val dayView: TextView = progressView.findViewById(dayViewId)
            if (success == 1) {
                /*
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
                 */
                dayView?.setTextColor(Color.parseColor("#52B449"))
            } else if (today > 0 && getDayViewInt(dayView) < (today - 1) && success == 0) {
                // 지난 요일에 대해서 successTime이 0이면 빨간색으로 표시
                dayView?.setTextColor(Color.RED)
            }
        }

        // 팝업 창
        holder.delChallBtn.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ChallengeviewActivity::class.java)
            intent.putExtra("challengeCode", challengeList[position].challengeCode)
            context.startActivity(intent)
        }

    }


    private fun getChallenge() {
        val firebaseDatabaseUrl =
            "https://dailyfootprint-aeac7-default-rtdb.asia-southeast1.firebasedatabase.app/"
        val database = Firebase.database(firebaseDatabaseUrl)
        val challRef = database.reference.child("challenges")

        /*
        val challenge = Challenge(
            challengeCode = "751ab369-9df3-4fef-8cad-9951f86uck0c",
            challengeName = "Challenge Name2",
            challengeOwner = "7jkumBBvTiftMfhGsknsNVBrLpx2",
            location = "",
            position = arrayListOf(37.7749F, -122.4194F),
            goal = 30,
            successTime = arrayListOf(0, 0, 0, 1, 0, 0, 0)
        )


        challRef.child("751ab369-9df3-4fef-8cad-9951f86uck0c").setValue(challenge)

         */


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
        getChallenge()
    }

    // 3. Return the size of your dataset (invoked by the layout manager)
    //테스트 (수정필요)
    override fun getItemCount(): Int {
        return challengeList.size
    }
}

class WeeklyCleanupReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            // Firebase Realtime Database에 접근
            val firebaseDatabaseUrl =
                "https://dailyfootprint-aeac7-default-rtdb.asia-southeast1.firebasedatabase.app/"
            val database = FirebaseDatabase.getInstance(firebaseDatabaseUrl)
            val challengesRef = database.reference.child("challenges")

            // challenges의 successTime을 초기화하는 작업
            challengesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (challengeSnapshot in dataSnapshot.children) {
                        // 현재 주의 시작 시간을 0으로 초기화
                        val successTime = mutableListOf<Int>(0, 0, 0, 0, 0, 0, 0)
                        challengeSnapshot.child("successTime").ref.setValue(successTime)
                    }

                    Log.d("WeeklyCleanupReceiver", "Weekly cleanup task executed.")
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("WeeklyCleanupReceiver", "Error cleaning up challenges: ${databaseError.toException()}")
                }
            })
        }
    }
}

class WeeklyCleanupScheduler(private val context: Context) {

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleWeeklyCleanup() {
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val requestCode = 0 // 정수 값으로 requestCode를 정의
        val intent = Intent(context, WeeklyCleanupReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent,
            PendingIntent.FLAG_IMMUTABLE)

        // 다음 토요일 23시 59분 59초 계산
        val nextSunday = getNextSunday()

        // 주기적으로 알람 설정 (주간마다)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmMgr.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                nextSunday.timeInMillis,
                pendingIntent
            )
        } else {
            alarmMgr.setExact(
                AlarmManager.RTC_WAKEUP,
                nextSunday.timeInMillis,
                pendingIntent
            )
        }
    }

    private fun getNextSunday(): Calendar {
        val calendar = Calendar.getInstance()
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val daysUntilSunday = (Calendar.SUNDAY - currentDayOfWeek + 7) % 7
        val nextSaturday = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            add(Calendar.DAY_OF_WEEK, daysUntilSunday)
        }
        return nextSaturday
    }
}

@Suppress("DEPRECATION")
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

        val cleanupScheduler = WeeklyCleanupScheduler(requireContext())
        cleanupScheduler.scheduleWeeklyCleanup()


        val challAddBtn: Button? = _binding?.root?.findViewById(R.id.addchall_btn)
        challAddBtn?.setOnClickListener {
            val context = view?.context
            val intent = Intent(context, ChallengeActivity::class.java)
            context?.startActivity(intent)
            Log.d("ChallAddBtnClick", "Challenge Activity Button Clicked!")
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        // 데이터베이스 리스너 해제
    }
}