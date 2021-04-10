package edu.neu.madcourse.getit.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import edu.neu.madcourse.getit.models.Group;
import edu.neu.madcourse.getit.models.UserModel;

public class GroupService {

    private static final String CREATE_GROUP_STATUS = "CREATE_GROUP_STATUS";
    private static final String GET_GROUP_BY_GROUP_NAME = "GET_GROUP_BY_GROUP_NAME";

    FirebaseFirestore db;
    CollectionReference users;
    CollectionReference groups;
    CollectionReference items;

    public GroupService() {
        db = FirebaseFirestore.getInstance();
        users = db.collection("users");
        groups = db.collection("groups");
        items = db.collection("items");
    }

    public void createGroup(String groupName) {
        Map<String, Object> newGroup = new HashMap<>();
        newGroup.put("group_name", groupName);
        newGroup.put("items_to_purchase", Arrays.asList());
        newGroup.put("items_purchased", Arrays.asList());
        newGroup.put("users", Arrays.asList());
        groups.document()
                .set(newGroup)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(CREATE_GROUP_STATUS, "Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(CREATE_GROUP_STATUS, "Failed");
                    }
                });
    }

    public void getGroupByGroupName(String groupName) {
        Query query = groups.whereEqualTo("group_name", groupName);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(GET_GROUP_BY_GROUP_NAME, document.getId() + " => " + document.getData());

                        Group group = (Group) document.toObject(Group.class);

                        Log.d(GET_GROUP_BY_GROUP_NAME, "user details----------------------");
                        Log.d(GET_GROUP_BY_GROUP_NAME, "user_name----:" + group.getGroup_name());
                        Log.d(GET_GROUP_BY_GROUP_NAME, "Items in group----:" + group.getItems_purchased());
                        Log.d(GET_GROUP_BY_GROUP_NAME, "Items in group----:" + group.getItems_purchased());
                        Log.d(GET_GROUP_BY_GROUP_NAME, "Users  ----:" + group.getUsers());
                    }
                } else {
                    Log.d(GET_GROUP_BY_GROUP_NAME, "Error getting document: ", task.getException());
                }
            }
        });

    }


}
