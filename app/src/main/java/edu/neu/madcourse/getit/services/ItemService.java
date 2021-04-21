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
        newItem.put("mName", item.getName());
        newItem.put("mInstructions", item.getInstructions());
        newItem.put("mPreferredStore", item.getPreferredStore());
        newItem.put("mPreferredBrand", item.getPreferredBrand());
        newItem.put("mQuantity", item.getQuantity());
        newItem.put("mPostedDateTime", item.getPostedDateTime());
        newItem.put("mUserPosted", item.getUserPosted());
        newItem.put("mUserGettingIt", item.getUserGettingIt());
//        newItem.put("mImageBitmap", item.getImageBitmap());
        newItem.put("mImageBitmap", null);

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

    private Map<String, String> getUserData(User user) {
        Map<String, String> map = new HashMap<>();
        map.put("user_email", user.getUserEmail());
        map.put("user_full_name", user.getFullName());
        return map;
    }

    public void getItemByItemId(String itemId, ItemServiceCallbacks.GetItemByItemIdTaskCallback callback) {
        items.document(itemId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                Item item = null;
                if(documentSnapshot != null) {
                    item = (Item) documentSnapshot.toObject(Item.class);
                } else {
                    Log.d(GET_ITEM_BY_ITEM_ID, "Error getting document: ", task.getException() );
                }

                callback.onComplete(item);
            }
        });
    }
}
