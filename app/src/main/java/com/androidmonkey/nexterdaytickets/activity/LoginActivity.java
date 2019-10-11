package com.androidmonkey.nexterdaytickets.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    //Constant
    private static final int SPLASH_TIME = 3000;
    private static final String EM_BASE_URL = "http://192.168.0.9:3000";
    private static final String DV_BASE_URL = "http://192.168.137.1:3000";
    private static String BASE_URL;
    private static final String LOGIN_URL = "/api/user/login";
    private static final String TEST_URL = "/api/test";
    private static final String TOKEN_VALIDATE_URL = "/api/validate";

    //Volley Declaration
    RequestQueue requestQueue;

    //UI Declarations
    TextView textViewLoginError;
    SpinKitView spinKitLogin;
    EditText editTextLoginEmail,editTextLoginPassword;
    Button buttonLoginSignIn,buttonLoginSignUp;
    RelativeLayout relativeLayoutSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //UI Linking
        relativeLayoutSplash = findViewById(R.id.relativeLayoutSplash);
        editTextLoginEmail = findViewById(R.id.editTextLoginEmail);
        editTextLoginPassword = findViewById(R.id.editTextLoginPassword);
        buttonLoginSignIn = findViewById(R.id.buttonLoginSignIn);
        buttonLoginSignUp = findViewById(R.id.buttonLoginSignUp);
        textViewLoginError = findViewById(R.id.textViewLoginError);
        spinKitLogin = findViewById(R.id.spinKitLogin);

        BASE_URL = checkRunningEnvironment();
        requestQueue = Volley.newRequestQueue(this);
        loginUsingToken();
        editTextLoginEmail.setText(getIntent().getStringExtra("passedEmail"));

        //Sends the user to Register Activity
        buttonLoginSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });

        //On Click listener for Sign In Button
        buttonLoginSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = editTextLoginEmail.getText().toString();
                final String password = editTextLoginPassword.getText().toString();

                if(email.isEmpty() || password.isEmpty()){
                    textViewLoginError.setVisibility(View.VISIBLE);
                    textViewLoginError.setText("Please, enter email and password!");
                }
                else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    textViewLoginError.setVisibility(View.VISIBLE);
                    textViewLoginError.setText("Please, enter a valid email.");
                }
                else if(password.length()<6){
                    textViewLoginError.setVisibility(View.VISIBLE);
                    textViewLoginError.setText("Password must be longer than 6 characters.");
                }
                else{
                    signInUser(email,password);
                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                relativeLayoutSplash.setVisibility(View.GONE);
            }
        }, SPLASH_TIME);
    }

    private String checkRunningEnvironment() {
        boolean inEmulator = "generic".equals(Build.BRAND.toLowerCase());
        String PASSED_LOGIN_URL;
        if(inEmulator){
            return EM_BASE_URL;
        }else{
            return DV_BASE_URL;
        }
    }

    //Login User Function
    private void signInUser(final String email, final String password) {

        showSpinKit(true);

        StringRequest loginStringRequest = new StringRequest(Request.Method.POST,BASE_URL+LOGIN_URL,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showSpinKit(false);
                textViewLoginError.setVisibility(View.VISIBLE);
                textViewLoginError.setText(response);
                if(response.contains("SUCCESS:")){

                    //API Gives Payload which was JWT, email & name of user
                    String payload = response.substring(8);
                    String[] arrayPayload = payload.split(";");
                    //Storing the Token In Shared Preference
                    if(arrayPayload.length==3){
                        SharedPreferences userDetailsSharedPreferences = getSharedPreferences("userDetails",MODE_PRIVATE);
                        SharedPreferences.Editor userDetailsEditor = userDetailsSharedPreferences.edit();
                        userDetailsEditor.putString("token",arrayPayload[0]);
                        userDetailsEditor.putString("email",arrayPayload[1]);
                        userDetailsEditor.putString("name",arrayPayload[2]);
                        userDetailsEditor.apply();
                    }
                    startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textViewLoginError.setVisibility(View.VISIBLE);
                textViewLoginError.setText("Error: "+error.toString());
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
        requestQueue.add(loginStringRequest);
    }

    void loginUsingToken(){
        SharedPreferences userDetailsSharedPreferences = getSharedPreferences("userDetails",MODE_PRIVATE);
        final String storedToken = userDetailsSharedPreferences.getString("token", null);
        if(storedToken==null) {
            checkServer();
            return;
        }
        StringRequest tokenLoginRequest = new StringRequest(BASE_URL+TOKEN_VALIDATE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("CREDENTIALS_VALID")){
                    startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("auth-token", storedToken);
                return params;
            }
        };
        requestQueue.add(tokenLoginRequest);
    }

    private void showSpinKit(Boolean show){
        if(show){
            spinKitLogin.setVisibility(View.VISIBLE);
            buttonLoginSignUp.setVisibility(View.GONE);
            buttonLoginSignIn.setVisibility(View.GONE);
        }else{
            spinKitLogin.setVisibility(View.GONE);
            buttonLoginSignUp.setVisibility(View.VISIBLE);
            buttonLoginSignIn.setVisibility(View.VISIBLE);
        }
    }

    void checkServer(){

        StringRequest stringRequestCheck = new StringRequest(BASE_URL+TEST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                textViewLoginError.setVisibility(View.VISIBLE);
                textViewLoginError.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textViewLoginError.setVisibility(View.VISIBLE);
                textViewLoginError.setText(error.toString());
            }
        });
        requestQueue.add(stringRequestCheck);
    }
}
