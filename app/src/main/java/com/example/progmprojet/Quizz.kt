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
        reponse.add("Dindonneau")
        reponse.add("Porcelet")
        reponse.add("Marcassin")
        reponse.add("porc")
        var questions = ArrayList<Question> ()
        questions.add(Question("Comment s'appelle le bébé de la truie ?",reponse,1,-1,))

        var reponse2 =ArrayList<String>()
        reponse2.add("Cuniculture")
        reponse2.add("Mytiliculture")
        reponse2.add("Sériciculture")
        reponse2.add("Héliciculture")
        questions.add(Question("Comment appelle-t-on l'élevage des lapins de ferme ?",reponse2,0,-1))

        var reponse3 =ArrayList<String>()
        reponse3.add("10 jours")
        reponse3.add("14 jours")
        reponse3.add("21 jours")
        reponse3.add("25 jours")
        questions.add(Question("Combien de temps une poule couve-t-elle ses oeufs ?",reponse3,2,-1))

        var reponse4 =ArrayList<String>()
        reponse4.add("L'oie")
        reponse4.add("La brebis")
        reponse4.add("La dinde")
        reponse4.add("La cane")
        questions.add(Question("De quelle femelle, le jar est-il le mâle ?",reponse4,0,-1))

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
            main.putExtra("input",score)
            setResult(RESULT_OK,main)
            finish()
        }
    }
}