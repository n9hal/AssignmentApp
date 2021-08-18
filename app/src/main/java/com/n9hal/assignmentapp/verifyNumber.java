package com.n9hal.assignmentapp;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class verifyNumber extends AppCompatActivity {

    Button btnVerify;
    EditText editOTP;
    boolean animCounter=false;
    String OTP;
    FirebaseAuth mAuth;
    String phoneNumber;
    List<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_number);

        btnVerify = findViewById(R.id.btnVerify);
        editOTP = findViewById(R.id.edit_OTP);
        mAuth = FirebaseAuth.getInstance();

        phoneNumber = getIntent().getStringExtra("phoneNumber");

        OTP = getIntent().getStringExtra("sent_OTP");
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(animCounter){
                    animCounter = false;
                    btnVerify.setText("Verify");
                }
                else{
                    String enteredOTP = editOTP.getText().toString();
                    if (enteredOTP.isEmpty()){
                        animCounter = false;
                        btnVerify.setText("Verify");
                        Toast.makeText(verifyNumber.this,"Please Enter OTP",Toast.LENGTH_LONG).show();
                    }
                    else{
                        animCounter = true;
                        btnVerify.setText("Verifying...");
                        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(OTP,enteredOTP);
                        signIn(phoneAuthCredential);
                    }
                }
            }
        });
    }
    public void signIn(PhoneAuthCredential phoneAuthCredential){
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d("signInSuccess","Sign-in Successful...");
                }
                else{
                    animCounter = false;
                    btnVerify.setText("Verify");
                    Toast.makeText(verifyNumber.this,"Something Went Worng",Toast.LENGTH_LONG).show();
                    Log.d("Error",task.toString());
                }
            }
        });
    }
    public void open_details_Form(){
        Intent intent = new Intent(verifyNumber.this,details_form.class);
        intent.putExtra("phoneNumber",phoneNumber);
        startActivity(intent);
        finish();
    }
}
