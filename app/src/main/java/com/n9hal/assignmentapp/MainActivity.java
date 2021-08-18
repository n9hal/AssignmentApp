package com.n9hal.assignmentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    TextView txtTemp,txtMinTemp,txtMaxTemp,txtHumidity,txtClouds,txtDiscription,
            tvTemp,tvMinTemp,tvMaxTemp,tvHumidity,tvClouds;
    EditText editTextPin,
            editCity,editState;
    Button btnCheck,btnShow;
    ImageView btnSignOut;

    boolean animCounter=false,animCounter2=false;
    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((ViewGroup) findViewById(R.id.checkbtnanimation)).getLayoutTransition()
                .enableTransitionType(LayoutTransition.APPEARING);
        ((ViewGroup) findViewById(R.id.layoutShowbtn)).getLayoutTransition()
                .enableTransitionType(LayoutTransition.APPEARING);
        editTextPin =findViewById(R.id.editWPinCode);
        editCity=findViewById(R.id.editWCity);
        editState=findViewById(R.id.editWState);

        btnCheck=findViewById(R.id.btnWCheck);
        btnShow=findViewById(R.id.btnShow);

        txtMinTemp =findViewById(R.id.txtMinTemp);
        txtMaxTemp =findViewById(R.id.txtMaxTemp);
        txtTemp =findViewById(R.id.txtTemp);
        txtHumidity =findViewById(R.id.txtHumadity);
        txtClouds =findViewById(R.id.txtClouds);
        txtDiscription =findViewById(R.id.txtDiscription);
        tvMinTemp =findViewById(R.id.tvMinTemp);
        tvMaxTemp =findViewById(R.id.tvMaxTemp);
        tvTemp =findViewById(R.id.tvTemp);
        tvHumidity =findViewById(R.id.tvHumadity);
        tvClouds =findViewById(R.id.tvClouds);

        btnSignOut=findViewById(R.id.btnSignOut);

        String city=getIntent().getStringExtra("City");
        editCity.setText(city);
        String pincode=getIntent().getStringExtra("Pincode");
        editTextPin.setText(pincode);
        String State=getIntent().getStringExtra("State");
        editState.setText(State);

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

        //SIGNOUT FROM THE APP
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                if(user!=null){
                    mAuth.signOut();
                    Intent intent= new Intent(MainActivity.this,Registration.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        //SHOW BUTTON
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(animCounter){
                    animCounter=false;
                    btnShow.setText("Show Result");

                }
                else{
                    animCounter=true;
                    btnShow.setText("Loading...");
                    checkWheather();
                }

            }
        });


    }
    public void pinCodeCheck() {
        String pincode = editTextPin.getText().toString();
        if(TextUtils.isEmpty(pincode)){
            Toast.makeText(MainActivity.this,"Please Enter Pincode",Toast.LENGTH_LONG).show();
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
                    Toast.makeText(MainActivity.this,"Enter Correct Pincode",Toast.LENGTH_LONG).show();
                    animCounter=false;
                    btnCheck.setText("Check");
                }
            });
            requestQueue.add(jsonArrayRequest);
        }
    }

    //FECHING DATA FROM OPENWEATHER API
    public void checkWheather(){
        String cityname = editCity.getText().toString();
        if(cityname.isEmpty()){
            animCounter=false;
            btnShow.setText("Show Result");
            Toast.makeText(this,"Please Select Pincode",Toast.LENGTH_LONG).show();
        }
        else{
            String weatherapiURL= "https://api.openweathermap.org/data/2.5/weather?q="+cityname+"&appid=dca3922258b635ebbd1d0d1dcb15ca9c";
            Log.d("Wlink",weatherapiURL);
            RequestQueue requestQueue1=Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, weatherapiURL,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                animCounter2=false;
                                btnShow.setText("Show Result");
                                JSONObject mainObject=response.getJSONObject("main");
                                String take_temp = mainObject.get("temp").toString();
                                txtTemp.setText(take_temp);
                                tvTemp.setVisibility(View.VISIBLE);
                                String temp_min = mainObject.get("temp_min").toString();
                                txtMinTemp.setText(temp_min);
                                tvMinTemp.setVisibility(View.VISIBLE);
                                String temp_max = mainObject.get("temp_max").toString();
                                txtMaxTemp.setText(temp_max);
                                tvMaxTemp.setVisibility(View.VISIBLE);
                                String humidity = mainObject.get("humidity").toString();
                                txtHumidity.setText(humidity);
                                tvHumidity.setVisibility(View.VISIBLE);

                                JSONObject cloudsObj = response.getJSONObject("clouds");
                                String clouds = cloudsObj.get("all").toString();
                                Log.d("Clouds",cloudsObj.toString());
                                txtClouds.setText(clouds);
                                tvClouds.setVisibility(View.VISIBLE);

                                String weatherArr= response.getString("weather");
                                JSONArray jsonArray = new JSONArray(weatherArr);
                                Log.d("weatherDD",jsonArray.toString());
                                JSONObject details = jsonArray.getJSONObject(0);
                                String description = details.get("description").toString();
                                txtDiscription.setText(description);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    animCounter2=false;
                    btnShow.setText("Show Result");
                    Toast.makeText(MainActivity.this,"Error",Toast.LENGTH_SHORT).show();
                    Log.d("VolleyError", String.valueOf(error));
                }
            });
            requestQueue1.add(jsonObjectRequest);
        }
    }
}