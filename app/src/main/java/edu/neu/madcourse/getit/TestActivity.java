package edu.neu.madcourse.getit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.neu.madcourse.getit.services.GroupService;
import edu.neu.madcourse.getit.services.ItemService;
import edu.neu.madcourse.getit.services.UserService;

public class TestActivity extends AppCompatActivity {
    Button test_button;
    UserService userService;
    GroupService groupService;
    ItemService itemService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        userService = new UserService();
        groupService = new GroupService();
        itemService = new ItemService();

        test_button = findViewById(R.id.test_button);

        test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                userService.createUser("new-test-user-102");
                userService.getUserByUsername("new-test-user");
//                userService.addUserToGroup("new-test-user", "test_group-19191");
//                itemService.createItem("test_item_001", "test_group", "test_user");
//                groupService.getGroupByGroupName("test_group");
//                itemService.getItemByItemId("SivO8kGztXXGpHQxQQ6U");
//                groupService.addItemTo_ToBePurchasedCategory("bSxorAkxFHnQQqdZag6A", "SivO8kGztXXGpHQxQQ6U");
            }
        });

    }
}