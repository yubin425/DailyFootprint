import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FirebaseManager {
    val firebaseDatabase: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    val databaseReference: DatabaseReference
        get() = firebaseDatabase.reference
}