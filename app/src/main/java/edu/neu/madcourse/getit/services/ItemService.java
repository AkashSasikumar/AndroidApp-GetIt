package edu.neu.madcourse.getit.services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.neu.madcourse.getit.callbacks.GroupServiceCallbacks;
import edu.neu.madcourse.getit.callbacks.ItemServiceCallbacks;
import edu.neu.madcourse.getit.models.Group;
import edu.neu.madcourse.getit.models.Item;
import edu.neu.madcourse.getit.models.User;

public class ItemService {

    private static final String CREATE_ITEM_STATUS = "CREATE_ITEM_STATUS";
    private static final String GET_ITEM_BY_ITEM_ID = "GET_ITEM_BY_ITEM_ID";
    private static final String INCREMENT_USER_SCORE = "INCREMENT_USER_SCORE";

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

    public void createItem(Item item, User loggedInUser, ItemServiceCallbacks.CreateItemTaskCallback callback) {
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
        newItem.put("imageBitmap", getEncodedStringOfBitmap(item.getImageBitmap()));

        items.document(newItemsDocId)
                .set(newItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // add the item under users list of item posted
                        users.document(loggedInUser.getUserId()).update("user_items_posted",
                                FieldValue.arrayUnion(newItemsDocId));

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
                    item.setItemID(itemId);
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
                    if(item_map.get("imageBitmap") != null) {
                        item.setImageBitmap(getDecodedBitmapFromString(item_map.get("imageBitmap").toString()));
                    }
                } else {
                    Log.d(GET_ITEM_BY_ITEM_ID, "Error getting document: ", task.getException() );
                }

                callback.onComplete(item);
            }
        });
    }

    public void addUserGettingTheItem(String itemID, User userGetting,
                                      ItemServiceCallbacks.addUserGettingTheItemTaskCallback callback){

        // update items database
        items.document(itemID).update("userGettingIt",userGetting );

        // add item to users list of items getting
        users.document(userGetting.getUserId()).update("user_items_getting", FieldValue.arrayUnion(itemID));

        // update score of the user getting the item
        final DocumentReference userRef = users.document(userGetting.getUserId());
        db.runTransaction(new Transaction.Function<Void>() {

            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(userRef);
                transaction.update(userRef, "user_score", FieldValue.increment(1));
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(INCREMENT_USER_SCORE, "Incremented score of the user!");
                callback.onComplete(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(INCREMENT_USER_SCORE, "Could not increment the score of the user!");
                // returning success even if score was not updated
                callback.onComplete(true);
            }
        });

    }

    private String getEncodedStringOfBitmap(Bitmap imageBitmap) {
        // First compress this image and then encode it
        int newHeight = (int) ( imageBitmap.getHeight() * (512.0 / imageBitmap.getWidth()) );
        Bitmap scaledImageBitmap = Bitmap.createScaledBitmap(imageBitmap, 512, newHeight, true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        scaledImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    private Bitmap getDecodedBitmapFromString(String imageEncoded) {
        byte[] decodedByteArray = Base64.decode(imageEncoded, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }
}
