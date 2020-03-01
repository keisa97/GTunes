package com.example.musicwithnav.ui.MyAccount.ui.myaccount;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;

import com.example.musicwithnav.MainActivity;
import com.example.musicwithnav.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class EmailLoginActivity extends AppCompatActivity {

    private EditText etEmail_login;
    private EditText etpassword_login;
    private Button btnLogin;
    private Button btn_to_Register;



    OnSuccessListener<AuthResult> mSuccessListener = new OnSuccessListener<AuthResult>() {
        @Override
        public void onSuccess(AuthResult authResult) {
            if (progressDialog.isShowing()){
                progressDialog.dismiss();
            }

            startActivity(new Intent( EmailLoginActivity.this, MainActivity.class));

        }
    };

    OnFailureListener mFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            //a user already exist with mail
            showError(e);
        }
    };

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);

        etEmail_login= findViewById(R.id.etEmail_login);
        etpassword_login= findViewById(R.id.etpassword_login);
        btnLogin = findViewById(R.id.user_email_login_btn);
        btn_to_Register = findViewById(R.id.btn_to_register_activity);

        btnLogin.setOnClickListener(v -> {


        });

        btn_to_Register.setOnClickListener(v ->
                startActivity(new Intent(EmailLoginActivity.this,RegisterActivity.class)));

    }

    private void login(){

        String email = etEmail_login.getText().toString().trim();
        String password = etpassword_login.getText().toString().trim();



        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnSuccessListener(mSuccessListener)
                .addOnFailureListener(mFailureListener);

        showProgress();
    }

    private String getEmail(){
        String email = etEmail_login.getText().toString();

        Pattern emailAddressRegex = Patterns.EMAIL_ADDRESS;
        boolean isMailValid = emailAddressRegex.matcher(email).matches();

        if (!isMailValid){
            etEmail_login.setError("invalid email address");
            return null;
        }
        return email;
    }

    private String getPassword(){
        String pass = etpassword_login.getText().toString();


        if (pass.length()<6){
            etpassword_login.setError("invalid email address");
            return null;
        }
        return pass;
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
