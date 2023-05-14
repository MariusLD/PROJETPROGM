package com.example.progmprojet
import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*


class CustomListAdapterSound(private val context: Context, private val question: ArrayList<QuestionSound>) : BaseAdapter(),
    ListAdapter {

    override fun getCount(): Int {
        return question.size;
    }

    override fun getItem(pos: Int): Any {
        return question[pos];
    }

    override fun getItemId(pos: Int): Long {
        return pos.toLong();
    }

    override fun getView(pos: Int, p1: View?, p2: ViewGroup?): View {
        var view =p1
        if(view == null){
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.custom_list_sound, p2, false)
        }

        val enonce = view!!.findViewById<TextView>(R.id.textView3)
        enonce.text = question[pos].enonce

        val reponse1=view!!.findViewById<RadioButton>(R.id.radioButtonS)
        reponse1.text= question[pos].reponses[0]
        val reponse2=view!!.findViewById<RadioButton>(R.id.radioButtonS2)
        reponse2.text= question[pos].reponses[1]
        val reponse3=view!!.findViewById<RadioButton>(R.id.radioButtonS3)
        reponse3.text= question[pos].reponses[2]
        val reponse4=view!!.findViewById<RadioButton>(R.id.radioButtonS4)
        reponse4.text= question[pos].reponses[3]

        reponse1.setOnClickListener {
            reponse1.isChecked = true
            reponse2.isChecked=false
            reponse3.isChecked=false
            reponse4.isChecked=false
            question[pos].propReponse=0
        }

        reponse2.setOnClickListener {
            reponse1.isChecked = false
            reponse2.isChecked=true
            reponse3.isChecked=false
            reponse4.isChecked=false
            question[pos].propReponse=1
        }

        reponse3.setOnClickListener {
            reponse1.isChecked = false
            reponse2.isChecked=false
            reponse3.isChecked=true
            reponse4.isChecked=false
            question[pos].propReponse=2
        }

        reponse4.setOnClickListener {
            reponse1.isChecked = false
            reponse2.isChecked=false
            reponse3.isChecked=false
            reponse4.isChecked=true
            question[pos].propReponse=3
        }

        val ecouter=view.findViewById<Button>(R.id.buttonEcouter)
        val mediaPlayer = MediaPlayer.create(context, question[pos].music)
        var joue=false
        mediaPlayer.setOnCompletionListener {
            // Le MediaPlayer a terminé de jouer le fichier audio
            ecouter.text = "ECOUTER"
            joue=false

        }
        ecouter.setOnClickListener{
            if (mediaPlayer.isPlaying) {
                // Le MediaPlayer est en train de jouer
                mediaPlayer.stop()
                ecouter.text = "ECOUTER"
                joue=true

            } else {
                // Le MediaPlayer n'est pas en train de jouer
                if(joue){
                    mediaPlayer.prepare()
                }
                mediaPlayer.start()
                ecouter.text = "STOP"
                joue=false

            }
        }
        return view
    }
}