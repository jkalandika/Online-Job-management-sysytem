package com.example.jobmagnet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.jobmagnet.databinding.RegisteredJobBinding

class RegisteredJobAdapter(
    private val registeredJobs: List<Job>,
    private val onItemClick: (Job) -> Unit
) : RecyclerView.Adapter<RegisteredJobAdapter.RegisteredJobViewHolder>() {

    inner class RegisteredJobViewHolder(private val binding: RegisteredJobBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(job: Job) {
            binding.apply {
                // Update the view bindings according to your new registered_job.xml layout
                title.text = job.title
                description.text = job.description
                price.text = job.price.toString()
                topic.text = job.topic
                root.setOnClickListener { onItemClick(job) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegisteredJobViewHolder {
        val binding = RegisteredJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RegisteredJobViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RegisteredJobViewHolder, position: Int) {
        val job = registeredJobs[position]
        holder.bind(job)
    }

    override fun getItemCount(): Int = registeredJobs.size
}
