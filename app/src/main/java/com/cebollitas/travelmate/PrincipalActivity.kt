package com.cebollitas.travelmate

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.cebollitas.travelmate.databinding.ActivityPrincipalBinding

class PrincipalActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPrincipalBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())
        binding.navigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.search -> {
                    replaceFragment(BuscarGuiaFragment())
                    true
                }
                R.id.add -> {
                    replaceFragment(ExperienciasDeRutasFragment())
                    true
                }
                R.id.notification -> {
                    replaceFragment(NotificacionesFragment())
                    true
                }
                R.id.chat -> {
                    replaceFragment(ChatListFragment())
                    true
                }
                else -> false
            }
        }
    }
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }
}