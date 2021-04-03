package edu.neu.madcourse.getit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Register extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViewById(R.id.loginHere).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        if (v.getId() == R.id.loginHere){
            intent = new Intent(v.getContext(), Login.class);
            v.getContext().startActivity(intent);
        }
    }
}