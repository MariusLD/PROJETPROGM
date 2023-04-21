package com.example.progmprojet

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

class CutBle : AppCompatActivity() {

    var countDownTimer: CountDownTimer? = null
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cut_ble)

        val contentActivity: ViewGroup =  findViewById(android.R.id.content)
        val ble: ImageView = findViewById(R.id.ble)
        val score: TextView = findViewById(R.id.score)
        var startInsideBle = false // Pour garder une trace si votre doigt est sur l'image view "ble"
        var goThroughBle = false
        var lastPointCountTime: Long = 0
        val pointCountDelay: Long = 250
        var currentImageRessource = R.drawable.ble
        contentActivity.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    val x = motionEvent.x.toInt()
                    val y = motionEvent.y.toInt()
                    // On vérifie que le joueur commence bien en mettant son doigt en dehors du blé pour préparer la coupe
                    if (x >= ble.left && x <= ble.right && y >= ble.top && y <= ble.bottom) {
                        startInsideBle = true
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    val x = motionEvent.x.toInt()
                    val y = motionEvent.y.toInt()
                    // On modifie la variable qui indique qu'on passe par le blé pour Action_UP si Action_DOWN
                    if (!startInsideBle && (x >= ble.left && x <= ble.right && y >= ble.top && y <= ble.bottom)) {
                        goThroughBle = true
                    }
                }
                MotionEvent.ACTION_UP -> {
                    val x = motionEvent.x.toInt()
                    val y = motionEvent.y.toInt()
                    // On vérifie qu'on finit bien hors du blé après être passé au travers de ce dernier
                    if (goThroughBle && !((x >= ble.left && x <= ble.right && y >= ble.top && y <= ble.bottom))) {
                        val currentTime = System.currentTimeMillis()
                        // On vérifie que le délai pointCountDelay est bien passé
                        if (currentTime - lastPointCountTime >= pointCountDelay) {
                            // On montre visuellement avec une image de blé coupé que le joueur a réussi sa coupe
                            ble.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.blecoupe))
                            currentImageRessource = R.drawable.blecoupe

                            // On ajoute un nouveau point au compteur pour valider la coupe
                            var scoreInt: Int = score.text.toString().toInt()
                            scoreInt+=1
                            score.text = scoreInt.toString()
                            lastPointCountTime = currentTime

                            // On remplace l'image du blé dès qu'on peut à nouveau le couper
                            ble.postDelayed({
                                if (currentImageRessource == R.drawable.blecoupe) {
                                    ble.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ble))
                                    currentImageRessource = R.drawable.ble
                                }
                            }, pointCountDelay)
                        }
                    }
                    // Peu importe si on a réussi la coupe ou non, on réintialise toutes les variables de mouvement pour préparer le prochain mouvement
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
                val scoreInt : String = score.text.toString()
                main.putExtra("input",""+scoreInt)
                setResult(RESULT_OK,main)
                finish()
            }
        }
        countDownTimer?.start()
    }
    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}