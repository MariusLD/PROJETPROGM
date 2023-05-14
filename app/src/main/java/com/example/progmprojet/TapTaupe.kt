package com.example.progmprojet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TapTaupe : AppCompatActivity() {
    var listButton =ArrayList<ImageButton>()
    var position=0
    var name:String= ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tap_taupe)
        var dbRef = FirebaseDatabase.getInstance().getReference("Taupe la Tape")
        name = intent.getStringExtra("user").toString()
        var score: Int=0
        var vie:Int=3
        var button: ImageButton = findViewById(R.id.b1)
        var button2: ImageButton = findViewById(R.id.b2)
        var button3: ImageButton = findViewById(R.id.b3)
        var button4: ImageButton = findViewById(R.id.b4)
        var button5: ImageButton = findViewById(R.id.b5)
        var button6: ImageButton = findViewById(R.id.b6)
        var button7: ImageButton = findViewById(R.id.b7)
        var button8: ImageButton = findViewById(R.id.b8)
        var scoreText : TextView=findViewById(R.id.score)
        var vieText : TextView=findViewById(R.id.vie)
        listButton.add(button)
        listButton.add(button2)
        listButton.add(button3)
        listButton.add(button4)
        listButton.add(button5)
        listButton.add(button6)
        listButton.add(button8)
        listButton.add(button7)
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                bougeTaupe()
                handler.postDelayed(this, 750) // rappel toutes les 5 secondes
            }
        }, 500) // appel initial après 5 secondes
        for(button in listButton){
            button.setOnClickListener{
                var buttonR:ImageButton=listButton.get(position)
                if (buttonR==it){
                    score=score+1
                    scoreText.setText("Score :" + score)
                    bougeTaupe()
                }else{
                    vie=vie-1
                    vieText.setText("Vie :" + vie)
                    if(vie==0){
                        val main : Intent =  Intent(this,MainActivity::class.java)
                        main.putExtra("input",score)
                        setResult(RESULT_OK,main)

                        if (name.isEmpty()) {
                            finish()
                        } else {
                            // We get the score from the database
                            dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val currentScore = snapshot.child(name).getValue(Int::class.java)
                                    if (currentScore == null || score > currentScore) {
                                        dbRef.child(name).setValue(score)
                                    }
                                    finish()
                                }
                                override fun onCancelled(error: DatabaseError) {
                                    finish()
                                }
                            })
                        }
                        finish()
                    }
                }
            }
        }
    }
    fun bougeTaupe(){
        for(button in listButton){
            button.setImageResource(R.drawable.mole_hole_illustration_clipart_image_210307988)
        }
        position= (0..7).random()
        var buttonR:ImageButton=listButton.get(position)
        buttonR.setImageResource(R.drawable.taupe2)
    }
}