package com.example.progmprojet

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class PieGame : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pie_game)
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
    }
}