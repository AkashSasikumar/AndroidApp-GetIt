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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import edu.neu.madcourse.getit.callbacks.GroupServiceCallbacks;
import edu.neu.madcourse.getit.callbacks.UserServiceCallbacks;
import edu.neu.madcourse.getit.models.Group;
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

    public void createUser(String userName, UserServiceCallbacks.CreateUserTaskCallback callback){
        Map<String, Object> user = new HashMap<>();
        user.put("user_name", userName);
        user.put("score", 0);
        user.put("groups", Collections.emptyList());

        users.document()
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

    public void getUserByUsername(String userName, UserServiceCallbacks.GetUserByUserNameTaskCallback callback) {
        Query query = users.whereEqualTo("user_name", userName);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                User user = null;
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Log.d(GET_USER_BY_USER_NAME, document.getId() + " => " + document.getData());
                        user = (User) document.toObject(User.class);
                    }
                } else {
                    Log.d(GET_USER_BY_USER_NAME, "Error getting document: ", task.getException());
                }

                callback.onComplete(user);
            }
        });
    }

    public void addUserToGroup(String userName, String groupName,
                               UserServiceCallbacks.AddUserToGroupTaskCallback callback) {
        Query query = users.whereEqualTo("user_name", userName);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(GET_USER_BY_USER_NAME, document.getId() + " => " + document.getData());

                        User user = (User) document.toObject(User.class);
                        Log.d(GET_USER_BY_USER_NAME, "groups list ----:" + user.getGroups());
                        users.document(document.getId()).update("groups", FieldValue.arrayUnion(groupName));

                        //TODO: Add userName to Group's "Users" field.
                        // To do this, firstly, the method getGroupByName should be made to return the group. The group object returned
                        // will have both group name and groupId. Use the groupId to reach the correct doc and user FieldValue.arrayUnion(userName)
                        // For example of FieldValue.arrayUnion, check addItemTo_ToBePurchasedCategory in GroupService
                        groupService.getGroupByGroupName(groupName,
                                new GroupServiceCallbacks.GetGroupByGroupNameTaskCallback() {
                            @Override
                            public void onComplete(Group group) {
                                groups.document(group.getGroupId())
                                        .update("users", FieldValue.arrayUnion(user.getUser_name()));

                                // send origin callback to true
                                callback.onComplete(true);
                            }
                        });
                    }
                } else {
                    Log.d(GET_USER_BY_USER_NAME, "Error getting document: ", task.getException());
                    callback.onComplete(false);
                }
            }
        });
    }

    //TODO: Implement removeUserFromGroup method

}