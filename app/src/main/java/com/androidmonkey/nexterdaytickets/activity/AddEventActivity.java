package com.androidmonkey.nexterdaytickets.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddEventActivity extends AppCompatActivity {

    //Other Declaration
    private static final String EM_BASE_URL = "http://192.168.0.9:3000";
    private static final String DV_BASE_URL = "http://192.168.137.1:3000";
    private static String BASE_URL;
    private static final String ADD_EVENT_URL = "/api/events/add";
    Long eventTimeStamp;

    //UI Declarations
    TextView textViewAddEventError,textViewAddEventTime;
    EditText editTextAddEventTitle,editTextAddEventDescription,editTextAddEventImageURL;
    EditText editTextAddEventFee,editTextAddEventVenue,editTextRegisterMax;
    Button buttonAddEventViewImage,buttonAddEventTimePicker,buttonPushEvent;
    ImageView imageViewAddEventDisplay;
    SpinKitView spinKitAddEventPush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        BASE_URL = checkRunningEnvironment();
        linkUpXML();
        setUpOnClickListeners();
    }

    private void pushEvent(final String token, final String host, final String title, final String description, final String location, final String imageURL, int fee, final int maxAttendees, final long eventTimeStamp){
        showSpinKit(true);

        if(token==null) {
            return;
        }
        StringRequest addEventRequest = new StringRequest(Request.Method.POST,BASE_URL+ADD_EVENT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                textViewAddEventError.setText(response);
                showSpinKit(false);
                if(response.equals("EVENT_ADDED")){
                    Toast.makeText(AddEventActivity.this, "Event Added", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textViewAddEventError.setText(error.toString());
                showSpinKit(false);
                error.printStackTrace();
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("auth-token", token);
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody(){
                String mRequestBody;
                try {
                    JSONObject jsonBody = new JSONObject();

                    jsonBody.put("host", host);
                    jsonBody.put("title", title );
                    jsonBody.put("description", description);

                    jsonBody.put("location", location);
                    jsonBody.put("eventTimeStamp", eventTimeStamp);
                    jsonBody.put("imageURL", imageURL);
                    jsonBody.put("maxAttendees", maxAttendees);

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
        requestQueue.add(addEventRequest);
    }

    private void linkUpXML(){

        editTextAddEventTitle = findViewById(R.id.editTextAddEventTitle);
        editTextAddEventDescription = findViewById(R.id.editTextAddEventDescription);
        editTextAddEventImageURL = findViewById(R.id.editTextAddEventImageURL);
        imageViewAddEventDisplay = findViewById(R.id.imageViewAddEventDisplay);
        //editTextAddEventContactDetails = findViewById(R.id.editTextAddEventContactDetails);
        editTextAddEventFee = findViewById(R.id.editTextAddEventFee);
        editTextAddEventVenue = findViewById(R.id.editTextAddEventVenue);
        editTextRegisterMax = findViewById(R.id.editTextRegisterMax);

        buttonAddEventViewImage = findViewById(R.id.buttonAddEventViewImage);
        buttonAddEventTimePicker = findViewById(R.id.buttonAddEventTimePicker);
        //buttonAddEventDatePicker = findViewById(R.id.buttonAddEventDatePicker);
        buttonPushEvent = findViewById(R.id.buttonPushEvent);
        textViewAddEventTime = findViewById(R.id.textViewAddEventTime);
        textViewAddEventError = findViewById(R.id.textViewAddEventError);
        spinKitAddEventPush = findViewById(R.id.spinKitAddEventPush);

    }

    private void setUpOnClickListeners(){
        buttonAddEventViewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageURL = editTextAddEventImageURL.getText().toString();
                if(isValidURL(imageURL)){
                    Glide.with(imageViewAddEventDisplay).load(imageURL).into(imageViewAddEventDisplay);
                }
                else{
                    Toast.makeText(AddEventActivity.this, "Invalid URL", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonAddEventTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewAddEventTime.setText("No date/time selected.");
                eventTimeStamp=0L;
                timePicker();
                datePicker();
            }
        });

        buttonPushEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences userDetailsSharedPreferences = getSharedPreferences("userDetails", MODE_PRIVATE);
                final String host = userDetailsSharedPreferences.getString("email", null);
                final String storedToken = userDetailsSharedPreferences.getString("token", null);


                final String title = editTextAddEventTitle.getText().toString();
                final String description = editTextAddEventDescription.getText().toString();
                final String imageURL = editTextAddEventImageURL.getText().toString();
                final String location = editTextAddEventVenue.getText().toString();
                //final String contactDetails = editTextAddEventContactDetails.getText().toString();
                final String stringFee = editTextAddEventFee.getText().toString();
                final String stringMaxAttendees = editTextRegisterMax.getText().toString();
                Integer fee, maxAttendees;

                if(host==null){
                    textViewAddEventError.setVisibility(View.VISIBLE);
                    textViewAddEventError.setText("Please, log out and try again.");
                }
                else if(stringMaxAttendees.isEmpty() || stringFee.isEmpty() || title.isEmpty() || description.isEmpty() || imageURL.isEmpty() || location.isEmpty()){
                    textViewAddEventError.setText("All fields must be entered");
                    textViewAddEventError.setVisibility(View.VISIBLE);

                }
                else{
                    try {
                        fee = Integer.valueOf(stringFee);
                        maxAttendees = Integer.valueOf(stringMaxAttendees);
                        pushEvent(storedToken,host,title,description,location,imageURL, fee, maxAttendees,eventTimeStamp);
                    }catch (Error error){
                        error.printStackTrace();
                        textViewAddEventError.setVisibility(View.VISIBLE);
                        textViewAddEventError.setText("Please, fill all fields correctly.");
                    }
                }
            }
        });
    }

    private void timePicker(){
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String outputHour = String.valueOf(hourOfDay);
                String outputMinute = String.valueOf(minute);
                long timeOfDay = Long.valueOf(outputHour)*3600+Long.valueOf(outputMinute)*60;
                eventTimeStamp+=timeOfDay;

                //Display The Time
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy \nHH:mm:ss");
                Date eventDate = new Date(eventTimeStamp*1000);
                textViewAddEventTime.setText(dateFormat.format(eventDate));
            }
        },calendar.get(Calendar.HOUR),calendar.get(Calendar.MINUTE),true);
        timePickerDialog.show();
    }

    private void datePicker(){
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String outputDay = String.valueOf(dayOfMonth);
                String outputMonth = String.valueOf(month+1);

                if(dayOfMonth<10)
                    outputDay = "0"+outputDay;

                if(month<9)
                    outputMonth = "0"+outputMonth;

                String date = outputDay+"/"+outputMonth+"/"+year;

                SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
                Date dt1 = null;
                try {
                    dt1 = format1.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                eventTimeStamp += dt1.getTime()/1000;
            }
        },calendar.get(Calendar.YEAR) ,calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showSpinKit(Boolean show){
        if(show){
            spinKitAddEventPush.setVisibility(View.VISIBLE);
            buttonPushEvent.setVisibility(View.GONE);
            textViewAddEventError.setVisibility(View.GONE);
        }else{
            spinKitAddEventPush.setVisibility(View.GONE);
            buttonPushEvent.setVisibility(View.VISIBLE);
            textViewAddEventError.setVisibility(View.VISIBLE);
        }
    }

    public static boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
            return true;
        }
        catch (Exception e) {
            return false;
        }
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
}
