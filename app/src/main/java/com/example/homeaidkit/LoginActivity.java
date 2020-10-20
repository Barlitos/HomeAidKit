package com.example.homeaidkit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String postUrl="http://192.168.0.2/HomeAidKit/login.php";
    protected EditText login;
    protected EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Bundle b = getIntent().getExtras();
        if(b!=null)
        {
            Toast.makeText(LoginActivity.this, b.getString("message"), Toast.LENGTH_SHORT).show();
        }
        login=findViewById(R.id.emailInput);
        password=findViewById(R.id.passwordInput);


        Button signIn = findViewById(R.id.signInButton);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(login.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this,"Login lub hasło nie są poprawne",Toast.LENGTH_LONG).show();
                }
                else {
                    PostRequest loginRequest = new PostRequest();
                    loginRequest.execute("login", login.getText().toString(), "password", password.getText().toString());
                }
            }
        });

        Button registration = findViewById(R.id.registrationButton);
        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegistrationActivity();
            }
        });
    }


    public void openMainActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openRegistrationActivity()
    {
        Intent intent = new Intent(this, register.class);
        startActivity(intent);
    }

    // --- Async task - performing post call to server-----
    private class PostRequest extends AsyncTask<String,Void ,String > {

        @Override
        protected void onPreExecute() {
        }
        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject object=new JSONObject(s);

                if(object.getInt("login")==1)
                {
                    if(object.has("password") && object.getInt("password")==0)
                    {
                        password.setError(object.getString("message"));
                    }
                    else {
                        SharedPreferences data=getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor=data.edit();
                        editor.putInt("user_id",object.getInt("user_id"));
                        editor.apply();
                        openMainActivity();
                    }
                }
                else {
                    login.setError(object.getString("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... strings){

            OkHttpClient client =new OkHttpClient();
            RequestBody form=new FormBody.Builder()
                    .add(strings[0],strings[1])
                    .add(strings[2],strings[3])
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