package com.cebollitas.travelmate

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import java.util.Calendar

class PublicarRutaActivity : AppCompatActivity() {
    private lateinit var constraintLayout: ConstraintLayout
    private var editTextCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publicar_ruta)

        constraintLayout = findViewById(R.id.main)
        val etDate = findViewById<EditText>(R.id.fecha)
        val btnAgregar = findViewById<TextView>(R.id.agregar)

        etDate.setOnClickListener {
            showDatePickerDialog(etDate)
        }

        btnAgregar.setOnClickListener {
            addNewEditTexts()
        }
        val publicar_ruta = findViewById<Button>(R.id.publicar_ruta)
        publicar_ruta.setOnClickListener {
            Toast.makeText(this, "Ruta publicada", Toast.LENGTH_SHORT).show()
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
        val editText = EditText(this)
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
            this,
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