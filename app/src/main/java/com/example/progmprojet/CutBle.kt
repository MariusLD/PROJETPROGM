package com.example.progmprojet

import android.annotation.SuppressLint
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CutBle : AppCompatActivity() {

    // Getting mac address from mobile
    private fun getMacAddr() : String {
        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        val macAddress = wifiInfo.macAddress
        return macAddress
    }

    private var countDownTimer: CountDownTimer? = null
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cut_ble)
        var dbRef = FirebaseDatabase.getInstance().getReference("CutBle")
        val contentActivity: ViewGroup =  findViewById(android.R.id.content)
        val ble: ImageView = findViewById(R.id.ble)
        val score: TextView = findViewById(R.id.score)
        var startInsideBle = false
        var goThroughBle = false
        var lastPointCountTime: Long = 0
        val pointCountDelay: Long = 250
        var currentImageRessource = R.drawable.ble
        contentActivity.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    val x = motionEvent.x.toInt()
                    val y = motionEvent.y.toInt()
                    // We check that we start outside
                    if (x >= ble.left && x <= ble.right && y >= ble.top && y <= ble.bottom) {
                        startInsideBle = true
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    val x = motionEvent.x.toInt()
                    val y = motionEvent.y.toInt()
                    // Then we check if the finger goes through the wheat
                    if (!startInsideBle && (x >= ble.left && x <= ble.right && y >= ble.top && y <= ble.bottom)) {
                        goThroughBle = true
                    }
                }
                MotionEvent.ACTION_UP -> {
                    val x = motionEvent.x.toInt()
                    val y = motionEvent.y.toInt()
                    // Finally we check if we end outside the view
                    if (goThroughBle && !((x >= ble.left && x <= ble.right && y >= ble.top && y <= ble.bottom))) {
                        val currentTime = System.currentTimeMillis()
                        // Only when the wheat is available to be cut
                        if (currentTime - lastPointCountTime >= pointCountDelay) {
                            // We show the cut wheat
                            ble.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.blecoupe))
                            currentImageRessource = R.drawable.blecoupe

                            // We add a point to the score
                            var scoreInt: Int = score.text.toString().toInt()
                            scoreInt+=1
                            score.text = scoreInt.toString()
                            lastPointCountTime = currentTime
                            // We reset the wheat after a delay
                            ble.postDelayed({
                                if (currentImageRessource == R.drawable.blecoupe) {
                                    ble.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ble))
                                    currentImageRessource = R.drawable.ble
                                }
                            }, pointCountDelay)
                        }
                    }
                    // We reset the booleans for the next touch
                    startInsideBle = false
                    goThroughBle = false
                }
            }
            true
        }

        val timerTextView: TextView = findViewById(R.id.timer)
        countDownTimer = object : CountDownTimer(30000, 1000) { // 30 seconds timer
            override fun onTick(millisUntilFinished: Long) {
                timerTextView.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                val main : Intent =  Intent(this@CutBle,MainActivity::class.java)
                val score: TextView = findViewById(R.id.score)
                val scoreInt : Int = score.text.toString().toInt()
                main.putExtra("input",""+scoreInt)
                setResult(RESULT_OK,main)

                val dbScoreRef = dbRef.child(getMacAddr())
                dbScoreRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    // We check if the score is better than the previous one and we update it if so
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val currentScore = snapshot.getValue(Int::class.java)
                        if (currentScore == null || scoreInt > currentScore) {
                            dbScoreRef.setValue(scoreInt)
                        }
                        finish()
                    }
                    override fun onCancelled(error: DatabaseError) {
                        finish()
                    }
                })
            }
        }
        countDownTimer?.start()
    }
    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}