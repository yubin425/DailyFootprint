/*package com.example.dailyfootprint.ui.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dailyfootprint.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FriendsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FriendsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FriendsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FriendsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}*/

package com.example.dailyfootprint.ui.friends

import FirebaseManager
import FirebaseManager.databaseReference
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dailyfootprint.R
import com.example.dailyfootprint.databinding.FragmentFriendsBinding
import com.example.dailyfootprint.ui.GrassDecoration
import com.example.dailyfootprint.ui.UserGrassAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class FriendsFragment : Fragment(R.layout.fragment_friends) {

    private lateinit var binding: FragmentFriendsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.plusButton.setOnClickListener {
            val intent = Intent(activity, AddFriendActivity::class.java)
            startActivity(intent)
        }

        // 임의의 더미 데이터 생성
        /*val dateStringList1: ArrayList<String> = arrayListOf("2023-11-10","2023-11-09","2023-11-08","2023-11-06")
        val dateStringList2: ArrayList<String> = arrayListOf("2023-11-14","2023-11-03","2023-11-02","2023-10-31")
        val dateStringList3: ArrayList<String> = arrayListOf("2023-11-22","2023-11-21","2023-11-20","2023-10-19")
        var dummyUser = User("UserCode", "Kim", dateStringList1, arrayListOf<String>("James", "Mathew"))
        var friend1 = User("UserCode1", "James", dateStringList2, arrayListOf<String>("Kim"))
        var friend2 = User("UserCode", "Mathew", dateStringList3, arrayListOf<String>("Kim"))
        data.add(friend1)
        data.add(friend2)*/

        addFriendsInfo(FirebaseManager.getUID(),
            callback = {friends ->
                // 리사이클러뷰 아이템 간 간격 설정
                binding.recyclerView.addItemDecoration(GrassDecoration(80))
                binding.recyclerView.adapter = UserGrassAdapter(friends)
            },
            onError = {error ->
                Log.w("Error: ", error.message)
            })
    }

    private fun addFriendsInfo(userCode: String, callback: (List<String>) -> Unit, onError: (DatabaseError) -> Unit) {
        val friendListRef = databaseReference.child("user/$userCode/friendList")
        var friendCodeList = arrayListOf<String>()

        friendListRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (friendSnapshot in snapshot.children) {
                    val friend = friendSnapshot.getValue(String::class.java)
                    friend?.let {
                        friendCodeList.add(it)
                    }
                }
                callback(friendCodeList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onError(databaseError)
            }
        })
    }
}