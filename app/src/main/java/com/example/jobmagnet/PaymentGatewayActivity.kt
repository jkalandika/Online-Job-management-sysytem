package com.example.jobmagnet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.jobmagnet.databinding.ActivityPaymentGatewayBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class PaymentGatewayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentGatewayBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentGatewayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().getReference("creditCards")

        binding.saveButton.setOnClickListener {
            val cardName = binding.cardNameInput.text.toString().trim()
            val cardNumber = binding.cardNumberInput.text.toString().trim()
            val expiryDate = binding.expiryDateInput.text.toString().trim()
            val cvv = binding.cvvInput.text.toString().trim()

            val creditCard = CreditCard(cardName, cardNumber, expiryDate, cvv)
            val creditCardRef = database.push()
            creditCardRef.setValue(creditCard)

            startActivity(Intent(this, CompanyDashboardActivity::class.java))
            finish()
        }
    }
}
