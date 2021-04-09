package edu.neu.madcourse.getit.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BackendUserService {
    private static final String CREATE_USER_STATUS = "CREATE_USER_STATUS";

    FirebaseFirestore db;
    CollectionReference users;
    CollectionReference groups;
    CollectionReference items;

    public BackendUserService() {
        db = FirebaseFirestore.getInstance();
        users = db.collection("users");
        groups = db.collection("groups");
        items = db.collection("items");
    }

    public void createUser(String userName){

        Map<String, Object> user = new HashMap<>();
        user.put("user_name", userName);
        user.put("score", 10);
        user.put("groups", Arrays.asList("test_group0", "test_group1"));
        users.document()
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(CREATE_USER_STATUS, "Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(CREATE_USER_STATUS, "Failed");
                    }
                });
    }

    public



    public String getUserByUserId(String userId) {
        return null;
    }

    public String getGroupsByUserId(String userId) {
        return null;
    }

}
