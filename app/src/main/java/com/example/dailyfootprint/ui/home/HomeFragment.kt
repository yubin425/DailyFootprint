package com.example.dailyfootprint.ui.home

import FirebaseManager
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyfootprint.R
import com.example.dailyfootprint.databinding.FragmentHomeBinding
import com.example.dailyfootprint.databinding.HomeRecyclerViewItemBinding
import com.example.dailyfootprint.model.Challenge
import com.example.dailyfootprint.ui.GrassDecoration
import com.example.dailyfootprint.ui.UserGrassAdapter
import com.example.dailyfootprint.ui.friendAlert.FriendAlertActivity
import com.example.dailyfootprint.ui.mypage.MyPageActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


val exampleChallenge = Challenge(
    challengeCode = "CH001",
    challengeName = "테스트", // home에서 사용
    challengeOwner = "Alice",
    location = "",
    position = arrayListOf(37.7749, -122.4194), // home에서 사용
    goal = 42, // 마라톤의 길이 (킬로미터)
    successTime = arrayListOf(0, 0, 0, 0, 0, 0, 0) // 주중에만 도전 (예: 수요일부터 일요일까지)
)


object DateUtils {
    fun getAdjustedDayOfWeek(): Int {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - 2
    }
}

var datachalllist = listOf(exampleChallenge)
// challenge형식으로 받아와서 넣기
// 위치사용
// 성공과 반응
// 데이터 올리는 것 까지 <- 문서와 맞춰야함
// 인증시 알림참으로 뜨게하는것도 나쁘지 않을듯

// 프로필이랑 알람은 뭔데?????? -> 일단 눌렀을때 반응으로바꾸기
// 프로필 누르면 -> Mypageview
// 알림누르면 friendalertview


// 임시데이터

//
//addChallengeToFirebase()
fun addChallengeToFirebase() {
    // Create an instance of the Firebase Realtime Database
    val database = FirebaseDatabase.getInstance()
    val challengesRef = database.getReference("challenges")

    // Create a Challenge object
    val exampleChallenge = Challenge(
        challengeCode = "",
        challengeName = "얘는 제목",
        challengeOwner = FirebaseManager.getUID(),
        location = "",
        position = arrayListOf(37.7749, -122.4194),
        goal = 42,
        successTime = arrayListOf(0, 0, 0, 0, 0, 0, 0)
    )

    // Push the challenge to Firebase
    // Using the challengeCode as a key ensures that each challenge is unique
    Log.d("Firebase add challenge", "in")
    challengesRef.child(exampleChallenge.challengeCode).setValue(exampleChallenge)
        .addOnSuccessListener {
            // Handle success
            println("Challenge added successfully")
        }
        .addOnFailureListener {
            // Handle failure
            println("Error adding challenge: ${it.message}")
        }
}


class MyAdapter : ListAdapter<Challenge, MyAdapter.ViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HomeRecyclerViewItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }
    class DiffCallback : DiffUtil.ItemCallback<Challenge>() {
        override fun areItemsTheSame(oldItem: Challenge, newItem: Challenge): Boolean {
            return oldItem.challengeCode == newItem.challengeCode // 고유 식별자 비교
        }

        override fun areContentsTheSame(oldItem: Challenge, newItem: Challenge): Boolean {
            return oldItem == newItem // 내용 비교
        }
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) // `getItem` 메서드를 사용
        holder.bind(item)
    }


    class ViewHolder(private val binding: HomeRecyclerViewItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Challenge) {
            binding.textTitle.text = item.challengeName
            binding.textpos.text = item.location
            val myButton: Button = binding.certifiyButton

            val locationChecker = LocationChecker(myButton.context)
            // 현재 날짜 및 시간

            locationChecker.update()
            if(1==item.successTime[DateUtils.getAdjustedDayOfWeek()]){
                myButton.isEnabled = false
            }
            else{
                myButton.isEnabled = true
            }

            if(myButton.isEnabled == false){

                myButton.background= ContextCompat.getDrawable(myButton.context, R.drawable.round_gray_button)
            }
            else{
                myButton.background= ContextCompat.getDrawable(myButton.context, R.drawable.round_green_button)

            }
            if(myButton.isEnabled){
                myButton.setOnClickListener {


                    val (latitude, longitude) = item.position
                    Log.d("pos", latitude.toString() + ", "+ longitude.toString())
                    locationChecker.isWithin20mOfTarget(latitude, longitude) { isNearby ->
                        Log.d("isNearby", isNearby.toString())


                    //test code
                    //var isNearby = true // 가까운지 확인하는 코드

                    if (isNearby) {
                        myButton.isEnabled = false
                        // 20미터 이내인 경우의 로직
                        // 디버그 코드 (현재 위치 출력)
                        Toast.makeText( myButton.context, binding.textTitle.text.toString()+" "+myButton.text.toString() + " 버튼이 클릭:true", Toast.LENGTH_SHORT).show()
                        // 배포용 코드
//                         Toast.makeText( myButton.context, " 오늘도 수고하셨어요~ 남은 하루도 파이팅! ", Toast.LENGTH_SHORT).show()

                        val database = FirebaseDatabase.getInstance()
                        val challengesRef: DatabaseReference = database.getReference("challenges")
                        val parentRef= challengesRef.child(item.challengeCode)
                        Log.d("firebase server",parentRef.toString())
                        parentRef.child("successTime")?.child(DateUtils.getAdjustedDayOfWeek().toString())?.setValue(1)


                        val calendar = Calendar.getInstance()
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val today: String = dateFormat.format(calendar.time)

                        val userRef: DatabaseReference = database.getReference("user")
                        userRef.child(FirebaseManager.getUID())?.child("successData")?.child(today)?.setValue(1)



                        //인증후 서버에 업데이트 -> 반영되면 그걸로 다시 판단
                    } else {
                        // 20미터 밖인 경우의 로직
                        // 디버그 코드 (현재 위치 출력)
                        Toast.makeText( myButton.context,latitude.toString() + ", " + longitude, Toast.LENGTH_SHORT).show()
                        // 배포용 코드
//                        Toast.makeText( myButton.context,"현재 위치가 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
                        myButton.isEnabled = true

                    }
                }}

            }
            // 다른 뷰에 데이터 바인딩
        }
    }
}
class HomeFragment : Fragment() {
    private lateinit var valueEventListener: ValueEventListener
    val database = FirebaseDatabase.getInstance()
    private var challengesRef: DatabaseReference = database.getReference("challenges")
//    private var _binding: FragmentHomeBinding? = null
    private var _binding: FragmentHomeBinding? = null

    private var currentDayOfWeek: Int = 0
    private val binding get() = _binding!!
    private var cheertext : (String) = "기본값 : 에러가 났어요!"
    private var cheercounttext : (String) = "기본값 : 카운트 에러가 났어요!"

    // This property is only valid between onCreateView and
    // onDestroyView.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

//        addChallengeToFirebase()
        // 네비게이션 바 숨기기
        (activity as? AppCompatActivity)?.supportActionBar?.hide()

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val profileimage : ImageView = binding.imageprofile
        profileimage.setOnClickListener {
            // 클릭 이벤트 발생 시 새로운 액티비티로 이동
            val intent = Intent(context, MyPageActivity::class.java)
            startActivity(intent)
        }
        val buttonbell : ImageView = binding.imagebell
        buttonbell.setOnClickListener {
            // 클릭 이벤트 발생 시 새로운 액티비티로 이동
            val intent = Intent(context, FriendAlertActivity::class.java)
            startActivity(intent)
        }


        val recyclerViewgrass: RecyclerView = binding.homeusergrass.recyclergrass
//        recyclerViewgrass.layoutManager = LinearLayoutManager(recyclerViewgrass.context)
//        recyclerViewgrass.addItemDecoration(CustomDividerDecoration(recyclerViewgrass.context))

        recyclerViewgrass.addItemDecoration(GrassDecoration(0))

        recyclerViewgrass.layoutManager = LinearLayoutManager(recyclerViewgrass.context)
        var friendCodeList = arrayListOf<String>(FirebaseManager.getUID())
        recyclerViewgrass.adapter = UserGrassAdapter(friendCodeList)



        // recyclerview
        val recyclerView: RecyclerView = binding.recyclerHome
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.addItemDecoration(CustomDividerDecoration(recyclerView.context))

        // 어댑터 초기화
        val adapter = MyAdapter()
        adapter.submitList(datachalllist)
        recyclerView.adapter = adapter
        recyclerView.layoutManager=LinearLayoutManager(context)

        val include_home_cheer = binding.includeHomeCheer
        val textView = include_home_cheer.backgroundbutton
        val textcountView = include_home_cheer.textcountview
        // Firebase에서 데이터 로드
        val currentUserUID = FirebaseManager.getUID() // 현재 사용자의 UID

        challengesRef.orderByChild("challengeOwner").equalTo(currentUserUID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val newlist = dataSnapshot.children.mapNotNull {
                        it.getValue(Challenge::class.java)
                    }

                    // 어댑터에 데이터 업데이트
                    datachalllist = newlist
                    adapter.submitList(newlist)
                    adapter.notifyDataSetChanged()

                    // cheerview
                    cheerupdate()
                    textView.text = cheertext
                    textcountView.text = cheercounttext
                    recyclerViewgrass.layoutManager = LinearLayoutManager(recyclerViewgrass.context)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // 쿼리 실패 처리
                    Log.w("FirebaseData", "loadPost:onCancelled", databaseError.toException())
                }
            })
        return root

    }


    private fun cheerupdate(){

        val dayOfWeek = DateUtils.getAdjustedDayOfWeek() // 오늘 요일의 숫자 (0부터 6까지)

        val countMatching = datachalllist.count { item ->
            item.successTime.getOrElse(dayOfWeek) { 0 } == 1
        }

        val totalItemCount = datachalllist.size
        Log.d("string check",countMatching.toString() +" "+ totalItemCount.toString())

        val intscore = (countMatching.toDouble()/totalItemCount *100).toInt()
        when {
            intscore > 99 -> cheertext = "\uD83D\uDE0D 이번주의 챌린지를 전부 달성했어요!"
            intscore in 80..99 -> cheertext = "\uD83D\uDE31 거의 다 왔어요. 조금만 힘내요!"
            intscore in 50 until 80 -> cheertext = "\uD83E\uDD79 반이나 했어요. 정말 대단해요!"
            intscore > 0 && intscore < 50 -> cheertext = "\uD83E\uDD73  시작이 반이래요!"
            intscore == 0 -> cheertext = "\uD83D\uDC4D 챌린지 달성에 도전해봐요!"
            else -> cheertext = "계산 에러가 났어요"
        }
        cheercounttext = "$countMatching / $totalItemCount "

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



class CustomDividerDecoration(context: Context) : RecyclerView.ItemDecoration() {
    private val dividerHeight = 4 // 구분선의 높이
    private val dividerPaint = Paint().apply {
        color = Color.parseColor("#D2D2D2") // 구분선의 색상
    }
    private val padding = 40 // 구분선의 좌우 패딩

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft + padding
        val right = parent.width - parent.paddingRight - padding

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + dividerHeight

            c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), dividerPaint)
        }
    }
}

class LocationChecker(private val context: Context) {

//    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    var distanceInMeters = 2000.0f
    var currentLocation = Location("").apply {
        latitude = 0.0
        longitude = 0.0
    }
    fun update(){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없다면 false 반환

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
                }
            return
        }
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location){
                currentLocation = location
            }
        }
        Log.d("pos test","how many")
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)

    }
    fun isWithin20mOfTarget(targetLatitude: Double, targetLongitude: Double, callback: (Boolean) -> Unit){
        var targetLocation = Location("").apply {
            latitude = targetLatitude
            longitude = targetLongitude
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없다면 false 반환
            callback(false)
            return
        }
        val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        currentLocation = lastKnownLocation ?: Location("").apply {
            latitude = 0.0
            longitude = 0.0
        }
        val distanceInMeters = targetLocation.distanceTo(currentLocation)
        callback(distanceInMeters <= 20)
        Log.d("pos test",currentLocation.latitude.toString() + " "+ currentLocation.longitude.toString())

    }
}
