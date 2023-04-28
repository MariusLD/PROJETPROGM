package com.example.progmprojet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    }
}