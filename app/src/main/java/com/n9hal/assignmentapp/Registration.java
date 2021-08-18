package com.n9hal.assignmentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Registration extends AppCompatActivity {

    TextView txtRegister,txtView;
    EditText edit_Text_mobile;
    Button btnContinue;
    String number;
    FirebaseAuth mAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        txtRegister=findViewById(R.id.txtRegister);
        txtView=findViewById(R.id.textView);
        edit_Text_mobile=findViewById(R.id.edit_Text_mobile1);
        btnContinue = findViewById(R.id.btnContinue);
        mAuth= FirebaseAuth.getInstance();

        //LOGIN AND REGISTER
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number= edit_Text_mobile.getText().toString();
                if(TextUtils.isEmpty(number)){
                    Toast.makeText(Registration.this,"Please Enter Number",Toast.LENGTH_LONG).show();
                }
                else{
                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber("+91"+ number)
                            .setTimeout(60L,TimeUnit.SECONDS)
                            .setActivity(Registration.this)
                            .setCallbacks(mCallbacks)
                            .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                }
            }
        });
        mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signIn(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                number = edit_Text_mobile.getText().toString();
                Intent intent = new Intent(Registration.this,verifyNumber.class);
                intent.putExtra("sent_OTP",s);
                intent.putExtra("phoneNumber",number);
                startActivity(intent);
            }
        };
    }
    public void signIn(PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    open_details_Form();
                }
                else{
                    Toast.makeText(Registration.this,"Something Went Worng",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void open_details_Form(){
        number= edit_Text_mobile.getText().toString();
        Intent intent = new Intent(Registration.this,details_form.class);
        intent.putExtra("phoneNumber",number);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            openMain();
        }
    }
    public void openMain(){
        Intent intent = new Intent(Registration.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}