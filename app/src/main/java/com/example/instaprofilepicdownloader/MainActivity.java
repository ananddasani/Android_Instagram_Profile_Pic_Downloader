package com.example.instaprofilepicdownloader;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Button normalPic, hdPic;
    Button normalDownload, fastDownload;
    TextInputLayout editText;
    ImageView imageView;

    //Contains Main Actual URL
    String userGivenURl = "";
    String normalProfilePicUrl = "";
    String hdProfilePicUrl = "";

    //For Verification Purpose
    boolean normalPicButtonClicked = false;
    boolean hdPicButtonClicked = false;
    boolean loaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        normalPic = findViewById(R.id.normalPic);
        hdPic = findViewById(R.id.hdPic);
        normalDownload = findViewById(R.id.normalDownload);
        fastDownload = findViewById(R.id.fastDownload);
        editText = findViewById(R.id.input_layout);
        imageView = findViewById(R.id.imageView);

        normalPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();

                userGivenURl = editText.getEditText().getText().toString();
                normalPicButtonClicked = true;
                hdPicButtonClicked = false;

                if (!userGivenURl.equals("")) {
                    fetchProfilePicUrls(userGivenURl, v);  //fetch the profile pic from Insta API
                    userGivenURl = ""; //flushing
                } else
                    Snackbar.make(v, "Please Enter Profile Pic URL", Snackbar.LENGTH_LONG).show();
            }
        });

        hdPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();

                userGivenURl = editText.getEditText().getText().toString();
                normalPicButtonClicked = false;
                hdPicButtonClicked = true;

                if (!userGivenURl.equals("")) {
                    fetchProfilePicUrls(userGivenURl, v); //fetch the profile pic from Insta API
                    userGivenURl = ""; //flushing
                } else
                    Snackbar.make(v, "Please Enter Profile Pic URL", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    //fetch Normal and HD profile URls
    public void fetchProfilePicUrls(String userGivenURl, View view) {

        //make a custom url
        String tempURL = StringUtils.substringBefore(userGivenURl, "?");
        String finalUrl = tempURL + "?__a=1";

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        // { graphql : { profile_pic_url, profile_pic_url_hd } }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, finalUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    JSONObject graphql = response.getJSONObject("graphql");
                    JSONObject user = graphql.getJSONObject("user");
                    normalProfilePicUrl = user.getString("profile_pic_url");
                    hdProfilePicUrl = user.getString("profile_pic_url_hd");

                    //let glide load the Profile pic
                    loaded = true;

                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "Error in Instagram API", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("InvalidURL", error.getMessage());
                Snackbar.make(view, "Invalid Link", Snackbar.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonObjectRequest);


        //------------------ Using DeSerialization with Volley-----------------
        /*
        StringRequest stringRequest = new StringRequest(finalUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();

                try {
                    //get the response
                    FinalPhase finalPhase = gson.fromJson(response, FinalPhase.class);
                    normalProfilePicUrl = finalPhase.getGraphql().getUser().getProfile_pic_url();
                    hdProfilePicUrl = finalPhase.getGraphql().getUser().getProfile_pic_url_hd();

                    //let glide load the Profile pic
                    loaded = true;

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Error in Instagram API", Toast.LENGTH_SHORT).show();
                    Log.e("ApiError", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("InvalidURL", error.getMessage());
                Snackbar.make(view, "Invalid Link", Snackbar.LENGTH_LONG).show();
            }
        });

        requestQueue.add(stringRequest);
         */

        if (loaded) {
            loadThePic(); //now Glide will load the pic into ImageView
            loadThePic();
        }
    }

    //Function to load the actual pic in ImageView (Normal or HD)
    public void loadThePic() {
        if (normalPicButtonClicked && !hdPicButtonClicked) {
            //now load the normal one using glide
            Glide.with(getApplicationContext()).load(Uri.parse(normalProfilePicUrl)).into(imageView);
            Toast.makeText(MainActivity.this, "Normal Resolution", Toast.LENGTH_SHORT).show();

        } else if (hdPicButtonClicked && !normalPicButtonClicked) {
            //now load the hd one using glide
            Glide.with(getApplicationContext()).load(Uri.parse(hdProfilePicUrl)).into(imageView);
            Toast.makeText(MainActivity.this, "HD Resolution", Toast.LENGTH_SHORT).show();
        }
    }

    //hide the keyboard
    public void hideKeyboard() {

        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Unable to hide the keyboard", Toast.LENGTH_SHORT).show();
        }
    }
}