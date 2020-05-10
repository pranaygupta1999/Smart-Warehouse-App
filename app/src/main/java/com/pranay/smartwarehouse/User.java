package com.pranay.smartwarehouse;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {
    public String uid;
    public String name;
    public String email;
    public HashMap<String, String> listOfDevices;

    public User() {

    }

    public User(FirebaseUser firebaseUser) {
        this.uid = firebaseUser.getUid();
        this.email = firebaseUser.getEmail();
    }

    public User(String uid, String name, String email, HashMap<String, String> listOfDevices) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.listOfDevices = listOfDevices;
    }


    public static void register(final String name, final String email, final String password, final boolean proceedToLogin, final Activity activity, final FirebaseAuth firebaseAuth) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = task.getResult().getUser();
                            User user = new User(firebaseUser);
                            user.name = name;
                            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users");
                            userReference.child(user.uid).setValue(user);

                            if (proceedToLogin) {
                                login(email, password, activity, firebaseAuth);
                            }
                        } else {
                            Log.v("Pranay", "Some error in registration sir");
                        }
                    }
                });
    }

    public static void login(String email, String password, final Activity activity, final FirebaseAuth firebaseAuth) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            activity.startActivity(new Intent(activity.getApplicationContext(), MainActivity.class));
                            activity.finish();
                        }
                    }
                });
    }
}
