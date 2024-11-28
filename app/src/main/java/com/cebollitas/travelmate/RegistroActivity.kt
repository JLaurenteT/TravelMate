package com.cebollitas.travelmate

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistroActivity : AppCompatActivity() {

    // Definir FirebaseAuth y Firestore
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Inicializar FirebaseAuth y Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Obtener referencias a los campos de entrada
        val nombreEditText = findViewById<EditText>(R.id.nombre)
        val emailEditText = findViewById<EditText>(R.id.email)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val roleSpinner = findViewById<Spinner>(R.id.roleSpinner)

        // Botón de registro
        val registroButton = findViewById<Button>(R.id.registroButton)
        registroButton.setOnClickListener {

            // Obtener los valores de los campos
            val nombre = nombreEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val role = roleSpinner.selectedItem.toString()

            // Verificar si los campos están vacíos
            if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            } else {
                // Registrar al usuario en Firebase
                registerUser(email, password, nombre, role)
            }
        }

        // Botón de login para redirigir a la pantalla de login
        val loginTextView = findViewById<TextView>(R.id.login)
        loginTextView.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    // Función para registrar el usuario en Firebase
    private fun registerUser(email: String, password: String, nombre: String, role: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registro exitoso, agregar datos del usuario a Firestore
                    val user = auth.currentUser
                    val userData = hashMapOf(
                        "nombre" to nombre,
                        "email" to email,
                        "role" to role
                    )
                    user?.let {
                        db.collection("users").document(it.uid).set(userData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                                navigateToHome()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al registrar en Firestore: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                } else {
                    // Si el registro falla, mostrar un mensaje de error
                    Toast.makeText(this, "Error al registrar: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    // Función para redirigir a la pantalla de inicio después del registro
    private fun navigateToHome() {
        val intent = Intent(this, PrincipalActivity::class.java)
        startActivity(intent)
        finish() // Finalizar esta actividad para evitar regresar al registro con el botón de atrás
    }
}