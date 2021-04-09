package edu.neu.madcourse.getit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity implements View.OnClickListener {

    EditText mFullName, mEmail, mPassword, mConfirmPassword;
    Button mRegisterBtn;
    TextView mLoginHere;
    ProgressBar mProgressBar;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullName = findViewById(R.id.fullName);
        mEmail = findViewById(R.id.emailAddress);
        mPassword = findViewById(R.id.password);
        mConfirmPassword = findViewById(R.id.confirmPassword);
        mRegisterBtn = findViewById(R.id.register);
        mLoginHere = findViewById(R.id.loginHere);
        mProgressBar = findViewById(R.id.registerProgressBar);
        fAuth = FirebaseAuth.getInstance();

        //set progress bar to invisible
        mProgressBar.setVisibility(View.INVISIBLE);

        //set on click listeners
        mRegisterBtn.setOnClickListener(this);
        mLoginHere.setOnClickListener(this);
        // check if user is already logged in
        //if (fAuth.getCurrentUser() != null){
        //    startActivity(new Intent(getApplicationContext(),Login.class));
        //    finish();
        //}
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.loginHere){
            startActivity(new Intent(getApplicationContext(),Login.class));
        } else if (v.getId() == R.id.register){
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();
            String confirmPassword = mConfirmPassword.getText().toString().trim();

            if(TextUtils.isEmpty(email)){
                mEmail.setError("Email is required");
                return;
            }
            if(TextUtils.isEmpty((password))){
                mPassword.setError("Password is required");
                return;
            }
            if(TextUtils.isEmpty(confirmPassword)){
                mConfirmPassword.setError("Password is required");
                return;
            }
            if(password.length() < 6){
                mPassword.setError("Password must be at least 6 characters");
                return;
            }
            if(!password.equals(confirmPassword)){
                mConfirmPassword.setError("Password does not match");
                return;
            }

            // register the user
            mProgressBar.setVisibility(View.VISIBLE);
            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Snackbar.make(v, "User created successfully. Please login!", Snackbar.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(),Login.class));
                    }else{
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Snackbar.make(v, "Error! " + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}