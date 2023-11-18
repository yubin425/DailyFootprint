package com.example.dailyfootprint.ui.home

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
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


val dataList = listOf("항목 1", "항목 2", "항목 3","항목 4","항목 5","항목 6","항목 7","항목 8","항목 9")

class MyAdapter(private val dataList: List<String>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HomeRecyclerViewItemBinding.inflate(inflater, parent, false)

        val myButton: Button = binding.certifiyButton
        var Button_value = true
        myButton.setOnClickListener {
            // 여기에 버튼 클릭시 수행할 작업을 넣습니다.
            Toast.makeText( myButton.context, binding.textTitle.text.toString()+" "+myButton.text.toString() + " 버튼이 클릭되었습니다", Toast.LENGTH_SHORT).show()
            Button_value = false
            if(!Button_value){

                myButton.isEnabled = Button_value

                myButton.background= ContextCompat.getDrawable(myButton.context, R.drawable.round_gray_button)
            }

        }

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item)
    }

    override fun getItemCount() = dataList.size

    class ViewHolder(private val binding: HomeRecyclerViewItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String) {
            binding.textTitle.text = item
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
            adapter = MyAdapter(dataList)
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