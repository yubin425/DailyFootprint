
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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

    fun getUID() : String {
        return authInstance.currentUser!!.uid.toString()
    }

    // FirebaseAuth 객체에 접근하는 함수
    fun getFirebaseAuthInstance(): FirebaseAuth {
        return authInstance
    }

    val databaseReference: DatabaseReference
        get() = firebaseDatabase.reference

    val userDatabaseReference : DatabaseReference
        get() = userReference

    fun getName(userCode: String, callback: (String?) -> Unit, onError: (DatabaseError) -> Unit) {
        val userRef = databaseReference.child("user/$userCode/userName")

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.getValue(String::class.java)
                callback(userName.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 오류 처리
                onError(databaseError)
            }
        })
    }
}