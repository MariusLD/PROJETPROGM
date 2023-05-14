package com.example.progmprojet

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PieGame : AppCompatActivity() {

    var name = ""

    private var countDownTimer: CountDownTimer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pie_game)
        name = intent.getStringExtra("user").toString()
        var dbRef = FirebaseDatabase.getInstance().getReference("PieGame")
        val mp: MediaPlayer = MediaPlayer.create(this, R.raw.cow)
        var incr = 0
        var sm = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        var mySensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        var se = object : SensorEventListener{
            @SuppressLint("SuspiciousIndentation")
            override fun onSensorChanged(event: SensorEvent?) {
                var values = event?.values
                var y = values?.get(1)
                    if (y!! >= 50) {
                        incr += 1
                        mp.start();
                    }

                val score: TextView = findViewById(R.id.score)
                score.text = incr.toString()
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }
        }
        sm.registerListener(se, mySensor, SensorManager.SENSOR_DELAY_NORMAL)

        val timerTextView: TextView = findViewById(R.id.timer)
        countDownTimer = object : CountDownTimer(30000, 1000) { // 30 seconds timer
            override fun onTick(millisUntilFinished: Long) {
                timerTextView.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                val main : Intent =  Intent(this@PieGame,MainActivity::class.java)
                val score: TextView = findViewById(R.id.score)
                val scoreInt : Int = score.text.toString().toInt()
                main.putExtra("input",scoreInt)
                setResult(RESULT_OK,main)

                if (name.isEmpty()) {
                    finish()
                    return
                } else {
                    // We get the score from the database
                    dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val currentScore = snapshot.child(name).getValue(Int::class.java)
                            if (currentScore == null || scoreInt > currentScore) {
                                dbRef.child(name).setValue(scoreInt)
                            }
                            finish()
                        }
                        override fun onCancelled(error: DatabaseError) {
                            finish()
                        }
                    })
                }
            }
        }
        countDownTimer?.start()
    }
    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}