package com.example.homeaidkit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
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

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String postUrl="http://192.168.0.3/HomeAidKit/login.php";
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
                PostRequest loginRequest=new PostRequest();
                loginRequest.execute("login",login.getText().toString(),"password",password.getText().toString());
                //
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
            // alert dialog ---DEBUGGING---- should delete

           // AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
            //alertDialog.setTitle("Alert");
            //alertDialog.setMessage(s);
            try {
                JSONObject object=new JSONObject(s);
              //  alertDialog.setMessage(object.getString("message"));

                if(object.getInt("login")==1)
                {
                    if(object.has("password") && object.getInt("password")==0)
                    {
                        password.setError(object.getString("message"));
                    }
                    else {
                        openMainActivity();
                    }
                }
                else {
                    login.setError(object.getString("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
              //      new DialogInterface.OnClickListener() {
                //        public void onClick(DialogInterface dialog, int which) {
                  //          dialog.dismiss();
                    //    }
                    //});
            //alertDialog.show();
            //------//

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