package edu.neu.madcourse.getit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.neu.madcourse.getit.handlers.MainThreadHandler;
import edu.neu.madcourse.getit.services.GroupService;
import edu.neu.madcourse.getit.services.ItemService;
import edu.neu.madcourse.getit.services.UserService;
import edu.neu.madcourse.getit.utils.GroupServiceThread;

public class TestActivity extends AppCompatActivity {
    Button test_button;
    UserService userService;
    GroupService groupService;
    ItemService itemService;
    TextView textView;
    MainThreadHandler mainThreadHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        userService = new UserService();
        groupService = new GroupService();
        itemService = new ItemService();

        textView = findViewById(R.id.textView3);
        test_button = findViewById(R.id.test_button);

        mainThreadHandler = new MainThreadHandler(textView);

        GroupServiceThread groupServiceThread = new GroupServiceThread(mainThreadHandler, groupService);
        groupServiceThread.setName("group-service-thread");
        groupServiceThread.start();

        test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                userService.createUser("new-test-user-102");
                Message group_name_message = Message.obtain();
                group_name_message.obj = "test_group";
                groupServiceThread.getGroupServiceHandler().sendMessage(group_name_message);
//                userService.addUserToGroup("new-test-user", "test_group-19191");
//                itemService.createItem("test_item_001", "test_group", "test_user");
//                groupService.getGroupByGroupName("test_group");
//                itemService.getItemByItemId("SivO8kGztXXGpHQxQQ6U");
//                groupService.addItemTo_ToBePurchasedCategory("bSxorAkxFHnQQqdZag6A", "SivO8kGztXXGpHQxQQ6U");
            }
        });

    }
}