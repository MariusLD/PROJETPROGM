package com.example.progmprojet

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.graphics.RectF
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

class SnakeWithoutSnake : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snake_without_snake)
        val score = findViewById<TextView>(R.id.score)
        val board = findViewById<RelativeLayout>(R.id.board)
        val food = ImageView(this)
        val pig = ImageView(this)
        val tracking = mutableListOf(pig)
        var delayMillis = 100
        var currentDirection = "right"
        var handler = Handler()
        pig.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pig))
        pig.layoutParams = ViewGroup.LayoutParams(
            100,
            100
        )
        val centerX = board.width / 2 - pig.width / 2
        val centerY = board.height / 2 - pig.height / 2
        pig.translationX = centerX.toFloat()
        pig.translationY = centerY.toFloat()

        board.addView(pig)
        tracking.add(pig)
        var snakeX = pig.x
        var snakeY = pig.y

        food.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.apple))
        food.layoutParams = ViewGroup.LayoutParams(
            100,
            100
        )
        board.addView(food)
        val random = Random()
        val randomX = random.nextInt(801) - 400
        val randomY = random.nextInt(801) - 400
        food.x = randomX.toFloat()
        food.y = randomY.toFloat()

        fun checkFoodCollision() {
            val distanceThreshold = 50

            val distance = sqrt((pig.x - food.x).pow(2) + (pig.y - food.y).pow(2))

            if (distance < distanceThreshold) {

                // On aggrandit le cochon
                val newPig = ImageView(this)
                newPig.setImageResource(R.drawable.pig)
                newPig.layoutParams = ViewGroup.LayoutParams(
                    100,
                    100
                )
                board.addView(newPig)
                tracking.add(newPig)

                // On génère alors une nouvelle pomme
                val randomX = random.nextInt(801) - 100
                val randomY = random.nextInt(801) - 100
                food.x = randomX.toFloat()
                food.y = randomY.toFloat()
                delayMillis--
                var scoreInt: Int = score.text.toString().toInt()
                scoreInt+=1
                score.text = scoreInt.toString()
            }
        }

        val runnable = object : Runnable {
            override fun run() {
                println(snakeY)
                // On update la position du corps, sauf la tête évidemment
                for (i in tracking.size - 1 downTo 1) {
                    tracking[i].x = tracking[i - 1].x
                    tracking[i].y = tracking[i - 1].y
                }
                // Get the bounds of the board view in screen coordinates
                val boardBounds = Rect()
                board.getGlobalVisibleRect(boardBounds)

                // Set the limits of the map based on the bounds of the board view
                val map = RectF(
                    boardBounds.left.toFloat(),
                    boardBounds.top.toFloat(),
                    boardBounds.right.toFloat(),
                    boardBounds.bottom.toFloat()
                )

                // Use the map limits in your code
                when (currentDirection) {
                    "up" -> {
                        snakeY -= 10
                        if (snakeY < -(board.height/2)+score.height) {
                            snakeY = (-(board.height/2)+score.height).toFloat()
                            currentDirection = "pause"
                            println("Game Over")
                        }
                        pig.translationY = snakeY
                    }
                    "down" -> {
                        snakeY += 10
                        if (snakeY > (board.height/2)+score.height) {
                            snakeY = ((board.height/2)+score.height).toFloat()
                            currentDirection = "pause"
                            println("Game Over")
                        }
                        pig.translationY = snakeY
                    }
                    "left" -> {
                        snakeX += 10
                        if (snakeX < -(board.width/2)) {
                            snakeX = (board.width/2).toFloat()
                            currentDirection = "pause"
                            println("Game Over")
                        }
                        pig.translationX = snakeX
                    }
                    "right" -> {
                        snakeX += 10
                        snakeX -= 10
                        if (snakeX > board.width/2) {
                            snakeX = (-(board.width/2)).toFloat()
                            currentDirection = "pause"
                            println("Game Over")
                        }
                        pig.translationX = snakeX
                    }
                    "pause" -> {
                        snakeX += 0
                        pig.translationX = snakeX
                    }
                }
                
                checkFoodCollision()
                handler.postDelayed(this, delayMillis.toLong())
            }
        }
        handler.postDelayed(runnable, delayMillis.toLong())

        var sm = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        var mySensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        var se = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                val x = event?.values?.get(0) ?: 0f
                val y = event?.values?.get(1) ?: 0f

                // Déterminer la direction en fonction des valeurs de l'accéléromètre
                // Inclinaison vers la gauche
                if (x < -2) {
                    currentDirection = "left"
                } else if (x > 2) { // Inclinaison vers la droite
                    currentDirection = "right"
                } else if (y < -2) { // Inclinaison vers l'avant
                    currentDirection = "up"
                } else if (y > 2) { // Inclinaison vers soi
                    currentDirection = "down"
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }
        }
        sm.registerListener(se, mySensor, SensorManager.SENSOR_DELAY_NORMAL)
    }
}