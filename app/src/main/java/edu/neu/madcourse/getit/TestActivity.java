package edu.neu.madcourse.getit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.neu.madcourse.getit.services.BackendUserService;

public class TestActivity extends AppCompatActivity {
    Button test_button;
    BackendUserService backendUserService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        backendUserService = new BackendUserService();

        test_button = findViewById(R.id.test_button);

        test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backendUserService.createUser("new-test-user-99");
                backendUserService.getUserByUsername("new-test-user");
            }
        });

    }
}