package com.example.progmprojet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ListView

class QuizzSound : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quizz_sound)
        var reponse =ArrayList<String>()
        reponse.add("Un enfant")
        reponse.add("Macaque")
        reponse.add("Singe hurleur")
        reponse.add("Hyène")
        var questions = ArrayList<QuestionSound> ()
        questions.add(QuestionSound("Devine le nom de l'animal",reponse,3,-1,R.raw.hyene))

        var reponse2 =ArrayList<String>()
        reponse2.add("poule")
        reponse2.add("Canard")
        reponse2.add("Dinde")
        reponse2.add("Oie")
        questions.add(QuestionSound("Devine le nom de l'animal",reponse2,2,-1,R.raw.dindon))


        var reponse3 =ArrayList<String>()
        reponse3.add("Rouge gorge")
        reponse3.add("Moineau")
        reponse3.add("Mésange")
        reponse3.add("Hirondelle")
        questions.add(QuestionSound("Devine le nom de l'animal",reponse3,3,-1,R.raw.hirondelle))

        var reponse4 =ArrayList<String>()
        reponse4.add("Bouc")
        reponse4.add("Chèvre")
        reponse4.add("Mouton")
        reponse4.add("Agneau")
        questions.add(QuestionSound("Devine le nom de l'animal",reponse4,1,-1,R.raw.chevre))
        val listAdapt=CustomListAdapterSound(this,questions)
        val listView: ListView = findViewById(R.id.listView)
        listView.adapter = listAdapt

        val button: Button = findViewById(R.id.button)
        var score =0
        button.setOnClickListener{
            for(question in questions){
                if(question.reponse==question.propReponse){
                    score= score +1
                }
            }

            val main : Intent =  Intent(this,MainActivity::class.java)
            main.putExtra("input",score)
            setResult(RESULT_OK,main)
            finish()
        }
    }
}