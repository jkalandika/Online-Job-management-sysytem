package com.example.jobmagnet

import CompanyViewModel
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jobmagnet.CompanyAdapter
import com.example.jobmagnet.databinding.ActivityCompanyNotifyBinding

class CompanyNotify : AppCompatActivity() {

    private lateinit var binding: ActivityCompanyNotifyBinding
    private lateinit var viewModel: CompanyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCompanyNotifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(CompanyViewModel::class.java)

        initRecyclerView()
        observeCompanies()
    }

    private fun initRecyclerView() {
        binding.rvCompanies.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.rvCompanies.setHasFixedSize(true)
    }

    private fun observeCompanies() {
        viewModel.companies.observe(this) { companies ->
            val adapter = CompanyAdapter(companies)
            binding.rvCompanies.adapter = adapter
        }
    }
}
