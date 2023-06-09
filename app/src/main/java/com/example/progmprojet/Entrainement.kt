package com.example.progmprojet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Entrainement : AppCompatActivity() {

    var user : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entrainement)

        user = intent.getStringExtra("user").toString()
        val cutBle: Button = findViewById(R.id.button4)
        val pieGame: Button = findViewById(R.id.button5)
        val quizz: Button = findViewById(R.id.button7)
        val quizzSound: Button = findViewById(R.id.button6)
        val snake: Button = findViewById(R.id.button8)
        val taupe: Button = findViewById(R.id.button9)

        cutBle.setOnClickListener {
            val serviceIntent = Intent(this, CutBle::class.java)
            serviceIntent.putExtra("user", user)
            this.startActivity(serviceIntent)
        }

        pieGame.setOnClickListener {
            val serviceIntent = Intent(this, PieGame::class.java)
            serviceIntent.putExtra("user", user)
            this.startActivity(serviceIntent)
        }

        quizz.setOnClickListener {
            val serviceIntent = Intent(this, Quizz::class.java)
            serviceIntent.putExtra("user", user)
            this.startActivity(serviceIntent)
        }

        quizzSound.setOnClickListener {
            val serviceIntent = Intent(this, QuizzSound::class.java)
            serviceIntent.putExtra("user", user)
            this.startActivity(serviceIntent)
        }

        snake.setOnClickListener {
            val serviceIntent = Intent(this, SnakeWithoutSnake::class.java)
            serviceIntent.putExtra("user", user)
            this.startActivity(serviceIntent)
        }

        taupe.setOnClickListener {
            val serviceIntent = Intent(this, TapTaupe::class.java)
            serviceIntent.putExtra("user", user)
            this.startActivity(serviceIntent)
        }
    }
}