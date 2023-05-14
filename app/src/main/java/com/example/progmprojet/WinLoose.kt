package com.example.progmprojet

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class WinLoose : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_win_loose)

        val intent = getIntent()
        val extraValue = intent.getIntExtra("win",-5)
        val score = intent.getIntExtra("score",-5)
        var texte:String=""
        if(extraValue==1 ||extraValue==10){
            texte="Vous avez Gagné"
        }else if(extraValue==0){
            texte="Egaliter"
        }else if(extraValue==-1){
            texte="Vous avez perdu !"
        }
        val textView : TextView =findViewById(R.id.textViewWinLoose)
        textView.setText(texte)

        val textScore : TextView =findViewById(R.id.textScore)
        textScore.setText("Vous avez:"+score+" points !")
        val button: Button = findViewById(R.id.buttonMenu)
        var mediaPlayer = MediaPlayer.create(this, R.raw.loose)
        if(extraValue >-1){
            mediaPlayer = MediaPlayer.create(this, R.raw.win)
        }
        mediaPlayer.start()
        button.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            mediaPlayer.stop()
            finish()
        }

        val play: Button = findViewById(R.id.buttonRejouer)
        play.setOnClickListener{
            if(extraValue==10){
                val main : Intent =  Intent(this,MainActivity::class.java)
                main.putExtra("rejouer",1)
                setResult(RESULT_OK,main)
                mediaPlayer.stop()
                finish()
            }else{
                val intent = Intent(this, WifiDirectActivity::class.java)
                startActivity(intent)
                mediaPlayer.stop()
                finish()
            }
        }
    }
}