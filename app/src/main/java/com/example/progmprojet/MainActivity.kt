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
    companion object {
        const val REQUEST_CODE_PROFILE = 123
    }

    var user : String = ""

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
        val quizz : Intent =  Intent(this,QuizzSound::class.java)
        val button: Button = findViewById(R.id.button)
        val leaderboard: Button = findViewById(R.id.leaderboard)
        val multi: Button = findViewById(R.id.button2)
        val profile : Button = findViewById(R.id.profile)

        var getResult =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val value = it.data?.getIntExtra("input",0)
                    if (value != null) {
                        score += value
                    }
                }
            }

        button.setOnClickListener{
            for(i in 0..2) {
                val gameIntent = Intent(this, minijeux.get(i))
                gameIntent.putExtra("user", user)
                getResult.launch(Intent(gameIntent))
            }
            //getResult.launch(quizz)
        }

        leaderboard.setOnClickListener{
            // Start Activity Leaderboard
            val intent = Intent(this, Leaderboard::class.java)
            startActivity(intent)
        }

        multi.setOnClickListener{
            val gameIntent = Intent(this, WifiDirectActivity::class.java)
            gameIntent.putExtra("user", user)
            getResult.launch(Intent(gameIntent))
        }

        profile.setOnClickListener{
            val profileIntent = Intent(this, Profile::class.java)
            if (!user.isEmpty()) {
                profileIntent.putExtra("user", user)
            }
            startActivityForResult(profileIntent, REQUEST_CODE_PROFILE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PROFILE && resultCode == Activity.RESULT_OK && data != null) {
            val name = data.getStringExtra("name")
            user = name.toString()
            Toast.makeText(this, "Bienvenue $name", Toast.LENGTH_SHORT).show()
        }
    }
}