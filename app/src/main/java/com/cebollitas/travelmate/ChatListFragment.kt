package com.cebollitas.travelmate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatListFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private var userList = mutableListOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_list, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        userRecyclerView = view.findViewById(R.id.userRecyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(activity)
        userAdapter = UserAdapter(userList) { user ->
            val chatFragment = ChatFragment().apply {
                arguments = Bundle().apply {
                    putString("userId", user.id)
                }
            }
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, chatFragment)
                ?.addToBackStack(null)
                ?.commit()
        }

        userRecyclerView.adapter = userAdapter

        fetchUsers()

        return view
    }

    private fun fetchUsers() {
        val currentUser = auth.currentUser
        currentUser?.let {
            db.collection("users").document(it.uid).get()
                .addOnSuccessListener { document ->
                    val currentUserRole = document.getString("role")
                    val targetRole = if (currentUserRole == "Turista") "Guía" else "Turista"
                    db.collection("users").whereEqualTo("role", targetRole).get()
                        .addOnSuccessListener { result ->
                            userList.clear()
                            for (document in result) {
                                val user = document.toObject(User::class.java)
                                user.id = document.id
                                if (user.id != currentUser.uid) {
                                    userList.add(user)
                                }
                            }
                            userAdapter.notifyDataSetChanged()
                            Log.d("ChatListFragment", "Users fetched: ${userList.size}")
                        }
                        .addOnFailureListener { e ->
                            Log.e("ChatListFragment", "Error fetching users: ${e.message}")
                        }
                }
                .addOnFailureListener { e ->
                    Log.e("ChatListFragment", "Error fetching current user: ${e.message}")
                }
        }
    }
}