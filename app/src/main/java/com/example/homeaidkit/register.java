package com.example.homeaidkit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.textclassifier.TextLanguage;
import android.widget.Button;
import android.widget.EditText;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class register extends AppCompatActivity {
 protected EditText login;
 protected EditText email;
 protected EditText password;

 private static final String postUrl="http://192.168.0.2/HomeAidKit/createAccount.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        login=findViewById(R.id.loginInput);
        email=findViewById(R.id.emailInput);
        password=findViewById(R.id.password1Input);

        //Button with onclick listener
        Button register = findViewById(R.id.registerButton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostRequest registerRequest=new PostRequest();
               registerRequest.execute("login",login.getText().toString(),"email",email.getText().toString(),"password",password.getText().toString());
                //openLogingActivity();
            }
        });

        Button loggingIn = findViewById(R.id.loggingInButton);
        loggingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogingInActivity("");
            }
        });
    }

    public void openLogingInActivity(String message)
    {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("message", message);
        startActivity(intent);
    }

    // --- Async task - performing post call to server-----
    private class PostRequest extends AsyncTask<String,Void ,String >{

        @Override
        protected void onPreExecute() {
        }
        @Override
        protected void onPostExecute(String s) {
            // alert dialog ---DEBUGGING---- should delete

            AlertDialog alertDialog = new AlertDialog.Builder(register.this).create();
            alertDialog.setTitle("Alert");
            //alertDialog.setMessage(s);
            try {
                JSONObject object=new JSONObject(s);
                alertDialog.setMessage(object.getString("message"));
                if(object.getInt("email")==0)
                {
                    email.setError(object.getString("message"));
                }
                if(object.getInt("login")==0)
                {
                    login.setError(object.getString("message"));
                }

                if(object.getInt("success")==1)
                {
                    openLogingInActivity(object.getString("message"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            //------//
        }

        @Override
        protected String doInBackground(String... strings){

            OkHttpClient client =new OkHttpClient();
            RequestBody form=new FormBody.Builder()
                    .add(strings[0],strings[1])
                    .add(strings[2],strings[3])
                    .add(strings[4],strings[5])
                    .build();
            Request request=new Request.Builder()
                    .url(postUrl)
                    .post(form)
                    .build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert response != null;
            try {
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Unknown Error";
        }
    }
}