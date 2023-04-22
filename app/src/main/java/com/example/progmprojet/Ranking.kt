package com.example.progmprojet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Ranking : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)
        val dbRef = FirebaseDatabase.getInstance().getReference()

        val rankingContainer: LinearLayout = findViewById(R.id.object_list_container)

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val objectsList = mutableListOf<Pair<String, Int>>()
                for (ds in snapshot.children) {
                    val objectType = ds.key
                    val objectScore = ds.getValue(Int::class.java)
                    if (objectScore != null) {
                        objectsList.add(Pair(objectType!!, objectScore))
                    }
                }
                // Sort the objectsList in descending order of their scores
                objectsList.sortByDescending { it.second }

                for (obj in objectsList) {
                    // Create a view for the object and its score
                    val view = layoutInflater.inflate(R.layout.object_list_item, null)
                    val nameView: TextView = view.findViewById(R.id.object_name)
                    val scoreView: TextView = view.findViewById(R.id.object_score)
                    nameView.text = obj.first
                    scoreView.text = obj.second.toString()

                    // Add the view to the ranking container
                    rankingContainer.addView(view)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        val back: ImageButton = findViewById(R.id.back)
        back.setOnClickListener{
            // Start Activity Leaderboard
            val intent = Intent(this, Leaderboard::class.java)
            startActivity(intent)
        }
    }
}