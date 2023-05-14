package com.example.progmprojet

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val sharedPref = getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)
        val currentUsername = sharedPref.getString("username", null)
        var name : String = ""
        if  (currentUsername != null) {
            name = currentUsername
        }
        val nameField : EditText = findViewById(R.id.name)
        val valider: Button = findViewById(R.id.button)

        if (name != "") {
            nameField.setText("Connecté en tant que $name ✅")
        }

        valider.setOnClickListener{
            val intent = intent
            if (nameField.text.toString().length > 16) {
                Toast.makeText(this, "Le nom ne doit pas dépasser 16 caractères", Toast.LENGTH_SHORT).show()
            } else if (nameField.text.toString().length in 1..3) {
                Toast.makeText(this, "Le nom ne doit faire plus de 3 caractères", Toast.LENGTH_SHORT).show()
            } else {
                val editor = sharedPref.edit()
                editor.putString("username", nameField.text.toString())
                editor.apply()
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }
}