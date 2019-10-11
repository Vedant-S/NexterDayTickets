package com.androidmonkey.nexterdaytickets.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidmonkey.nexterdaytickets.R;
import com.github.ybq.android.spinkit.SpinKitView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class RegisterActivity extends AppCompatActivity {

    //Constants
    private static final String DV_REGISTER_URL = "http://192.168.137.1:3000/api/user/register";
    private static final String EM_REGISTER_URL = "http://192.168.0.9:3000/api/user/register";

    //UI Declarations
    TextView textViewRegisterError;
    EditText editTextRegisterEmail,editTextRegisterName,editTextRegisterPassword;
    Button buttonRegisterSignUp,buttonRegisterSignIn;
    SpinKitView spinKitRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //UI Linking
        editTextRegisterPassword = findViewById(R.id.editTextRegisterPassword);
        editTextRegisterName = findViewById(R.id.editTextRegisterName);
        editTextRegisterEmail = findViewById(R.id.editTextRegisterEmail);
        buttonRegisterSignUp = findViewById(R.id.buttonRegisterSignUp);
        buttonRegisterSignIn = findViewById(R.id.buttonRegisterSignIn);
        textViewRegisterError = findViewById(R.id.textViewRegisterError);
        spinKitRegister = findViewById(R.id.spinKitRegister);

        //Sends user back to Login Activity
        buttonRegisterSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });

        //On Click listener for Sign Up Button
        buttonRegisterSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = editTextRegisterName.getText().toString();
                final String email = editTextRegisterEmail.getText().toString();
                final String password = editTextRegisterPassword.getText().toString();

                if(email.isEmpty() || password.isEmpty() || name.isEmpty()){
                    textViewRegisterError.setVisibility(View.VISIBLE);
                    textViewRegisterError.setText("Please, enter name, email and password!");
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    textViewRegisterError.setVisibility(View.VISIBLE);
                    textViewRegisterError.setText("Please, enter a valid email.");
                }
                else if(name.length()<6){
                    textViewRegisterError.setVisibility(View.VISIBLE);
                    textViewRegisterError.setText("Name must be longer than 6 characters.");
                }
                else if(password.length()<6){
                    textViewRegisterError.setVisibility(View.VISIBLE);
                    textViewRegisterError.setText("Password must be longer than 6 characters.");
                }
                else{
                    registerUser(name,email,password);
                }
            }
        });
    }

    //Register User Function
    private void registerUser(final String name, final String email, final String password) {

        showSpinKit(true);
        boolean inEmulator = "generic".equals(Build.BRAND.toLowerCase());
        String PASSED_LOGIN_URL;
        if(inEmulator){
            PASSED_LOGIN_URL = EM_REGISTER_URL;
        }else{
            PASSED_LOGIN_URL = DV_REGISTER_URL;
        }

        StringRequest loginStringRequest = new StringRequest(Request.Method.POST,PASSED_LOGIN_URL,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                textViewRegisterError.setVisibility(View.VISIBLE);
                textViewRegisterError.setText(response);
                if(response.contains("SUCCESS:")){
                    Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
                    loginIntent.putExtra("passedEmail", email);
                    startActivity(loginIntent);
                    finish();
                }
                showSpinKit(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textViewRegisterError.setVisibility(View.VISIBLE);
                textViewRegisterError.setText("Error: "+error.toString());
                showSpinKit(false);
            }
        })
        {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody(){
                String mRequestBody;
                try {
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("email", email);
                    jsonBody.put("password", password);
                    jsonBody.put("name", name);
                    mRequestBody = jsonBody.toString();
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s","utf-8");
                    showSpinKit(false);
                    return null;
                } catch (JSONException e) {
                    e.printStackTrace();
                    showSpinKit(false);
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String parsed;
                try {
                    parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                } catch (UnsupportedEncodingException e) {
                    parsed = new String(response.data);
                    showSpinKit(false);
                }
                return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(loginStringRequest);

    }

    private void showSpinKit(Boolean show){
        if(show){
            spinKitRegister.setVisibility(View.VISIBLE);
            buttonRegisterSignUp.setVisibility(View.GONE);
            buttonRegisterSignIn.setVisibility(View.GONE);
        }else{
            spinKitRegister.setVisibility(View.GONE);
            buttonRegisterSignUp.setVisibility(View.VISIBLE);
            buttonRegisterSignIn.setVisibility(View.VISIBLE);
        }
    }
}
