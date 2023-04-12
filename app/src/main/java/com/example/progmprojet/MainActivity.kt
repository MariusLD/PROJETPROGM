package com.example.progmprojet

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val quizz : Intent =  Intent(this,TapTaupe::class.java)
        val button: Button = findViewById(R.id.button)

        var getResult =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val value = it.data?.getStringExtra("input")
                    System.out.println(value)
                }
            }

        button.setOnClickListener{
            getResult.launch(quizz)
        }
    }
}