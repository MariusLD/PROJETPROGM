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
            40,
            40
        )
        val centerX = board.width / 2 - pig.width / 2
        val centerY = board.height / 2 - pig.height / 2
        pig.translationX = centerX.toFloat()
        pig.translationY = centerY.toFloat()

        board.addView(pig)
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
            val distanceThreshold = 100

            val distance = sqrt((pig.x - food.x).pow(2) + (pig.y - food.y).pow(2))

            if (distance < distanceThreshold) {

                // On aggrandit le cochon
                val newPig = ImageView(this)
                newPig.setImageResource(R.drawable.pig)
                newPig.layoutParams = ViewGroup.LayoutParams(
                    40,
                    40
                )
                board.addView(newPig)
                tracking.add(newPig)

                // We generate a new apple
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

        // Function to end the game when the snake hits the wall or itself
        fun endGame() {
            var scoreInt: Int = score.text.toString().toInt()
            scoreInt=0
            score.text = scoreInt.toString()
            // We delete the snake body, the traking list if it has body parts
            if (tracking.size > 1) {
                for (i in tracking.size - 1  downTo 1) {
                    board.removeView(tracking[i])
                    tracking.removeAt(i)
                }
            }
        }

        // Method to detect collision with the snake body
        fun checkSnakeCollision() {
            val snakeHead = tracking[0]
            val snakeHeadRect = RectF(snakeHead.x, snakeHead.y, snakeHead.x + snakeHead.width, snakeHead.y + snakeHead.height)
            if (tracking.size > 1) {
                for (i in 1 until tracking.size-1) {
                    val snakeBodyPart = tracking[i]
                    val snakeBodyPartRect = RectF(snakeBodyPart.x, snakeBodyPart.y, snakeBodyPart.x + snakeBodyPart.width, snakeBodyPart.y + snakeBodyPart.height)
                    if (snakeHeadRect.intersect(snakeBodyPartRect)) {
                        endGame()
                    }
                }
            }
        }


        val runnable = object : Runnable {
            override fun run() {

                // We update the body position with some space in-between, except its head
                for (i in tracking.size - 1 downTo 1) {
                    val bodyPart = tracking[i]
                    val previousBodyPart = tracking[i - 1]
                    bodyPart.x = previousBodyPart.x
                    bodyPart.y = previousBodyPart.y
                }

                val translateValue = 40
                val boardHeightLimit = board.height/2
                val boardWidthLimit = board.width/2

                // Use the map limits in your code
                when (currentDirection) {
                    "up" -> {
                        snakeY -= translateValue
                        pig.translationY = snakeY
                        if (snakeY < -boardHeightLimit) {
                            snakeX = (centerX/2).toFloat()
                            snakeY = (centerY/2).toFloat()
                            pig.translationX = snakeX
                            pig.translationY = snakeY
                            endGame()
                        }

                    }
                    "down" -> {
                        snakeY += translateValue
                        pig.translationY = snakeY
                        if (snakeY > boardHeightLimit) {
                            snakeX = (centerX/2).toFloat()
                            snakeY = (centerY/2).toFloat()
                            pig.translationX = snakeX
                            pig.translationY = snakeY
                            endGame()
                        }

                    }
                    "left" -> {
                        snakeX += translateValue
                        pig.translationX = snakeX
                        if (snakeX > boardWidthLimit) {
                            snakeX = (centerX/2).toFloat()
                            snakeY = (centerY/2).toFloat()
                            pig.translationX = snakeX
                            pig.translationY = snakeY
                            endGame()
                        }

                    }
                    "right" -> {
                        snakeX -= translateValue
                        pig.translationX = snakeX
                        if (snakeX < -boardWidthLimit) {
                            snakeX = (centerX/2).toFloat()
                            snakeY = (centerY/2).toFloat()
                            pig.translationX = snakeX
                            pig.translationY = snakeY
                            endGame()
                        }
                    }
                }
                checkFoodCollision()
                checkSnakeCollision()
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