package com.example.progmprojet

import android.content.Intent
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
        var texte:String=""
        if(extraValue==1){
            texte="Vous avez Gagné"
        }else if(extraValue==0){
            texte="Egaliter"
        }else if(extraValue==-1){
            texte="Vous avez perdu !"
        }
        val textView : TextView =findViewById(R.id.textViewWinLoose)
        textView.setText(texte)

        val button: Button = findViewById(R.id.buttonMenu)

        button.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val play: Button = findViewById(R.id.buttonRejouer)
        play.setOnClickListener{
            val intent = Intent(this, WifiDirectActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}