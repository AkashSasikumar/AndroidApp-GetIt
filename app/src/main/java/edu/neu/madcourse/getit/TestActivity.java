package edu.neu.madcourse.getit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Random;

import edu.neu.madcourse.getit.callbacks.GroupServiceCallbacks;
import edu.neu.madcourse.getit.callbacks.ItemServiceCallbacks;
import edu.neu.madcourse.getit.callbacks.UserServiceCallbacks;
import edu.neu.madcourse.getit.models.Group;
import edu.neu.madcourse.getit.models.Item;
import edu.neu.madcourse.getit.services.GroupService;
import edu.neu.madcourse.getit.services.ItemService;
import edu.neu.madcourse.getit.services.UserService;

public class TestActivity extends AppCompatActivity {
    private static final String FETCHING_FCM_REGISTRATION_TOKEN_STATUS = "FETCHING_FCM_REGISTRATION_TOKEN_STATUS:  ";
    Button test_button_1;
    Button test_button_2;
    UserService userService;
    GroupService groupService;
    ItemService itemService;
    TextView textView3, textView4;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        userService = new UserService();
        groupService = new GroupService();
        itemService = new ItemService();

        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        test_button_1 = findViewById(R.id.test_button_1);
        test_button_2 = findViewById(R.id.test_button_2);
        fAuth = FirebaseAuth.getInstance();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.d(FETCHING_FCM_REGISTRATION_TOKEN_STATUS, "failed ", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        Log.d(FETCHING_FCM_REGISTRATION_TOKEN_STATUS, "success " + token);

                        userService.updateUserDeviceToken(fAuth.getCurrentUser().getUid() , token);
                    }
                });

        test_button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                testCreateUser("new-test-user-102", textView3);

                // created multiple users in firebase manually to test...
//                int rand = new Random().nextInt(5);
//                textView3.setText("calling for user: " + rand);
//                testGetUserByUserName("new-test-user-" + rand, textView3);

//                testAddUserToGroup("new-test-user-102", "test_group_1", textView3);

//                testCreateItem("test_item_1", "test_group", "test_user", textView3);
//                testGetItemByItemId("uVG78vhrpLhIcQRBdKhu", textView3);

//                testCreateGroup("test_group_1", textView3);
                testGetGroupByGroupName("test_group_1", textView3);
            }
        });

        test_button_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                testCreateUser("new-test-user-102", textView4);

                int rand = new Random().nextInt(5);
                textView4.setText("calling for user: " + rand);
                testGetUserByUserName("new-test-user-" + rand, textView4);
            }
        });
    }

    private void testCreateUser(String userName, TextView textView) {
//        userService.createUser(userName, new UserServiceCallbacks.CreateUserTaskCallback() {
//            @Override
//            public void onComplete(boolean isSuccess) {
//                textView.setText("Created user: " + userName + " with result: " + isSuccess);
//            }
//        });
    }

    private void testGetUserByUserName(String userName, TextView textView) {
        userService.getUserByUserId(userName,
                (UserServiceCallbacks.GetUserByUserIdTaskCallback) user -> {
                    String msg = user.toString();
                    textView.setText(msg);
                });
    }

//    private void testAddUserToGroup(String userName, String groupName, TextView textView) {
//        userService.addUserToGroup(userName, groupName, new UserServiceCallbacks.AddUserToGroupTaskCallback() {
//            @Override
//            public void onComplete(boolean isSuccess) {
//                textView.setText("Added user: " + userName + " to group: " + groupName + " result: " + isSuccess);
//            }
//        });
//    }

//    private void testCreateItem(String itemName, String groupName, String userName, TextView textView) {
//        itemService.createItem(itemName, groupName, userName, new ItemServiceCallbacks.CreateItemTaskCallback() {
//            @Override
//            public void onComplete(boolean isSuccess) {
//                textView.setText("Item: " + itemName + " created under group: " + groupName
//                        + " with result: " + isSuccess);
//            }
//        });
//    }

    private void testGetItemByItemId(String itemId, TextView textView) {
        itemService.getItemByItemId(itemId, new ItemServiceCallbacks.GetItemByItemIdTaskCallback() {
            @Override
            public void onComplete(Item item) {
                String msg = item.toString();
                textView.setText(msg);
            }
        });
    }

//    private void testCreateGroup(String groupName, TextView textView) {
//        groupService.createGroup(groupName, new GroupServiceCallbacks.CreateGroupTaskCallback() {
//            @Override
//            public void onComplete(boolean isSuccess) {
//                textView.setText("Create group: " + groupName + " with result: " + isSuccess);
//            }
//        });
//    }

    private void testGetGroupByGroupName(String groupName, TextView textView) {
        groupService.getGroupByGroupName(groupName, new GroupServiceCallbacks.GetGroupByGroupNameTaskCallback() {
            @Override
            public void onComplete(Group group) {
                String msg = group.toString();
                textView.setText(msg);
            }
        });
    }
}