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

import kotlin.collections.ArrayList
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    var score : Int =0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var minijeux = ArrayList<Class<*>>()
        minijeux.add(TapTaupe::class.java)
        minijeux.add(Quizz::class.java)
        minijeux.add(QuizzSound::class.java)
        minijeux.add(PieGame::class.java)
        minijeux.add(CutBle::class.java)
        minijeux= minijeux.shuffled() as ArrayList<Class<*>>
        val quizz : Intent =  Intent(this,TapTaupe::class.java)
        val button: Button = findViewById(R.id.button)
        val leaderboard: Button = findViewById(R.id.leaderboard)

        var getResult =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val value = it.data?.getStringExtra("input")
                    score += value!!.toInt()
                }
            }

        button.setOnClickListener{
            for(i in 0..2) {
                getResult.launch(Intent(this,minijeux.get(i)))
            }
        }

        leaderboard.setOnClickListener{
            // Start Activity Leaderboard
            val intent = Intent(this, Leaderboard::class.java)
            startActivity(intent)
        }
    }
}