package com.trade.unic_01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Login extends AppCompatActivity {
    private Button btnLogin;
    private TextView tvSignup;
    LinearLayout loginform;
    private View mProgressView;
    private TextView tvLoad;

    private EditText mLoginDetail;
    private EditText mPassword;

    private FirebaseAuth mAuth;

    private String Phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLogin=findViewById(R.id.btnLogin);
        tvSignup=findViewById(R.id.tvsignup);
        loginform=findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);

        mLoginDetail = findViewById(R.id.etDetail);
        mPassword = findViewById(R.id.etPassword);

        Intent intent = getIntent();
        if(intent.getStringExtra("Phone")!=null) {
            Phone = intent.getStringExtra("Phone");
            mLoginDetail.setText(Phone);
        }
        else mLoginDetail.setText(null);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()!=null){
            startActivity(new Intent(Login.this,Home.class));
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Detail = mLoginDetail.getText().toString().trim();
                String Password = mPassword.getText().toString();
                if(Detail.contains("@")) Authorize_by_Email(Detail,Password);
                else Authorize_by_Phone(Detail,Password);
                mProgressView.setVisibility(View.VISIBLE);
                tvLoad.setText("Logging you in...Please wait...");
                tvLoad.setVisibility(View.VISIBLE);
                loginform.setVisibility(View.GONE);



            }
        });
        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,SignUp.class));


            }
        });


    }
    public void Authorize_by_Email(String Email,String password){
        mAuth.signInWithEmailAndPassword(Email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(Login.this, Home.class));
                    finish();
                }
                else {
                    Toast.makeText(Login.this, "Invalid Email and Password combination", Toast.LENGTH_SHORT).show();
                    tvLoad.setVisibility(View.GONE);
                    mProgressView.setVisibility(View.GONE);
                    loginform.setVisibility(View.VISIBLE);

                }

            }
        });

    }
    public void Authorize_by_Phone(String phone, final String password) {
        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously();
        CollectionReference collectionReference = mDatabase.collection("users");
        collectionReference.whereEqualTo("phone_number",phone).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        String email = document.get("email_address").toString();
                        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    startActivity(new Intent(Login.this, Home.class));
                                    finish();
                                }
                                else {
                                    Toast.makeText(Login.this, "Invalid Phone and Password combination", Toast.LENGTH_SHORT).show();
                                    tvLoad.setVisibility(View.GONE);
                                    mProgressView.setVisibility(View.GONE);
                                    loginform.setVisibility(View.VISIBLE);

                                }
                            }
                        });
                    }
                }
            }
        });
    }
}
