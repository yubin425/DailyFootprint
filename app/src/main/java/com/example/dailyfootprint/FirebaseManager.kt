import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FirebaseManager {
    val firebaseDatabase: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance("https://dailyfootprint-aeac7-default-rtdb.asia-southeast1.firebasedatabase.app")
    }

    val userReference: DatabaseReference by lazy {
        firebaseDatabase.reference.child("user")
    }

    // FirebaseAuth 객체를 싱글톤으로 선언
    val authInstance: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    // FirebaseAuth 객체에 접근하는 함수
    fun getFirebaseAuthInstance(): FirebaseAuth {
        return authInstance
    }

    val databaseReference: DatabaseReference
        get() = firebaseDatabase.reference

    val userDatabaseReference : DatabaseReference
        get() = userReference
}