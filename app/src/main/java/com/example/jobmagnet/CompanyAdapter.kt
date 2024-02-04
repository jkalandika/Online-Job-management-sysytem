package com.example.jobmagnet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.jobmagnet.databinding.CompanyItemBinding

class CompanyAdapter(private val companies: List<Company>) : RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        val binding = CompanyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CompanyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        val company = companies[position]
        holder.bind(company)
    }

    override fun getItemCount(): Int {
        return companies.size
    }

    inner class CompanyViewHolder(private val binding: CompanyItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(company: Company) {
            with(binding) {
                companyNameTextView.text = company.companyName
                companyTypeTextView.text = company.companyType
                availableJobsTextView.text = company.availableJobs
                contactNumberTextView.text = company.contactNumber
                descriptionTextView.text = company.description
                emailTextView.text = company.email
                addressTextView.text = company.address

                Glide.with(itemView.context)
                    .load(company.profileImage)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .into(companyImageView)

                closeButton.setOnClickListener {
                    binding.root.visibility = View.GONE
                }
            }
        }
    }


}
