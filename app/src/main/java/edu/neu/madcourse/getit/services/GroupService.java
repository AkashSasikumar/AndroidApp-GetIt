package edu.neu.madcourse.getit.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.neu.madcourse.getit.callbacks.GroupServiceCallbacks;
import edu.neu.madcourse.getit.callbacks.UserServiceCallbacks;
import edu.neu.madcourse.getit.models.Group;

public class GroupService {

    private static final String CREATE_GROUP_STATUS = "CREATE_GROUP_STATUS";
    private static final String GET_GROUP_BY_GROUP_NAME = "GET_GROUP_BY_GROUP_NAME";
    private static final String ADD_ITEMID_TO_TOBE_PURCHASED_CATEGORY = "ADD_ITEMID_TO_TOBE_PURCHASED_CATEGORY";
    private static final String ADD_ITEMID_TO_PURCHASED_CATEGORY = "ADD_ITEMID_TO_PURCHASED_CATEGORY";
    private static final String REMOVE_ITEMID_FROM_TOBE_PURCHASED_CATEGORY = "REMOVE_ITEMID_FROM_TO_PURCHASED_CATEGORY";
    private static final String REMOVE_ITEMID_FROM_PURCHASED_CATEGORY = "REMOVE_ITEMID_FROM_PURCHASED_CATEGORY";
    private static final String ADD_USER_TO_GROUP = "ADD_USER_TO_GROUP";

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

    public void createGroup(String groupName, GroupServiceCallbacks.CreateGroupTaskCallback callback) {
        Map<String, Object> newGroup = new HashMap<>();
        newGroup.put("group_name", groupName);
        newGroup.put("items", Collections.emptyList());
        newGroup.put("users", Collections.emptyList());

        groups.document()
                .set(newGroup)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(CREATE_GROUP_STATUS, "Success");
                        callback.onComplete(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(CREATE_GROUP_STATUS, "Failed");
                        callback.onComplete(false);
                    }
                });
    }

    public void getGroupByGroupName(String groupName,
                                    GroupServiceCallbacks.GetGroupByGroupNameTaskCallback callback) {
        Query query = groups.whereEqualTo("group_name", groupName);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Group group = null;
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(GET_GROUP_BY_GROUP_NAME, document.getId() + " => " + document.getData());
                        group = (Group) document.toObject(Group.class);
                        group.setGroupId(document.getId());
                    }
                } else {
                    Log.d(GET_GROUP_BY_GROUP_NAME, "Error getting document: ", task.getException());
                }

                callback.onComplete(group);
            }
        });
    }

    public void getGroupNameByGroupID(String groupID, GroupServiceCallbacks.GetGroupNameFromGroupIDCallback callback){
        groups.document(groupID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot groupRef = task.getResult();
                    if(groupRef.exists()){
                        String groupName = (String) groupRef.getData().get("group_name");
                        callback.onComplete(groupName);
                    }
                }else{
                    Log.d(GET_GROUP_BY_GROUP_NAME, "Failed to get group name from group id");
                    callback.onComplete(null);
                }
            }
        });
    }

    public void addUserToGroup(String userID, String groupName,
                                  GroupServiceCallbacks.AddUserToGroupTaskCallback callback){
        getGroupByGroupName(groupName, new GroupServiceCallbacks.GetGroupByGroupNameTaskCallback() {
            @Override
            public void onComplete(Group group) {
                try{
                    // add user to group
                    groups.document(group.getGroupId()).update("users", FieldValue.arrayUnion(userID));
                    Log.d(ADD_USER_TO_GROUP, "Success");

                    // add group id to user's group list
                    users.document(userID).update("user_groups", FieldValue.arrayUnion(group.getGroupId()));

                    callback.onComplete(true);
                }catch (Error e){
                    Log.d(ADD_USER_TO_GROUP, "Success");
                    callback.onComplete(false);
                }
            }
        });
    }

    public void addItemToGroup(String itemId, String groupName,
                               GroupServiceCallbacks.AddItemToGroupTaskCallback callback){
        getGroupByGroupName(groupName, new GroupServiceCallbacks.GetGroupByGroupNameTaskCallback() {
            @Override
            public void onComplete(Group group) {
                try{
                    // add item to group
                    groups.document(group.getGroupId()).update("items", FieldValue.arrayUnion(itemId));
                    callback.onComplete(true);
                }catch (Error e){
                    callback.onComplete(false);
                }
            }
        });
    }

//    public boolean addItemTo_ToBePurchasedCategory(String groupId, String itemId) {
//        try {
//            groups.document(groupId).update("items_to_purchase", FieldValue.arrayUnion(itemId));
//            Log.d(ADD_ITEMID_TO_TOBE_PURCHASED_CATEGORY, "Success");
//            return true;
//        } catch (Error e) {
//            Log.d(ADD_ITEMID_TO_TOBE_PURCHASED_CATEGORY, "Failed");
//            return false;
//        }
//    }
//
//    public boolean removeItemFrom_ToBePurchasedCategory(String groupId, String itemId) {
//        try {
//            groups.document(groupId).update("items_to_purchase", FieldValue.arrayRemove(itemId));
//            Log.d(REMOVE_ITEMID_FROM_TOBE_PURCHASED_CATEGORY, "Success");
//            return true;
//        } catch (Error e) {
//            Log.d(REMOVE_ITEMID_FROM_TOBE_PURCHASED_CATEGORY, "Failed");
//            return false;
//        }
//    }
//
//    public boolean addItemTo_AlreadyPurchasedCategory(String groupId, String itemId) {
//        try {
//            groups.document(groupId).update("items_purchased", FieldValue.arrayUnion(itemId));
//            Log.d(ADD_ITEMID_TO_PURCHASED_CATEGORY, "Success");
//            return true;
//        } catch (Error e) {
//            Log.d(ADD_ITEMID_TO_PURCHASED_CATEGORY, "Failed");
//            return false;
//        }
//    }
//
//    public boolean removeItemFrom_AlreadyPurchasedCategory(String groupId, String itemId) {
//        try {
//            groups.document(groupId).update("items_purchased", FieldValue.arrayRemove(itemId));
//            Log.d(REMOVE_ITEMID_FROM_PURCHASED_CATEGORY, "Success");
//            return true;
//        } catch (Error e) {
//            Log.d(REMOVE_ITEMID_FROM_PURCHASED_CATEGORY, "Failed");
//            return false;
//        }
//    }

}
