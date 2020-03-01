package com.example.musicwithnav.ui.MyAccount.ui.myaccount;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.musicwithnav.MainActivity;
import com.example.musicwithnav.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    EditText eMailEt,mPasswordEt;
    Button mRegisterBtn;

    OnSuccessListener<AuthResult> mSuccessListener = new OnSuccessListener<AuthResult>() {
        @Override
        public void onSuccess(AuthResult authResult) {
            if (progressDialog.isShowing()){
                progressDialog.dismiss();
            }

            startActivity(new Intent( RegisterActivity.this, MainActivity.class));
            finish();

        }
    };

    OnFailureListener mFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            //a user already exist with mail
            showError(e);
        }
    };

    //preogressbar to display while registering user
    ProgressDialog progressDialog;

    //Declare an instance of FirebaseAuth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Account");
        //enable back buttton
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //init
        eMailEt = findViewById(R.id.etEmail_register);
        mPasswordEt = findViewById(R.id.etpassword_register);
        mRegisterBtn = findViewById(R.id.user_register_btn);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("process registering...");

        //handle register btn click
        mRegisterBtn.setOnClickListener(v -> {
            //input email , pass

            String email = eMailEt.getText().toString().trim();
            String password = mPasswordEt.getText().toString().trim();
            //validate
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                //set error and focous email edit text
                eMailEt.setError("Invaild Email");
                eMailEt.setFocusable(true);
            }
            else if (password.length()<6){
                //set error and focous email edit text
                mPasswordEt.setError("Password length as least 6 characters");
                eMailEt.setFocusable(true);
            }
            else {
                registerUser(email,password);//register the user
            }



        });
    }


    private void registerUser(String email, String password) {

        //email and password pattern is valid, show progress dialog and
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, dismiss dialog and start register activity
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(RegisterActivity.this, "registered...\n"+user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent( RegisterActivity.this, MainActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "Authenticaion failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //register -> addOnSuccess -> addOnFailure
    private void registerByTomer(){
        String email = eMailEt.getText().toString().trim();
        String password = mPasswordEt.getText().toString().trim();


        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnSuccessListener(mSuccessListener)
                .addOnFailureListener(mFailureListener);
        showProgress();

    }

    private void loginByTomer(){
        String email = eMailEt.getText().toString().trim();
        String password = mPasswordEt.getText().toString().trim();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnSuccessListener(mSuccessListener)
                .addOnFailureListener(mFailureListener);

        showProgress();

    }

    private void showProgress(){
        //before use if it's null - > init
        //lazy-variable
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Logging you in");
            progressDialog.setMessage("Loading");
        }
    }

    private void showError(Exception e){
        new AlertDialog.Builder(this)
                .setTitle("an Error oucor")
                .setMessage(e.getLocalizedMessage())
                .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }
}


