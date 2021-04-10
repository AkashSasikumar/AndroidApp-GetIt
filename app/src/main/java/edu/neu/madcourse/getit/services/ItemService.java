package edu.neu.madcourse.getit.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ItemService {

    private static final String CREATE_ITEM_STATUS = "CREATE_ITEM_STATUS";
    private static final String GET_ITEM_BY_GROUP_AND_USER_NAME = "GET_ITEM_BY_GROUP_AND_USER_NAME";

    FirebaseFirestore db;
    CollectionReference users;
    CollectionReference groups;
    CollectionReference items;

    public ItemService() {
        db = FirebaseFirestore.getInstance();
        users = db.collection("users");
        groups = db.collection("groups");
        items = db.collection("items");
    }

    public void createItem(String itemName, String groupName, String userName) {
        String newItemsDocId = items.document().getId();

        Map<String, Object> newItem = new HashMap<>();
        newItem.put("item_name", itemName);
        newItem.put("user_to_request", userName);
        newItem.put("user_to_purchase", "");
        newItem.put("group_name", groupName);
        newItem.put("date_added", Timestamp.now());
        newItem.put("date_purchased", null);

        items.document(newItemsDocId)
                .set(newItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(CREATE_ITEM_STATUS, "Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(CREATE_ITEM_STATUS, "Failed");
                    }
                });
    }
}
