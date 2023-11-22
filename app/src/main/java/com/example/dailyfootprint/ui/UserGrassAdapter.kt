package com.example.dailyfootprint.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyfootprint.R
import com.example.dailyfootprint.databinding.ItemUsergrassBinding
import com.example.dailyfootprint.model.User
import org.joda.time.LocalDate

class UserGrassAdapter(private val userList: List<User>) : RecyclerView.Adapter<UserGrassAdapter.ViewHolder>() {


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
        val user = userList[position]

        holder.binding.textViewUsername.text = user.userName

        // Clear existing views in GridLayout
        holder.binding.gridLayoutGrass.removeAllViews()

        // 140개의 날짜 데이터를 그려야 하므로 139일 전부터 오늘까지의 날짜 별 인증 여부 확인
        var previousDate = LocalDate.now().minusDays(139)
        var consecutive = 0

        for (i in 0 until 140) {
            val grassView = View(holder.itemView.context)

            val preDateToString = previousDate.toString()
            var isAchieved = false

            for (i in user.successData) {
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
            // 해당 날짜에 인증
            if (!isAchieved) {
                grassView.setBackgroundResource(R.drawable.square_gray_cell)
                consecutive = 0
            }



            // GridLayout을 위한 parameter 설정
            val params = GridLayout.LayoutParams()
            val screenWidth = holder.itemView.context.resources.displayMetrics.widthPixels
            // 각 셀의 크기 설정 - 가로 화면 비율에 맞게 조정
            // 각 셀의 위치 설정
            params.rowSpec = GridLayout.spec(6-(i%7)) // 셀의 row 위치값 계산
            params.columnSpec = GridLayout.spec(i/7) // 셀의 column 위치값 계산
            // 셀 간 간격 설정 - 가로 화면 비율에 맞게 조정
            params.topMargin = 4
            params.bottomMargin = 4
            params.leftMargin = 4
            params.rightMargin = 4
            params.width = (screenWidth-24-params.topMargin*40-120)/20
            params.height = (screenWidth-24-params.topMargin*40-120)/20

            grassView.layoutParams = params

            // GridLayout에 잔디 추가
            holder.binding.gridLayoutGrass.addView(grassView)

            previousDate = previousDate.plusDays(1)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}