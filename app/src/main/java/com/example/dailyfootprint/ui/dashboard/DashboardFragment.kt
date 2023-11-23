package com.example.dailyfootprint.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import com.example.dailyfootprint.ui.dashboard.ChallengeActivity
import com.example.dailyfootprint.databinding.FragmentDashboardBinding
import com.example.dailyfootprint.databinding.ActivityChallengeBinding

import com.example.dailyfootprint.R
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL

class MyAdapter() :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
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
    //테스트를 위한 임시 text값
    //firebase 연결해야해..
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.challengeTitle.setText("제목입니다")
        holder.location.setText("위치입니다")
        holder.numOfChallenge.setText("달성 목표 수입니다")

        // Set a click listener for the delete button
        holder.delChallBtn.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ChallengeActivity::class.java)
            context.startActivity(intent)
        }
    }


    // 3. Return the size of your dataset (invoked by the layout manager)
    //테스트 (수정필요)
    override fun getItemCount(): Int {
        return 3
    }
}
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
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
    }
}