package com.example.progmprojet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import com.example.progmprojet.R

class Leaderboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)
        val back: ImageButton = findViewById(R.id.back)

        back.setOnClickListener{
            // Start Activity Leaderboard
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}