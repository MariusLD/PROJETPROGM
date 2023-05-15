package com.example.progmprojet

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

import kotlin.collections.ArrayList
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    var score : Int =0
    companion object {
        const val REQUEST_CODE_PROFILE = 123
    }

    var user : String = ""
    var getResult : ActivityResultLauncher<Intent>? =null
    var minijeux = ArrayList<Class<*>>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPref = getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)
        var collectedName = sharedPref.getString("username", null).toString()
        if (collectedName != "") {
            user = collectedName
        }
        minijeux.add(TapTaupe::class.java)
        minijeux.add(Quizz::class.java)
        minijeux.add(QuizzSound::class.java)
        minijeux.add(PieGame::class.java)
        minijeux.add(CutBle::class.java)
        //minijeux.add(SnakeWithoutSnake::class.java)
        minijeux= minijeux.shuffled() as ArrayList<Class<*>>
        val quizz : Intent =  Intent(this,QuizzSound::class.java)
        val button: Button = findViewById(R.id.button)
        val entrainement: Button = findViewById(R.id.button3)
        val leaderboard: Button = findViewById(R.id.leaderboard)
        val multi: Button = findViewById(R.id.button2)
        val profile : Button = findViewById(R.id.profile)
        var i=0
         this.getResult =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val value = it.data?.getIntExtra("input",0)
                    if (value != null) {
                        score += value
                        i++
                        if(i==3){
                            val serviceIntent = Intent(this, WinLoose::class.java)
                            serviceIntent.putExtra("win",10)
                            //serviceIntent.putExtra("activity",this)
                            serviceIntent.putExtra("score",this.score)
                            //this.startActivity(serviceIntent)
                            i=0
                            getResult?.launch(Intent(serviceIntent))
                        }
                    }
                    val rejoue = it.data?.getIntExtra("rejouer",0)
                    if(rejoue!=0){
                        joue()
                    }
                }
            }

        button.setOnClickListener{
            joue()
            //getResult.launch(quizz)
        }

        leaderboard.setOnClickListener{
            // Start Activity Leaderboard
            val intent = Intent(this, Leaderboard::class.java)
            startActivity(intent)
        }

        entrainement.setOnClickListener{
            // Start Activity Leaderboard
            val intent = Intent(this, Entrainement::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
        }

        multi.setOnClickListener{
            val gameIntent = Intent(this, WifiDirectActivity::class.java)
            gameIntent.putExtra("user", user)
            getResult?.launch(Intent(gameIntent))
        }

        profile.setOnClickListener{
            val profileIntent = Intent(this, Profile::class.java)
            startActivityForResult(profileIntent, REQUEST_CODE_PROFILE)
        }
    }

    fun joue(){
        val random = java.util.Random()
        val set = mutableSetOf<Int>()
        while (set.size < 3) {
            val rt=random.nextInt(5)
            set.add(rt)
        }
        val numbers = set.toList()
        for(i in 0..2) {
            val gameIntent = Intent(this, minijeux.get(numbers.get(i)))
            gameIntent.putExtra("user", user)
            getResult?.launch(Intent(gameIntent))
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PROFILE && resultCode == Activity.RESULT_OK && data != null) {
            val sharedPref = getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)
            user = sharedPref.getString("username", null).toString()
            if (user != "") {
                Toast.makeText(this, "Bienvenue $user", Toast.LENGTH_SHORT).show()
            }
        }
    }
}