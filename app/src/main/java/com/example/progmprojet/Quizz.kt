package com.example.progmprojet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ListView

class Quizz : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quizz)
        var reponse =ArrayList<String>()
        reponse.add("reponse1")
        reponse.add("reponse2")
        reponse.add("reponse3")
        reponse.add("reponse4")
        var questions = ArrayList<Question> ()
        questions.add(Question("question1",reponse,1,-1,))
        questions.add(Question("question2",reponse,1,-1))

        val listAdapt=CustomListAdapter(this,questions)
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
            main.putExtra("input",""+score)
            setResult(RESULT_OK,main)
            finish()
        }
    }
}