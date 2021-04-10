package edu.neu.madcourse.getit.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.neu.madcourse.getit.models.UserModel;

public class UserService {
    private static final String CREATE_USER_STATUS = "CREATE_USER_STATUS";
    private static final String GET_USER_BY_USER_NAME = "GET_USER_BY_USER_NAME";

    FirebaseFirestore db;
    CollectionReference users;
    CollectionReference groups;
    CollectionReference items;

    public UserService() {
        db = FirebaseFirestore.getInstance();
        users = db.collection("users");
        groups = db.collection("groups");
        items = db.collection("items");
    }

    public void createUser(String userName){

        Map<String, Object> user = new HashMap<>();
        user.put("user_name", userName);
        user.put("score", 0);
        user.put("groups", Arrays.asList());
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

    public User getUserByUsername(String userName) {
        Query query = users.whereEqualTo("user_name", userName);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(GET_USER_BY_USER_NAME, document.getId() + " => " + document.getData());

                        UserModel userModel = (UserModel) document.toObject(UserModel.class);

                        Log.d(GET_USER_BY_USER_NAME, "user details----------------------");
                        Log.d(GET_USER_BY_USER_NAME, "user_name----:" + userModel.getUser_name());
                        Log.d(GET_USER_BY_USER_NAME, "user score----:" + userModel.getScore());
                        Log.d(GET_USER_BY_USER_NAME, "groups list ----:" + userModel.getGroups());
                    }
                } else {
                    Log.d(GET_USER_BY_USER_NAME, "Error getting document: ", task.getException());
                }
            }
        });

        return null;
    }

    public void addUserToGroup(String userName, String groupName) {
        Query query = users.whereEqualTo("user_name", userName);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(GET_USER_BY_USER_NAME, document.getId() + " => " + document.getData());

                        UserModel userModel = (UserModel) document.toObject(UserModel.class);

                        Log.d(GET_USER_BY_USER_NAME, "groups list ----:" + userModel.getGroups());
                        users.document(document.getId()).update("groups", FieldValue.arrayUnion(groupName));
                    }
                } else {
                    Log.d(GET_USER_BY_USER_NAME, "Error getting document: ", task.getException());
                }
            }
        });
    }

}
