package com.example.homeaidkit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.textclassifier.TextLanguage;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
 protected EditText repeatPassword;
 //validation flags
 private boolean isEmailOk=false,isLoginOk=false,isPasswordOk=false;

 private static final String postUrl="http://192.168.8.118/HomeAidKit/createAccount.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        login=findViewById(R.id.loginInput);
        email=findViewById(R.id.emailInput);
        password=findViewById(R.id.password1Input);
        repeatPassword=findViewById(R.id.password2Input);

        login.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(login.getText().toString().isEmpty())
                {
                    setLoginOk(false);
                }
                else setLoginOk(true);
            }
        });
        login.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    if(!isLoginOk)
                    login.setError(getString(R.string.empty_login_error));
                }
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                    if(android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()){
                    setEmailOk(true);
                    }
                    else {
                        setEmailOk(false);
                    }
            }
        });
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    if (email.getText().toString().isEmpty() || !isEmailOk()) {
                        email.setError(getString(R.string.incorrect_email_error));
                    }
                }
            }
        });

        //Buttons with onclick listeners
        Button register = findViewById(R.id.registerButton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(formReadyToRequest()) {
                    PostRequest registerRequest = new PostRequest();
                    registerRequest.execute("login", login.getText().toString(), "email", email.getText().toString(), "password", password.getText().toString());
                }
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
    // ----END OF ASYNCTASK CLASS

    private boolean formReadyToRequest()
    {
        return isLoginOk() && isEmailOk() && isPasswordOk();
    }

    // validation flags getters/ setters
    public boolean isEmailOk() {
        return isEmailOk;
    }

    public void setEmailOk(boolean emailOk) {
        isEmailOk = emailOk;
    }

    public boolean isLoginOk() {
        return isLoginOk;
    }

    public void setLoginOk(boolean loginOk) {
        isLoginOk = loginOk;
    }

    public boolean isPasswordOk() {
        return isPasswordOk;
    }

    public void setPasswordOk(boolean passwordOk) {
        isPasswordOk = passwordOk;
    }
}