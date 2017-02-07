package Backend;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by root on 2/4/17.
 */

public class FirebaseCommunication {
    public int pingCheck;
    FirebaseDatabase database;
    public FirebaseCommunication() {
        database = FirebaseDatabase.getInstance();
        DatabaseReference pingReference = database.getReference("pingState");

        pingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() ){
                    int value = ((Long)(dataSnapshot.getValue())).intValue();
                    pingCheck = value;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void refreshCurrentState(float t1, float t2, float t3, float o2, float co2) {
        DatabaseReference currentState = database.getReference("current_state");
        currentState.child("co2_value").setValue(co2);
        currentState.child("o2_value").setValue(o2);
        currentState.child("temperature_point_1").setValue(t1);
        currentState.child("temperature_point_2").setValue(t2);
        currentState.child("temperature_point_3").setValue(t3);

    }

    public void publicHistoricalState() {
        DatabaseReference history = database.getReference("history");
    }

}
