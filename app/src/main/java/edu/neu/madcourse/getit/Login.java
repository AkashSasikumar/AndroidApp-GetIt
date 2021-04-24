package edu.neu.madcourse.getit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity implements View.OnClickListener {

    EditText mEmail;
    EditText mPassword;
    Button mLoginBtn;
    TextView mRegisterHere;
    ProgressBar mProgressBar;
    ConstraintLayout mLoginLayout;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.emailAddress);
        mPassword = findViewById(R.id.password);
        mLoginBtn = findViewById(R.id.login);
        mRegisterHere = findViewById(R.id.registerHere);
        mProgressBar = findViewById(R.id.loginProgressBar);
        mLoginLayout = findViewById(R.id.login_layout);

        fAuth = FirebaseAuth.getInstance();

        //set progress bar to invisible
        mProgressBar.setVisibility(View.INVISIBLE);

        // set on click listeners
        mRegisterHere.setOnClickListener(this);
        mLoginBtn.setOnClickListener(this);
        mLoginLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // hide keyboard
                try{
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    return false;
                }catch (Exception e){
                    return false;
                }

            }
        });

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.registerHere){
            startActivity(new Intent(getApplicationContext(),Register.class));
        }else if (v.getId() == R.id.login){

            // hide keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();

            if(TextUtils.isEmpty(email)){
                mEmail.setError("Email is required");
                return;
            }
            if(TextUtils.isEmpty((password))){
                mPassword.setError("Password is required");
                return;
            }
            if(password.length() < 6){
                mPassword.setError("Password must be at least 6 characters");
                return;
            }

            // authenticate the user
            mProgressBar.setVisibility(View.VISIBLE);
            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        mProgressBar.setVisibility(View.INVISIBLE);
                        // Snackbar.make(v, "User logged in successfully!", Snackbar.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(),YourGroupsActivity.class));
                    }else{
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Snackbar.make(v, "Error! " + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}