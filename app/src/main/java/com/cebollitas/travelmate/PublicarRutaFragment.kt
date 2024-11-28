package com.cebollitas.travelmate

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class PublicarRutaFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var constraintLayout: ConstraintLayout
    private var editTextCount = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_publicar_ruta, container, false)
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        constraintLayout = view.findViewById(R.id.main)

        view.findViewById<View>(R.id.publicar_ruta).setOnClickListener {
            val destinos = view.findViewById<EditText>(R.id.destinos).text.toString()
            val fecha = view.findViewById<EditText>(R.id.fecha).text.toString()
            val observaciones = view.findViewById<EditText>(R.id.observaciones).text.toString()
            saveRouteToFirebase(destinos, fecha, observaciones)
        }
        view.findViewById<View>(R.id.fecha).setOnClickListener {
            showDatePickerDialog(view.findViewById(R.id.fecha))
        }
        view.findViewById<View>(R.id.agregar).setOnClickListener {
            addNewEditTexts()
        }

        return view
    }

    private fun saveRouteToFirebase(destinos: String, fecha: String, observaciones: String) {
        val user = auth.currentUser
        if (user != null) {
            val routeData = hashMapOf(
                "destinos" to destinos,
                "fecha" to fecha,
                "observaciones" to observaciones,
                "userId" to user.uid
            )

            db.collection("rutas")
                .add(routeData)
                .addOnSuccessListener {
                    Toast.makeText(activity, "Ruta publicada correctamente", Toast.LENGTH_SHORT).show()
                    view?.findViewById<EditText>(R.id.destinos)?.text?.clear()
                    view?.findViewById<EditText>(R.id.fecha)?.text?.clear()
                    view?.findViewById<EditText>(R.id.observaciones)?.text?.clear()
                    view?.findViewById<EditText>(R.id.destinos)?.requestFocus()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(activity, "Error al publicar la ruta: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(activity, "No se pudo obtener el usuario autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addNewEditTexts() {
        val newEditText1 = createEditText("Nuevo destino")
        val newEditText2 = createEditText("Nueva fecha")
        val newEditText3 = createEditText("Nuevas observaciones")

        constraintLayout.addView(newEditText1)
        constraintLayout.addView(newEditText2)
        constraintLayout.addView(newEditText3)

        newEditText2.setOnClickListener {
            showDatePickerDialog(newEditText2)
        }

        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        val lastViewId = if (editTextCount == 0) R.id.observaciones else constraintLayout.getChildAt(constraintLayout.childCount - 4).id

        constraintSet.connect(newEditText1.id, ConstraintSet.TOP, lastViewId, ConstraintSet.BOTTOM, 20)
        constraintSet.connect(newEditText1.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.connect(newEditText1.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

        constraintSet.connect(newEditText2.id, ConstraintSet.TOP, newEditText1.id, ConstraintSet.BOTTOM, 20)
        constraintSet.connect(newEditText2.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.connect(newEditText2.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

        constraintSet.connect(newEditText3.id, ConstraintSet.TOP, newEditText2.id, ConstraintSet.BOTTOM, 20)
        constraintSet.connect(newEditText3.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.connect(newEditText3.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

        constraintSet.applyTo(constraintLayout)

        editTextCount += 3
    }

    private fun createEditText(hint: String): EditText {
        val editText = EditText(activity)
        editText.id = ViewCompat.generateViewId()
        editText.layoutParams = ConstraintLayout.LayoutParams(
            300.dpToPx(),
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        editText.hint = hint
        editText.setPadding(10.dpToPx(), 10.dpToPx(), 10.dpToPx(), 10.dpToPx())
        editText.setBackgroundResource(R.drawable.edit_text_background)
        editText.textSize = 14f
        return editText
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireActivity(),
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val formattedDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
                editText.setText(formattedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}