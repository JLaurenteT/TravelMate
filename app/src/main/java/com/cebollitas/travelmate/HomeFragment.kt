package com.cebollitas.travelmate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val ruta = view.findViewById<CardView>(R.id.ruta)
        ruta.setOnClickListener {
            navigateToFragment(PublicarRutaFragment())
        }
        val buscarGuia = view.findViewById<CardView>(R.id.buscarGuia)
        buscarGuia.setOnClickListener {
            navigateToFragment(BuscarGuiaFragment())
        }
        val explorar = view.findViewById<CardView>(R.id.explorar)
        explorar.setOnClickListener {
            navigateToFragment(ExperienciasDeRutasFragment())
        }
        return view
    }

    private fun navigateToFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}