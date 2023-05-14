package com.example.progmprojet

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
        val name = intent.getStringExtra("user").toString()
        val nameField : EditText = findViewById(R.id.name)
        val valider: Button = findViewById(R.id.button)

        if (name != "null") {
            nameField.setText("Connecté en tant que $name ✅")
        }

        // Listener on "valider" button to send the name to MainActivity when clicked
        valider.setOnClickListener{
            val intent = intent
            if (nameField.text.toString().length > 16) {
                Toast.makeText(this, "Le nom ne doit pas dépasser 16 caractères", Toast.LENGTH_SHORT).show()
            } else if (nameField.text.toString().length < 3) {
                Toast.makeText(this, "Le nom ne doit faire au moins 3 caractères", Toast.LENGTH_SHORT).show()
            } else {
                intent.putExtra("name", nameField.text.toString())
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }
}