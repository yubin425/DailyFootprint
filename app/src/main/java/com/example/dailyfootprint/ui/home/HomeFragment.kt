package com.example.dailyfootprint.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyfootprint.R
import com.example.dailyfootprint.databinding.FragmentHomeBinding
import com.example.dailyfootprint.databinding.HomeMainBinding
import com.example.dailyfootprint.databinding.HomeRecyclerViewItemBinding
import com.example.dailyfootprint.model.Challenge
import com.example.dailyfootprint.model.User


val dataList = listOf("항목 1", "항목 2", "항목 3","항목 4","항목 5","항목 6","항목 7","항목 8","항목 9")
val exampleChallenge = Challenge(
    challengeCode = "CH001",
    challengeName = "Marathon Training", // home에서 사용
    challengeOwner = "Alice",
    position = arrayOf(37.7749F, -122.4194F), // home에서 사용
    goal = 42, // 마라톤의 길이 (킬로미터)
    successTime = arrayOf(0, 0, 0, 1, 1, 1, 1) // 주중에만 도전 (예: 수요일부터 일요일까지)
)
val datachalllist = listOf(exampleChallenge, exampleChallenge, exampleChallenge, exampleChallenge, exampleChallenge, exampleChallenge, exampleChallenge)
// challenge형식으로 받아와서 넣기
// 위치사용
// 성공과 반응
// 데이터 올리는 것 까지 <- 문서와 맞춰야함
// 인증시 알림참으로 뜨게하는것도 나쁘지 않을듯

// 프로필이랑 알람은 뭔데?????? -> 일단 눌렀을때 반응으로바꾸기
// 프로필 누르면 -> Mypageview
// 알림누르면 friendalertview


// 임시데이터

class MyAdapter(private val dataList: List<Challenge>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HomeRecyclerViewItemBinding.inflate(inflater, parent, false)


        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = datachalllist[position]
        holder.bind(item)
    }

    override fun getItemCount() = datachalllist.size

    class ViewHolder(private val binding: HomeRecyclerViewItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Challenge) {
            binding.textTitle.text = item.challengeName

            val myButton: Button = binding.certifiyButton
            var button_value = true
            myButton.setOnClickListener {
                // 여기에 버튼 클릭시 수행할 작업을 넣습니다.

                button_value = false
                if(!button_value){

                    val locationChecker = LocationChecker(myButton.context)

                    val (latitude, longitude) = item.position
//                    val (latitude, longitude) = Pair(37.4219983, -122.084) // 현재위치 뉴욕
                    val latitudeDouble = latitude.toDouble()
                    val longitudeDouble = longitude.toDouble()
                    val isNearby = locationChecker.isWithin20mOfTarget(latitudeDouble, longitudeDouble)

                    if (isNearby) {
                        myButton.isEnabled = button_value
                        // 20미터 이내인 경우의 로직
                        Toast.makeText( myButton.context, binding.textTitle.text.toString()+" "+myButton.text.toString() + " 버튼이 클릭:true", Toast.LENGTH_SHORT).show()
                        myButton.background= ContextCompat.getDrawable(myButton.context, R.drawable.round_gray_button)
                    } else {
                        // 20미터 밖인 경우의 로직
                        Toast.makeText( myButton.context,latitude.toString() + ", " + longitude, Toast.LENGTH_SHORT).show()
                    }
                }

            }
            // 다른 뷰에 데이터 바인딩
        }
    }
}
class HomeFragment : Fragment() {

//    private var _binding: FragmentHomeBinding? = null
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var cheertext : (String) = "기본값 : 에러가 났어요!"

    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }
    // This property is only valid between onCreateView and
    // onDestroyView.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 네비게이션 바 숨기기
        (activity as? AppCompatActivity)?.supportActionBar?.hide()

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        // cheerview
        val include_home_cheer = binding.includeHomeCheer
        val textView = include_home_cheer.backgroundbutton
        cheerupdate()
        textView.text = cheertext


        // recyclerview
        val recyclerView: RecyclerView = binding.recyclerHome
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.addItemDecoration(CustomDividerDecoration(recyclerView.context))
//        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, LinearLayoutManager.VERTICAL)

//        recyclerView.addItemDecoration(dividerItemDecoration)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
//            adapter = MyAdapter(dataList)
            adapter = MyAdapter(datachalllist)
//            isNestedScrollingEnabled = false
        }


//        recyclerView.layoutManager = NoScrollLinearLayoutManager(activity)
        return root
    }

    private fun cheerupdate(score: Double = 30.2){
        // hard coding - + cheer data 연결

        val intscore = score.toInt()
        when {
            intscore in 70..100 -> cheertext = "잘했어요"
            intscore in 50 until 70 -> cheertext = "반이나 했어요"
            intscore in 30 until 50 -> cheertext = "반까지 얼마 안남았어요"
            intscore > 0 && intscore < 30 -> cheertext = "시작이 반이래요!"
            intscore == 0 -> cheertext = "같이 시작해볼까요?"
            else -> cheertext = "계산 에러가 났어요"
            // 추가적으로 0이나 음수 등의 경우를 처리할 수도 있습니다.
        }

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

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun isWithin20mOfTarget(targetLatitude: Double, targetLongitude: Double): Boolean {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없다면 false 반환
            return false
        }

        val currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: return false
        val targetLocation = Location("").apply {
            latitude = targetLatitude
            longitude = targetLongitude
        }

        val distanceInMeters = currentLocation.distanceTo(targetLocation)
        return distanceInMeters <= 20
    }
}

