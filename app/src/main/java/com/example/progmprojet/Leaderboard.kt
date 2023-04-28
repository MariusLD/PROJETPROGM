package com.example.progmprojet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.example.progmprojet.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Leaderboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)
        val back: ImageButton = findViewById(R.id.back)
        val leaderboardContainer: LinearLayout = findViewById(R.id.leaderboard_container)
        val dbRef = FirebaseDatabase.getInstance().getReference()

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children) {
                    val type = ds.key
                    val view: View = layoutInflater.inflate(R.layout.card, null)
                    val nameView : TextView = view.findViewById(R.id.name)
                    val ranking : Button = view.findViewById(R.id.ranking)
                    nameView.text = type
                    leaderboardContainer.addView(view)

                    ranking.setOnClickListener {
                        val intent = Intent(this@Leaderboard, Ranking::class.java)
                        intent.putExtra("type", type)
                        startActivity(intent)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        back.setOnClickListener{
            // Start Activity Leaderboard
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}