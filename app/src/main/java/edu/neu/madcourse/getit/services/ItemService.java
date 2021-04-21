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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.neu.madcourse.getit.callbacks.ItemServiceCallbacks;
import edu.neu.madcourse.getit.models.Item;
import edu.neu.madcourse.getit.models.User;

public class ItemService {

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

    public void createItem(Item item, ItemServiceCallbacks.CreateItemTaskCallback callback) {
        String newItemsDocId = items.document().getId();
        Map<String, Object> newItem = new HashMap<>();
        newItem.put("name", item.getName());
        newItem.put("instructions", item.getInstructions());
        newItem.put("preferredStore", item.getPreferredStore());
        newItem.put("preferredBrand", item.getPreferredBrand());
        newItem.put("quantity", item.getQuantity());
        newItem.put("postedDateTime", item.getPostedDateTime());
        newItem.put("userPosted", item.getUserPosted());
        newItem.put("userGettingIt", item.getUserGettingIt());
//        newItem.put("mImageBitmap", item.getImageBitmap());
        newItem.put("imageBitmap", null);

        items.document(newItemsDocId)
                .set(newItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(CREATE_ITEM_STATUS, "Success");
                        callback.onComplete(true, newItemsDocId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(CREATE_ITEM_STATUS, "Failed");
                        callback.onComplete(false, newItemsDocId);
                    }
                });
    }

    public void getItemByItemId(String itemId, ItemServiceCallbacks.GetItemByItemIdTaskCallback callback) {
        items.document(itemId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                Item item = new Item();
                if(documentSnapshot != null) {
                    Map<String, Object> item_map = documentSnapshot.getData();
                    item.setName(item_map.get("name").toString());
                    item.setQuantity(item_map.get("quantity").toString());
                    item.setInstructions(item_map.get("instructions").toString());
                    item.setPreferredBrand(item_map.get("preferredBrand").toString());
                    item.setPreferredStore(item_map.get("preferredStore").toString());
                    if(item_map.get("userPosted") != null) {
                        item.setUserPosted(new User((HashMap) item_map.get("userPosted")));
                    }
                    if(item_map.get("userGettingIt") != null) {
                        item.setUserPosted(new User((HashMap) item_map.get("userGettingIt")));
                    }
                    item.setPostedDateTime(item_map.get("postedDateTime").toString());
                    // TODO: set item image bitmap as well
                } else {
                    Log.d(GET_ITEM_BY_ITEM_ID, "Error getting document: ", task.getException() );
                }

                callback.onComplete(item);
            }
        });
    }
}
