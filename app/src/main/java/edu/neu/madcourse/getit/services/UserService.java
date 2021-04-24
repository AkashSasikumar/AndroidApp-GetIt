package edu.neu.madcourse.getit.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.neu.madcourse.getit.callbacks.UserServiceCallbacks;
import edu.neu.madcourse.getit.models.User;

public class UserService {
    private static final String CREATE_USER_STATUS = "CREATE_USER_STATUS";
    private static final String GET_USER_BY_USER_NAME = "GET_USER_BY_USER_NAME";


    FirebaseFirestore db;
    CollectionReference users;
    CollectionReference groups;
    CollectionReference items;
    GroupService groupService;

    public UserService() {
        db = FirebaseFirestore.getInstance();
        users = db.collection("users");
        groups = db.collection("groups");
        items = db.collection("items");
        groupService = new GroupService();
    }

    public void createUser(String userAuthID, String userEmail, String userFullName, UserServiceCallbacks.CreateUserTaskCallback callback){
        Map<String, Object> user = new HashMap<>();
        user.put("user_email", userEmail);
        user.put("user_full_name", userFullName);
        user.put("device_token", "");
        user.put("user_score", 0);
        user.put("user_groups", Collections.emptyList());
        user.put("user_items_posted", Collections.emptyList());
        user.put("user_items_getting", Collections.emptyList());


        users.document(userAuthID)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(CREATE_USER_STATUS, "Success");
                        callback.onComplete(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(CREATE_USER_STATUS, "Failed");
                        callback.onComplete(false);
                    }
                });
    }

    public void getUserByUserId(String userID, UserServiceCallbacks.GetUserByUserIdTaskCallback callback) {
        //Query query = users.whereEqualTo("user_name", userName);
        // no need to query, getting user by id
        DocumentReference docRef = users.document(userID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User user = new User();
                if (task.isSuccessful()){
                    DocumentSnapshot userRef = task.getResult();
                    if(userRef.exists()){
                        Map<String, Object> user_map = userRef.getData();
                        user.setUserEmail(user_map.get("user_email").toString());
                        user.setFullName( (String) user_map.get("user_full_name"));
                        user.setScore( (long) user_map.get("user_score"));
                        user.setGroups((ArrayList<String>) user_map.get("user_groups"));
                        user.setUserItemsGetting((ArrayList<String>) user_map.get("user_items_getting"));
                        user.setUserItemsPosted((ArrayList<String>) user_map.get("user_items_posted"));
                        user.setFirebase_token((String) user_map.get("device_token"));
                        user.setUserId(userID);
                    }
                }else{
                    Log.d(GET_USER_BY_USER_NAME, "Error getting document: ", task.getException());
                }
                callback.onComplete(user);
            }
        });
    }

    public void getUserFromEmail(String userEmail, UserServiceCallbacks.GetUserFromEmailCallback callback) {
        Query query = users.whereEqualTo("user_email", userEmail);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                User user = new User();
                if (task.isSuccessful()) {
                    QuerySnapshot documentRef = task.getResult();
                    if (!documentRef.isEmpty()){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> user_map = document.getData();
                            user.setUserEmail(user_map.get("user_email").toString());
                            user.setFullName( (String) user_map.get("user_full_name"));
                            user.setScore( (long) user_map.get("user_score"));
                            user.setGroups((ArrayList<String>) user_map.get("user_groups"));
                            user.setUserItemsGetting((ArrayList<String>) user_map.get("user_items_getting"));
                            user.setUserItemsPosted((ArrayList<String>) user_map.get("user_items_posted"));
                            user.setFirebase_token((String) user_map.get("device_token"));
                            user.setUserId((String) document.getId());
                            callback.onComplete(user);
                        }
                    }else{
                        callback.onComplete(null);
                    }
                } else {
                    callback.onComplete(null);
                }
            }
        });
    }

    public void updateUserDeviceToken(String userDocId, String token) {
        users.document(userDocId).update("device_token", token);
    }


//    public void addUserToGroup_old(String userName, String groupName,
//                               UserServiceCallbacks.AddUserToGroupTaskCallback callback) {
//        Query query = users.whereEqualTo("user_name", userName);
//        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        Log.d(GET_USER_BY_USER_NAME, document.getId() + " => " + document.getData());
//
//                        User user = (User) document.toObject(User.class);
//                        Log.d(GET_USER_BY_USER_NAME, "groups list ----:" + user.getGroups());
//                        users.document(document.getId()).update("groups", FieldValue.arrayUnion(groupName));
//
//                        //TODO: Add userName to Group's "Users" field.
//                        // To do this, firstly, the method getGroupByName should be made to return the group. The group object returned
//                        // will have both group name and groupId. Use the groupId to reach the correct doc and user FieldValue.arrayUnion(userName)
//                        // For example of FieldValue.arrayUnion, check addItemTo_ToBePurchasedCategory in GroupService
//                        groupService.getGroupByGroupName(groupName,
//                                new GroupServiceCallbacks.GetGroupByGroupNameTaskCallback() {
//                            @Override
//                            public void onComplete(Group group) {
//                                groups.document(group.getGroupId())
//                                        .update("users", FieldValue.arrayUnion(user.getUser_name()));
//
//                                // send origin callback to true
//                                callback.onComplete(true);
//                            }
//                        });
//                    }
//                } else {
//                    Log.d(GET_USER_BY_USER_NAME, "Error getting document: ", task.getException());
//                    callback.onComplete(false);
//                }
//            }
//        });
//    }



    //TODO: Implement removeUserFromGroup method

}
