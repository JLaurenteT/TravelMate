package com.cebollitas.travelmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class User(var id: String = "", val nombre: String = "", val email: String = "", val role: String = "")

class UserAdapter(private val users: List<User>, private val onClick: (User) -> Unit) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(view: View, val onClick: (User) -> Unit) : RecyclerView.ViewHolder(view) {
        private val fullNameTextView: TextView = view.findViewById(R.id.fullNameTextView)
        private var currentUser: User? = null

        init {
            view.setOnClickListener {
                currentUser?.let {
                    onClick(it)
                }
            }
        }

        fun bind(user: User) {
            currentUser = user
            fullNameTextView.text = user.nombre
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount() = users.size
}