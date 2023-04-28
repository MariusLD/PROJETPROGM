package com.example.progmprojet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Ranking : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)
        val type = intent.getStringExtra("type")
        val dbRef = FirebaseDatabase.getInstance().getReference().child(type!!)

        val rankingContainer: LinearLayout = findViewById(R.id.ranking_container)
        val view = layoutInflater.inflate(R.layout.rank_list_item, null)
        val nameView: TextView = view.findViewById(R.id.tv_name)
        val scoreView: TextView = view.findViewById(R.id.tv_score)
        val rankView : TextView = view.findViewById(R.id.tv_rank)
        rankView.text = "Rank"
        nameView.text = "Player"
        scoreView.text = "Score"
        rankingContainer.addView(view)
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

                var rank = 1
                for (obj in objectsList) {
                    // Create a view for the object and its score
                    val view = layoutInflater.inflate(R.layout.rank_list_item, null)
                    val nameView: TextView = view.findViewById(R.id.tv_name)
                    val scoreView: TextView = view.findViewById(R.id.tv_score)
                    val rankView : TextView = view.findViewById(R.id.tv_rank)
                    if (rank==1) {
                        rankView.text = "🥇"
                    } else if (rank==2) {
                        rankView.text = "🥈"
                    } else if (rank==3) {
                        rankView.text = "🥉"
                    } else {
                        rankView.text = rank.toString()
                    }
                    rank++
                    nameView.text = obj.first // Player ID 4 the moment
                    scoreView.text = obj.second.toString()

                    // Add the view to the ranking container
                    rankingContainer.addView(view)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        val back: ImageButton = findViewById(R.id.back)
        back.setOnClickListener{
            // Start Activity Leaderboard
            val intent = Intent(this, Leaderboard::class.java)
            startActivity(intent)
        }
    }
}