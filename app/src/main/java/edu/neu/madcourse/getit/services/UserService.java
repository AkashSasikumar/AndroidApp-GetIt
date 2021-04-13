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
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import edu.neu.madcourse.getit.models.Group;
import edu.neu.madcourse.getit.models.UserModel;

public class UserService {
    private static final String CREATE_USER_STATUS = "CREATE_USER_STATUS";
    private static final String GET_USER_BY_USER_NAME = "GET_USER_BY_USER_NAME";

    FirebaseFirestore db;
    CollectionReference users;
    CollectionReference groups;
    CollectionReference items;
    GroupService groupService;
    private boolean createUserSuccessFlag;
    private UserModel user;
    private boolean addUserToGroupSuccessFlag;
    private CountDownLatch latch;

    public UserService() {
        db = FirebaseFirestore.getInstance();
        users = db.collection("users");
        groups = db.collection("groups");
        items = db.collection("items");
        groupService = new GroupService();
    }

    //TODO: Return boolean indicating createUser successful.
    public boolean createUser(String userName){
        Map<String, Object> user = new HashMap<>();
        user.put("user_name", userName);
        user.put("score", 0);
        user.put("groups", Arrays.asList());

        createUserSuccessFlag = false;
        ServiceTaskHandler.performTask(() -> createUserAsyncTask(user));

        return createUserSuccessFlag;
    }

    private void createUserAsyncTask(Map<String, Object> user) {
        users.document()
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(CREATE_USER_STATUS, "Success");
                        createUserSuccessFlag = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(CREATE_USER_STATUS, "Failed");
                        createUserSuccessFlag = false;
                    }
                });
    }

    //TODO: Return User object once method execution successfull.
    public UserModel getUserByUsername(String userName) {
//        ServiceTaskHandler.performTask(() -> getUserByUsernameAsyncTask(userName));

        System.out.println("getting user for: " + userName);
        try {
            latch = new CountDownLatch(1);
            getUserByUsernameAsyncTask(userName);

            System.out.println("awaiting for latch");
            latch.await();
//            return user;
        } catch (Exception e) {
            Log.e("ERROR: ", e.toString());
        }


        System.out.println("returning");
        if(user == null) {
            // handle this null return in a better way...in case task was not successful
        }

        Log.d(GET_USER_BY_USER_NAME, "user details----------------------");
        Log.d(GET_USER_BY_USER_NAME, "user_name----:" + user.getUser_name());
        Log.d(GET_USER_BY_USER_NAME, "user score----:" + user.getScore());
        Log.d(GET_USER_BY_USER_NAME, "groups list ----:" + user.getGroups());

        return user;
    }

    private void getUserByUsernameAsyncTask(String userName) {
        Query query = users.whereEqualTo("user_name", userName);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(GET_USER_BY_USER_NAME, document.getId() + " => " + document.getData());
                        user = (UserModel) document.toObject(UserModel.class);
                    }
                } else {
                    Log.d(GET_USER_BY_USER_NAME, "Error getting document: ", task.getException());
                    user = null;
                }

                System.out.println("updating latch");
                latch.countDown();
            }
        });
    }

    //TODO: Return boolean indicating method successful
    public boolean addUserToGroup(String userName, String groupName) {
        addUserToGroupSuccessFlag = false;
        ServiceTaskHandler.performTask(() -> addUserToGroupAsyncTask(userName, groupName));
        return addUserToGroupSuccessFlag;
    }

    private void addUserToGroupAsyncTask(String userName, String groupName) {
        Query query = users.whereEqualTo("user_name", userName);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(GET_USER_BY_USER_NAME, document.getId() + " => " + document.getData());

                        UserModel user = (UserModel) document.toObject(UserModel.class);
                        Log.d(GET_USER_BY_USER_NAME, "groups list ----:" + user.getGroups());
                        users.document(document.getId()).update("groups", FieldValue.arrayUnion(groupName));

                        //TODO: Add userName to Group's "Users" field.
                        // To do this, firstly, the method getGroupByName should be made to return the group. The group object returned
                        // will have both group name and groupId. Use the groupId to reach the correct doc and user FieldValue.arrayUnion(userName)
                        // For example of FieldValue.arrayUnion, check addItemTo_ToBePurchasedCategory in GroupService
                        Group group = groupService.getGroupByGroupName(groupName);
                        groups.document(group.getGroupId())
                                .update("users", FieldValue.arrayUnion(user.getUser_name()));

                        addUserToGroupSuccessFlag = true;
                    }
                } else {
                    Log.d(GET_USER_BY_USER_NAME, "Error getting document: ", task.getException());
                    addUserToGroupSuccessFlag = false;
                }
            }
        });
    }

    //TODO: Implement removeUserFromGroup method

}
