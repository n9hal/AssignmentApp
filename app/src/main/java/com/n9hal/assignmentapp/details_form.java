package com.n9hal.assignmentapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.LayoutTransition;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class details_form extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {


    EditText edit_Text_mobile2,editDOB,editName,
            editTextAdd1,editTextAdd2,editTextPin,
            editCity,editState;
    Button btnCheck,btnRegister;
    Spinner spinner;
    RequestQueue requestQueue;
    boolean animCounter=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_form);

        ((ViewGroup) findViewById(R.id.checkbtnanimation)).getLayoutTransition()
                .enableTransitionType(LayoutTransition.APPEARING);



        editName=findViewById(R.id.editName);
        editDOB=findViewById(R.id.editDOB);
        edit_Text_mobile2 = findViewById(R.id.edit_Text_mobile2);
        editTextAdd1 = findViewById(R.id.editTextAdd1);
        editTextAdd2 =findViewById(R.id.editTextAdd2);
        editTextPin =findViewById(R.id.editTextPinCode);
        editCity=findViewById(R.id.editCity);
        editState=findViewById(R.id.editState);

        spinner=findViewById(R.id.spinnerGen);

        btnCheck=findViewById(R.id.btnCheck);
        btnRegister=findViewById(R.id.btnRegister);

        //Mobile
        String number = getIntent().getStringExtra("phoneNumber");
        edit_Text_mobile2.setText(number);

        //Birthdate Picker
        editDOB.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                com.n9hal.assignmentapp.DatePicker datePicker;
                datePicker = new com.n9hal.assignmentapp.DatePicker();
                datePicker.show(getSupportFragmentManager(),"Date Pick");
            }
        });

        //DROPDOWN Menu for GENDER
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);



        //REGISTER BUTTON
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name= editName.getText().toString();
                String phoneNumber= edit_Text_mobile2.getText().toString();
                String bday = editDOB.getText().toString();
                String gender = getIntent().getStringExtra("Gender");
                String Add1 = editTextAdd1.getText().toString();
                String Add2 = editTextAdd2.getText().toString();
                String pinCode = editTextPin.getText().toString();
                String city = editCity.getText().toString();
                String state = editState.getText().toString();

                if(TextUtils.isEmpty(name) || TextUtils.isEmpty(bday) || TextUtils.isEmpty(gender) ||
                        TextUtils.isEmpty(Add1) || TextUtils.isEmpty(pinCode)) {
                    Toast.makeText(details_form.this, "Fill up all Details", Toast.LENGTH_LONG).show();
                }
                else{
                    saveData(name,phoneNumber,bday,gender,Add1,Add2,pinCode,city,state);
                    Intent intent = new Intent(details_form.this, MainActivity.class);
                    intent.putExtra("Pincode",pinCode);
                    intent.putExtra("City",city);
                    intent.putExtra("State",state);
                    startActivity(intent);
                    finish();
                }
            }
        });

        //CHECK BUTTON
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(animCounter){
                    animCounter=false;
                    btnCheck.setText("Check");

                }
                else{
                    animCounter=true;
                    btnCheck.setText("Checking");
                    pinCodeCheck();

                }
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String selectedDate = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());
        editDOB.setText(selectedDate);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedGen = parent.getItemAtPosition(position).toString();
        getIntent().putExtra("Gender",selectedGen);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //PINCODE API DATA FETCHING
    public void pinCodeCheck() {
        String pincode = editTextPin.getText().toString();
        if(TextUtils.isEmpty(pincode)){
            Toast.makeText(details_form.this,"Please Enter Pincode",Toast.LENGTH_LONG).show();
            animCounter=false;
            btnCheck.setText("Check");
        }
        else{
            String apiURL = "https://api.postalpincode.in/pincode/" + pincode;
            requestQueue = Volley.newRequestQueue(this);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                    apiURL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray jsonArray) {
                            try {
                                JSONObject jsonObject= jsonArray.getJSONObject(0);
                                String postOffice = jsonObject.getString("PostOffice");
                                JSONArray pos = new JSONArray(postOffice);
                                for(int i=0; i< pos.length();i++){
                                    JSONObject details = pos.getJSONObject(i);
                                    String cityName = details.getString("District");
                                    editCity.setText(cityName);
                                    String stateName = details.getString("State");
                                    editState.setText(stateName);
                                }
                                animCounter=false;
                                btnCheck.setText("Check");
                            }catch (Exception e){

                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(details_form.this,"Enter Correct Pincode",Toast.LENGTH_LONG).show();
                }
            });
            requestQueue.add(jsonArrayRequest);
        }

    }

    //REGISTER BUTTON
    public void saveData(String name,String phoneNumber,String bday,String gender,String Add1,String Add2, String pinCode,String city,String state){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String id = mAuth.getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef= db.collection("Users").document(id);
        Log.d("nehal",db.collection("users").document(id).toString());
        Map<String, Object> Users = new HashMap<>();
        Users.put("Name",name);
        Users.put("PhoneNumber",phoneNumber);
        Users.put("Birthdate",bday);
        Users.put("Gender",gender);
        Users.put("AddressLine1",Add1);
        Users.put("AddressLine2",Add2);
        Users.put("Pincode",pinCode);
        Users.put("City",city);
        Users.put("State",state);
        docRef.set(Users);
    }
}