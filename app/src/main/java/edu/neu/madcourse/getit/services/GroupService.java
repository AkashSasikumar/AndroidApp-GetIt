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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import edu.neu.madcourse.getit.models.Group;
import edu.neu.madcourse.getit.models.UserModel;

public class GroupService {

    private static final String CREATE_GROUP_STATUS = "CREATE_GROUP_STATUS";
    private static final String GET_GROUP_BY_GROUP_NAME = "GET_GROUP_BY_GROUP_NAME";
    private static final String ADD_ITEMID_TO_TOBE_PURCHASED_CATEGORY = "ADD_ITEMID_TO_TOBE_PURCHASED_CATEGORY";
    private static final String ADD_ITEMID_TO_PURCHASED_CATEGORY = "ADD_ITEMID_TO_PURCHASED_CATEGORY";
    private static final String REMOVE_ITEMID_FROM_TOBE_PURCHASED_CATEGORY = "REMOVE_ITEMID_FROM_TO_PURCHASED_CATEGORY";
    private static final String REMOVE_ITEMID_FROM_PURCHASED_CATEGORY = "REMOVE_ITEMID_FROM_PURCHASED_CATEGORY";

    Group group;
    FirebaseFirestore db;
    CollectionReference users;
    CollectionReference groups;
    CollectionReference items;
    private boolean createGroupSuccessFlag;

    public GroupService() {
        db = FirebaseFirestore.getInstance();
        users = db.collection("users");
        groups = db.collection("groups");
        items = db.collection("items");
    }

    public boolean createGroup(String groupName) {
        Map<String, Object> newGroup = new HashMap<>();
        newGroup.put("group_name", groupName);
        newGroup.put("items_to_purchase", Arrays.asList());
        newGroup.put("items_purchased", Arrays.asList());
        newGroup.put("users", Arrays.asList());

        createGroupSuccessFlag = false;
        ServiceTaskHandler.performTask(() -> createGroupAsyncTask(newGroup));

        return createGroupSuccessFlag;
    }

    private void createGroupAsyncTask(Map<String, Object> newGroup) {
        groups.document()
                .set(newGroup)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(CREATE_GROUP_STATUS, "Success");
                        createGroupSuccessFlag = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(CREATE_GROUP_STATUS, "Failed");
                        createGroupSuccessFlag = false;
                    }
                });
    }


    //TODO: Make this method return Group object.
    public Group getGroupByGroupName(String groupName) {
        Query query = groups.whereEqualTo("group_name", groupName);
        ServiceTaskHandler.performTask(() -> getGroupAsyncTask(query));

        if(group == null) {
            // handle this null return in a better way...in case task was not successful
        }

        Log.d(GET_GROUP_BY_GROUP_NAME, "user details----------------------");
        Log.d(GET_GROUP_BY_GROUP_NAME, "user_name----:" + group.getGroup_name());
        Log.d(GET_GROUP_BY_GROUP_NAME, "Items in group----:" + group.getItems_purchased());
        Log.d(GET_GROUP_BY_GROUP_NAME, "Items in group----:" + group.getItems_purchased());
        Log.d(GET_GROUP_BY_GROUP_NAME, "Users  ----:" + group.getUsers());
        Log.d(GET_GROUP_BY_GROUP_NAME, "groupid  ----:" + group.getGroupId());

        return group;
    }

    private void getGroupAsyncTask(Query query) {
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(GET_GROUP_BY_GROUP_NAME, document.getId() + " => " + document.getData());
                        group = (Group) document.toObject(Group.class);
                        group.setGroupId(document.getId());
                    }
                } else {
                    Log.d(GET_GROUP_BY_GROUP_NAME, "Error getting document: ", task.getException());
                    group = null;
                }
            }
        });
    }

    public boolean addItemTo_ToBePurchasedCategory(String groupId, String itemId) {
        try {
            groups.document(groupId).update("items_to_purchase", FieldValue.arrayUnion(itemId));
            Log.d(ADD_ITEMID_TO_TOBE_PURCHASED_CATEGORY, "Success");
            return true;
        } catch (Error e) {
            Log.d(ADD_ITEMID_TO_TOBE_PURCHASED_CATEGORY, "Failed");
            return false;
        }
    }

    public boolean removeItemFrom_ToBePurchasedCategory(String groupId, String itemId) {
        try {
            groups.document(groupId).update("items_to_purchase", FieldValue.arrayRemove(itemId));
            Log.d(REMOVE_ITEMID_FROM_TOBE_PURCHASED_CATEGORY, "Success");
            return true;
        } catch (Error e) {
            Log.d(REMOVE_ITEMID_FROM_TOBE_PURCHASED_CATEGORY, "Failed");
            return false;
        }
    }

    public boolean addItemTo_AlreadyPurchasedCategory(String groupId, String itemId) {
        try {
            groups.document(groupId).update("items_purchased", FieldValue.arrayUnion(itemId));
            Log.d(ADD_ITEMID_TO_PURCHASED_CATEGORY, "Success");
            return true;
        } catch (Error e) {
            Log.d(ADD_ITEMID_TO_PURCHASED_CATEGORY, "Failed");
            return false;
        }
    }

    public boolean removeItemFrom_AlreadyPurchasedCategory(String groupId, String itemId) {
        try {
            groups.document(groupId).update("items_purchased", FieldValue.arrayRemove(itemId));
            Log.d(REMOVE_ITEMID_FROM_PURCHASED_CATEGORY, "Success");
            return true;
        } catch (Error e) {
            Log.d(REMOVE_ITEMID_FROM_PURCHASED_CATEGORY, "Failed");
            return false;
        }
    }
}
