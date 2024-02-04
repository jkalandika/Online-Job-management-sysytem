package com.example.jobmagnet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobmagnet.databinding.ActivitySearchJobBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchJobActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchJobBinding
    private lateinit var database: DatabaseReference
    private lateinit var registeredJobsDatabase: DatabaseReference
    private val allJobs = mutableListOf<Job>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchJobBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().getReference("jobs")
        registeredJobsDatabase = FirebaseDatabase.getInstance().getReference("registeredJobs")

        binding.searchJobRecyclerView.layoutManager = LinearLayoutManager(this)
        fetchJobs()

        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filterJobs(s.toString())
            }
        })

        binding.homeButton.setOnClickListener {
            startActivity(Intent(this, FreelancerDashboardActivity::class.java))
            finish()
        }
    }

    private fun fetchJobs() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                allJobs.clear()
                for (jobSnapshot in dataSnapshot.children) {
                    val job = jobSnapshot.getValue(Job::class.java)
                    job?.let { allJobs.add(it) }
                }
                displayJobs(allJobs)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun filterJobs(query: String) {
        val filteredJobs = allJobs.filter { job -> job.title.contains(query, ignoreCase = true) }
        displayJobs(filteredJobs)
    }

    private fun displayJobs(jobs: List<Job>) {
        binding.searchJobRecyclerView.adapter = JobAdapter(jobs) { job ->
            // Handle job registration
            val registeredJobRef = registeredJobsDatabase.push()
            registeredJobRef.setValue(job)
        }
    }
}

