package com.egkhan.firebasesetup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by ipnordicUser2 on 7/26/2017.
 */

public class AddToDatabase  extends AppCompatActivity{
    private static final String TAG = "AddToDatabase";
    
    Button addToDatabaseBtn;
    EditText addNewFoodET;
    
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth fAuth;
    FirebaseAuth.AuthStateListener fAuthStateListener;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_to_database_layout);
        
        addToDatabaseBtn = (Button) findViewById(R.id.addNewFoodBtn);
        addNewFoodET = (EditText) findViewById(R.id.addNewFoodEt);
        
        fAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    
        fAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    ToastMessage("Successfully sign in with "+user.getEmail());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    ToastMessage("Signed out successfully");
                }
            }
        };
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Object value = dataSnapshot.getValue();
                Log.d(TAG, "Value is: " + value);
            }
        
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        addToDatabaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Attempting to add object to database");
                String newFood = addNewFoodET.getText().toString();
                if(!newFood.equals(""))
                {
                    FirebaseUser user = fAuth.getCurrentUser();
                    String userId = user.getUid();
                    databaseReference.child(userId).child("Food").child("Favorite Foods").child(newFood).setValue("true");
                    addNewFoodET.setText("");
                }
            }
        });
    }
    private void ToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onStart() {
        super.onStart();
        fAuth.addAuthStateListener(fAuthStateListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (fAuthStateListener != null) {
            fAuth.removeAuthStateListener(fAuthStateListener);
        }
    }
}
