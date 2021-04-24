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

import edu.neu.madcourse.getit.callbacks.UserServiceCallbacks;
import edu.neu.madcourse.getit.services.UserService;

public class Register extends AppCompatActivity implements View.OnClickListener {

    EditText mFullName, mEmail, mPassword, mConfirmPassword;
    Button mRegisterBtn;
    TextView mLoginHere;
    ProgressBar mProgressBar;
    ConstraintLayout mRegisterLayout;
    String mUserID;
    FirebaseAuth fAuth;
    UserService userService;


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
        mRegisterLayout = findViewById(R.id.register_layout);
        fAuth = FirebaseAuth.getInstance();
        userService = new UserService();

        //set progress bar to invisible
        mProgressBar.setVisibility(View.INVISIBLE);

        //set on click listeners
        mRegisterBtn.setOnClickListener(this);
        mLoginHere.setOnClickListener(this);
        mRegisterLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try{
                    // hide keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    return false;
                } catch (Exception e){
                    return false;
                }

            }
        });

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
            String fullName = mFullName.getText().toString().trim();
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();
            String confirmPassword = mConfirmPassword.getText().toString().trim();

            if(TextUtils.isEmpty(fullName)){
                mEmail.setError("Full Name is required");
                return;
            }

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
                        mUserID = fAuth.getCurrentUser().getUid();
                        userService.createUser(mUserID, email, fullName, new UserServiceCallbacks.CreateUserTaskCallback() {
                            @Override
                            public void onComplete(boolean isSuccess) {
                                if(isSuccess) {
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                    Snackbar.make(v, "User created successfully. Please login!", Snackbar.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(),Login.class));
                                } else {
                                    // TODO: remove this user from fAuth as well
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                    Snackbar.make(v, "Error! " + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });
                    }else{
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Snackbar.make(v, "Error! " + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

}