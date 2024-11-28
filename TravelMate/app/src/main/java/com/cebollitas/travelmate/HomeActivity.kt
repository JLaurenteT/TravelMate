package com.cebollitas.travelmate

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val ruta = findViewById<CardView>(R.id.ruta)
        ruta .setOnClickListener {
            startActivity(Intent(this, PublicarRutaActivity::class.java))
        }
        val buscarGuia = findViewById<CardView>(R.id.buscarGuia)
        buscarGuia .setOnClickListener {
            startActivity(Intent(this, BuscarGuiaActivity::class.java))
        }
        val explorar = findViewById<CardView>(R.id.explorar)
        explorar .setOnClickListener {
            startActivity(Intent(this, ExperienciasdeRutasActivity::class.java))
        }
    }
}