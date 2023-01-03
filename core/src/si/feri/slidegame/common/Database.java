package si.feri.slidegame.common;

import com.google.firebase.

public class Database {
    // https://mb-hub-default-rtdb.europe-west1.firebasedatabase.app
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("message");

}
