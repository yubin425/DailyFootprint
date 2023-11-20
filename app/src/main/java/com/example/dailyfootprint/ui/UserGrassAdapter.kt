package com.example.dailyfootprint.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyfootprint.R
import com.example.dailyfootprint.databinding.ItemUsergrassBinding
import com.example.dailyfootprint.model.User

class UserGrassAdapter(private val userList: List<User>) : RecyclerView.Adapter<UserGrassAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemUsergrassBinding) : RecyclerView.ViewHolder(binding.root) {
        val textViewUsername: TextView = itemView.findViewById(R.id.textViewUsername)
        val gridLayoutGrass: GridLayout = itemView.findViewById(R.id.gridLayoutGrass)

        // 색깔을 일일이 칠하는 코드 같은데.. 이걸 실행하면 아래 에러가 뜸
        // java.lang.NullPointerException: Attempt to invoke virtual method 'void android.view.View.setBackgroundColor(int)' on a null object reference
        /*fun bindGridCells() {
            for (row in 0 until gridLayoutGrass.rowCount) {
                for (col in 0 until gridLayoutGrass.columnCount) {
                    print("ch")
                    val cell = gridLayoutGrass.getChildAt(row * gridLayoutGrass.columnCount + col)
                    cell.setBackgroundColor(Color.GRAY)
                }
            }
        }*/
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

        // 위 색깔 칠하기 코드 풀면 이것도 같이 풀어주기
        //holder.bindGridCells()

        // Clear existing views in GridLayout
        holder.binding.gridLayoutGrass.removeAllViews()

        for (i in 0 until 140) {
            val grassView = View(holder.itemView.context)

            // Set the default color to gray
            grassView.setBackgroundColor(Color.RED)

            // If grassData value is true, set the color to green
            /*if (userList[position].successData[i]) {
                grassView.setBackgroundColor(Color.GREEN)
            }*/

            // Add layout parameters for GridLayout
            val params = GridLayout.LayoutParams()
            params.rowSpec = GridLayout.spec(i / 20) // Calculate the row position
            params.columnSpec = GridLayout.spec(i % 20) // Calculate the column position

            grassView.layoutParams = params

            // Add grass item to GridLayout
            holder.binding.gridLayoutGrass.addView(grassView)
        }
    }

    override fun getItemCount(): Int {
        // 얘 값을 140으로 바꾸면 out of index 에러 생김.
        return 1
    }
}