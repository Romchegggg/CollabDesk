package com.example.collabdesk;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText etLogin, etPass;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        etLogin = findViewById(R.id.et_name);
        etPass = findViewById(R.id.et_pass);

        Button btnLogin = findViewById(R.id.btn_login);
        Button btnSignin = findViewById(R.id.btn_signin);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnLogIn(etLogin.getText().toString(), etPass.getText().toString());
            }
        });

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnSignIn(etLogin.getText().toString(), etPass.getText().toString());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            Log.i("TEST", "onStart: currentUser = " + mAuth.getCurrentUser().getEmail());
            startMainActivity();
            //mAuth.signOut();
        }
    }

    private void OnLogIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i("TEST", "signInWithEmail:success");
                            startMainActivity();
                        } else {
                            Log.i("TEST", "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication is failed!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void OnSignIn(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i("TEST", "createUserWithEmail:success");
                            startMainActivity();
                        } else {
                            Log.i("TEST", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication is failed!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void startMainActivity(){
        if(mAuth.getCurrentUser() == null){
            return;
        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("UserUID", mAuth.getCurrentUser());
        startActivity(intent);
    }
}