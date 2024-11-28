package com.cebollitas.travelmate

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        // Obtener referencias a los campos de entrada
        emailEditText = findViewById(R.id.mail)
        passwordEditText = findViewById(R.id.password)

        val iniciarButton = findViewById<Button>(R.id.iniciar)
        val registroButton = findViewById<Button>(R.id.registro)

        // Acción del botón de iniciar sesión
        iniciarButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Validar que los campos no estén vacíos
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar que el email sea válido
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.error = "Correo no válido"
                emailEditText.requestFocus()
                return@setOnClickListener
            }

            // Iniciar sesión con Firebase Authentication
            loginUser(email, password)
        }

        // Acción del botón de registro
        registroButton.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Si el inicio de sesión es exitoso, navegar a la HomeActivity
                    val intent = Intent(this, PrincipalActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish() // Cierra la actividad actual
                } else {
                    // Si el inicio de sesión falla, muestra un mensaje
                    Toast.makeText(this, "Error al iniciar sesión. Verifica tus credenciales.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }
}
