import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jobmagnet.Company
import com.google.firebase.database.*

class CompanyViewModel : ViewModel() {

    private val _companies = MutableLiveData<List<Company>>()
    val companies: LiveData<List<Company>> = _companies

    init {
        fetchCompanies()
    }

    private fun fetchCompanies() {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        usersRef.orderByChild("accountType").equalTo("company")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val companyList = mutableListOf<Company>()
                    for (companySnapshot in dataSnapshot.children) {
                        val company = companySnapshot.getValue(Company::class.java)
                        if (company != null) {
                            companyList.add(company)
                        }
                    }
                    _companies.value = companyList
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle the error
                }
            })
    }
}
