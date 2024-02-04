package com.example.jobmagnet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobmagnet.databinding.ActivityViewRegisteredJobsBinding
import com.google.firebase.database.*

class ViewRegisteredJobsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewRegisteredJobsBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewRegisteredJobsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registeredJobsRecyclerView.layoutManager = LinearLayoutManager(this)

        database = FirebaseDatabase.getInstance().getReference("registeredJobs")

        fetchRegisteredJobs()

        // Set up Home button click listener
        binding.homeButton.setOnClickListener {
            startActivity(Intent(this, FreelancerDashboardActivity::class.java))
            finish()
        }
    }

    private fun fetchRegisteredJobs() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val registeredJobs = mutableListOf<Job>()
                for (jobSnapshot in snapshot.children) {
                    val job = jobSnapshot.getValue(Job::class.java)
                    if (job != null) {
                        registeredJobs.add(job)
                    }
                }
                displayRegisteredJobs(registeredJobs)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun displayRegisteredJobs(registeredJobs: List<Job>) {
        binding.registeredJobsRecyclerView.adapter = RegisteredJobAdapter(registeredJobs) { job ->
            // Handle any click events on the job items
        }
    }
}
