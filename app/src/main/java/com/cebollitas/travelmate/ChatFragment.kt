package com.cebollitas.travelmate

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatUsuarioTextView: TextView
    private var messageList = mutableListOf<Message>()
    private var userId: String? = null
    private var chatId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView)
        messageEditText = view.findViewById(R.id.messageEditText)
        sendButton = view.findViewById(R.id.sendButton)
        chatUsuarioTextView = view.findViewById(R.id.chatusuario)

        userId = arguments?.getString("userId")
        chatId = getChatId(auth.currentUser?.uid ?: "", userId ?: "")

        chatRecyclerView.layoutManager = LinearLayoutManager(activity)
        chatAdapter = ChatAdapter(messageList, auth.currentUser?.uid ?: "")
        chatRecyclerView.adapter = chatAdapter

        sendButton.setOnClickListener {
            sendMessage()
        }

        loadMessages()
        loadUserName()

        return view
    }

    private fun getChatId(user1: String, user2: String): String {
        return if (user1 < user2) "$user1-$user2" else "$user2-$user1"
    }

    private fun sendMessage() {
        val messageText = messageEditText.text.toString()
        if (messageText.isNotEmpty()) {
            val message = Message(
                senderId = auth.currentUser?.uid ?: "",
                receiverId = userId ?: "",
                message = messageText,
                timestamp = Timestamp.now()
            )
            db.collection("chats").document(chatId!!).collection("messages").add(message)
                .addOnSuccessListener {
                    Log.d("ChatFragment", "Message sent with server timestamp")
                }
                .addOnFailureListener { e ->
                    Log.e("ChatFragment", "Error sending message: ${e.message}")
                }
            messageEditText.text.clear()
        } else {
            Log.e("ChatFragment", "Message text is empty")
        }
    }

    private fun loadMessages() {
        db.collection("chats").document(chatId!!).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("ChatFragment", "Error loading messages: ${e.message}")
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    for (dc in snapshots.documentChanges) {
                        val message = dc.document.toObject(Message::class.java)
                        if (message.timestamp != null) {
                            when (dc.type) {
                                DocumentChange.Type.ADDED -> {
                                    if (!messageList.any { it.timestamp == message.timestamp }) {
                                        messageList.add(message)
                                        messageList.sortBy { it.timestamp }
                                        chatAdapter.notifyItemInserted(messageList.size - 1)
                                        chatRecyclerView.scrollToPosition(messageList.size - 1)
                                    }
                                }
                                DocumentChange.Type.MODIFIED -> {
                                    val index = messageList.indexOfFirst { it.timestamp == message.timestamp }
                                    if (index != -1) {
                                        messageList[index] = message
                                        chatAdapter.notifyItemChanged(index)
                                    }
                                }
                                DocumentChange.Type.REMOVED -> {
                                    val index = messageList.indexOfFirst { it.timestamp == message.timestamp }
                                    if (index != -1) {
                                        messageList.removeAt(index)
                                        chatAdapter.notifyItemRemoved(index)
                                    }
                                }
                            }
                        } else {
                            Log.e("ChatFragment", "Message timestamp is null: ${dc.document.id}")
                        }
                    }
                }
            }
    }

    private fun loadUserName() {
        userId?.let { id ->
            db.collection("users").document(id).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val userName = document.getString("nombre")
                        chatUsuarioTextView.text = userName
                    } else {
                        Log.e("ChatFragment", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("ChatFragment", "get failed with ", exception)
                }
        }
    }
}