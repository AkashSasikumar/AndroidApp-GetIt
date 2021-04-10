package edu.neu.madcourse.getit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.neu.madcourse.getit.services.UserService;

public class TestActivity extends AppCompatActivity {
    Button test_button;
    UserService userService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        userService = new UserService();

        test_button = findViewById(R.id.test_button);

        test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userService.createUser("new-test-user-101");
                userService.getUserByUsername("new-test-user-99");
                userService.addUserToGroup("new-test-user", "test_group-19191");
            }
        });

    }
}