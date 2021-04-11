package edu.neu.madcourse.getit.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import edu.neu.madcourse.getit.models.Group;
import edu.neu.madcourse.getit.models.Item;

public class    ItemService {

    private static final String CREATE_ITEM_STATUS = "CREATE_ITEM_STATUS";
    private static final String GET_ITEM_BY_ITEM_ID = "GET_ITEM_BY_ITEM_ID";

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

    //TODO: Make the method to return boolean
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


    //TODO: Make method to return object
    public void getItemByItemId(String itemId) {
        items.document(itemId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();

                if(documentSnapshot != null) {
                    Item item = (Item) documentSnapshot.toObject(Item.class);
                    Log.d(GET_ITEM_BY_ITEM_ID, "Item name: " + item.getItem_name());
                    Log.d(GET_ITEM_BY_ITEM_ID, "Item group name: " + item.getGroup_name());
                    Log.d(GET_ITEM_BY_ITEM_ID, "Item purchase date: " + item.getDate_purchase());
                    Log.d(GET_ITEM_BY_ITEM_ID, "Item added date: " + item.getDate_added());
                } else {
                    Log.d(GET_ITEM_BY_ITEM_ID, "Error getting document: ", task.getException() );
                }
            }
        });
    }
}
