package com.example.dailyfootprint.ui

import FirebaseManager
import FirebaseManager.databaseReference
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyfootprint.R
import com.example.dailyfootprint.databinding.ItemUsergrassBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import org.joda.time.LocalDate

class UserGrassAdapter(private val friendCodeList: List<String>) : RecyclerView.Adapter<UserGrassAdapter.ViewHolder>() {


    class ViewHolder(val binding: ItemUsergrassBinding) : RecyclerView.ViewHolder(binding.root) {
        val textViewUsername: TextView = itemView.findViewById(R.id.textViewUsername)
        val gridLayoutGrass: GridLayout = itemView.findViewById(R.id.gridLayoutGrass)
    }

    // ViewHolder를 생성 후 리턴
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usergrass, parent, false)
        return ViewHolder(ItemUsergrassBinding.bind(view))
    }

    // View에 내용이 씌워질 때 호출
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
<<<<<<< Updated upstream
        val userCode = friendCodeList[position]
        var successData = arrayListOf<String>()
=======
        val userCode = userCodeList[position]

>>>>>>> Stashed changes
        val userRef = databaseReference.child("user/$userCode")

        Log.w("user: ", userCode)


        FirebaseManager.getName(userCode,
            callback = { userName ->
                holder.binding.textViewUsername.text = userName
                getSuccessData(userRef,
                    callback = {successData ->
                        Log.w("successData:", successData.toString())

                        // Clear existing views in GridLayout
                        holder.binding.gridLayoutGrass.removeAllViews()

                        // 140개의 날짜 데이터를 그려야 하므로 139일 전부터 오늘까지의 날짜 별 인증 여부 확인
                        var previousDate = LocalDate.now().minusDays(139)
                        var consecutive = 0

                        for (i in 0 until 140) {
                            val grassView = View(holder.itemView.context)

                            val preDateToString = previousDate.toString()
                            var isAchieved = false

                            if (successData != null) {
                                for (i in successData) {
                                    if (preDateToString == i && consecutive == 2) {
                                        grassView.setBackgroundResource(R.drawable.square_darkgreen_cell)
                                        isAchieved = true
                                        break
                                    } else if (preDateToString == i && consecutive == 1) {
                                        grassView.setBackgroundResource(R.drawable.square_green_cell)
                                        consecutive += 1
                                        isAchieved = true
                                        break
                                    } else if (preDateToString == i && consecutive == 0) {
                                        grassView.setBackgroundResource(R.drawable.square_lightgreen_cell)
                                        consecutive += 1
                                        isAchieved = true
                                        break
                                    }
                                }
                            }
                            // 해당 날짜에 인증
                            if (!isAchieved) {
                                grassView.setBackgroundResource(R.drawable.square_gray_cell)
                                consecutive = 0
                            }



                            // GridLayout을 위한 parameter 설정
                            val params = GridLayout.LayoutParams()
                            val screenWidth = holder.itemView.context.resources.displayMetrics.widthPixels
                            // 각 셀의 위치 설정
                            params.rowSpec = GridLayout.spec(6-(i%7)) // 셀의 row 위치값 계산
                            params.columnSpec = GridLayout.spec(i/7) // 셀의 column 위치값 계산
                            // 셀 간 간격 설정 - 가로 화면 비율에 맞게 조정
                            params.topMargin = screenWidth/270
                            params.bottomMargin = screenWidth/270
                            params.leftMargin = screenWidth/270
                            params.rightMargin = screenWidth/270
                            //params.width = (screenWidth-24-params.topMargin*40-120)/20
                            //params.height = (screenWidth-24-params.topMargin*40-120)/20
                            params.width = screenWidth/28
                            params.height = screenWidth/28

                            grassView.layoutParams = params

                            // GridLayout에 잔디 추가
                            holder.binding.gridLayoutGrass.addView(grassView)

                            previousDate = previousDate.plusDays(1)
                        }
                    },
                    onError = {error ->
                        Log.w("Error: ", error.message)
                    })
            },
            onError = { error ->
                Log.w("Error: ", error.message)
            })
    }

    override fun getItemCount(): Int {
        return friendCodeList.size
    }

    private fun getSuccessData(userRef: DatabaseReference, callback: (List<String>) -> Unit, onError: (DatabaseError) -> Unit) {
        var successData = arrayListOf<String>()

        userRef.child("successData").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (successDataSnaphot in snapshot.children) {
                    val date = successDataSnaphot.key
                    date?.let {
                        Log.w("success: ", it)
                        successData.add(it)
                    }
                }
                callback(successData)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("Fail: ", "fail to get successData")
            }
        })
    }
}