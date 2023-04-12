package com.example.progmprojet
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class CustomListAdapter(private val context: Context, private val question: ArrayList<Question>) : BaseAdapter(),
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
            view = inflater.inflate(R.layout.custom_list, p2, false)
        }

        val enonce = view!!.findViewById<TextView>(R.id.textView)
        enonce.text = question[pos].enonce

        val reponse1=view!!.findViewById<TextView>(R.id.radioButton)
        reponse1.text= question[pos].reponses[0]
        val reponse2=view!!.findViewById<TextView>(R.id.radioButton2)
        reponse2.text= question[pos].reponses[1]
        val reponse3=view!!.findViewById<TextView>(R.id.radioButton3)
        reponse3.text= question[pos].reponses[2]
        val reponse4=view!!.findViewById<TextView>(R.id.radioButton4)
        reponse4.text= question[pos].reponses[3]

        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            // Récupérer le RadioButton sélectionné
            val selectedRadioButton = view.findViewById<RadioButton>(checkedId)
            val position = radioGroup.indexOfChild(selectedRadioButton)
            question[pos].propReponse=position

        }
        return view
    }
}