package com.example.homeaidkit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class passwordEdit extends AppCompatActivity {
    protected EditText newPassword;
    protected EditText repeatPassword;
    protected EditText password;
    private static final String postUrl="http://192.168.0.6/HomeAidKit/changePassword.php";

    private boolean isNewPasswordOk=false,isRepeatPasswordOk=false,isPasswordOk=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_edit);

        newPassword=findViewById(R.id.newPassword1Input);
        repeatPassword=findViewById(R.id.newpassword2Input);
        password=findViewById(R.id.passwordInput);
        SharedPreferences data=getSharedPreferences("UserData",MODE_PRIVATE);
        final int user_id=data.getInt("user_id",-1);
        System.out.println(user_id);

        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(newPassword.getText().toString().isEmpty())
                {
                    newPassword.setError(getString(R.string.empty_login_error));
                    setNewPasswordOk(false);
                }
                else setNewPasswordOk(true);
            }
        });
        newPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    if(!isNewPasswordOk){
                        newPassword.setError(getString(R.string.empty_login_error));
                    }
                }
            }
        });

        repeatPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(repeatPassword.getText().toString().isEmpty())
                {
                    repeatPassword.setError(getString(R.string.empty_login_error));
                    setRepeatPasswordOk(false);
                }
                else setRepeatPasswordOk(true);
            }
        });
        repeatPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!(repeatPassword.getText().toString().equals(newPassword.getText().toString())))
                {
                    repeatPassword.setError("Wprowadzone hasła różnią się!");
                }
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(password.getText().toString().isEmpty())
                {
                    password.setError(getString(R.string.empty_login_error));
                    setPasswordOk(false);
                }
                else setPasswordOk(true);
            }
        });
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    if(!isPasswordOk){
                        password.setError(getString(R.string.empty_login_error));
                    }
                }
            }
        });

        Button account = findViewById(R.id.accountButton);
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAccountActivity();
            }
        });

        Button logOut = findViewById(R.id.logOutButton);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogOutActivity();
            }
        });

        Button approveEmailEdit = findViewById(R.id.approveEditPasswordButton);
        approveEmailEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formReadyToRequest()) {
                    if (newPassword.getText().toString().isEmpty()
                || !repeatPassword.getText().toString().equals(newPassword.getText().toString())
                            || password.getText().toString().isEmpty()) {
                        Toast.makeText(passwordEdit.this, "Niepoprawnie wypełniono pola",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        PostRequest changePassword = new PostRequest();
                        changePassword.execute("user_id", String.valueOf(user_id), "newPassword", newPassword.getText().toString(), "password", password.getText().toString());
                    }
                }
            }
        });
    }

    private class PostRequest extends AsyncTask<String,Void,String>
    {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try
            {
                JSONObject response = new JSONObject(s);
                if(response.has("success") && response.getInt("success")==1)
                {
                    Toast.makeText(passwordEdit.this,response.getString("message"),Toast.LENGTH_LONG).show();
                    finish();
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
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

    public void openAccountActivity()
    {
        Intent intent = new Intent(this, account.class);
        startActivity(intent);
    }

    public void openLogOutActivity()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void openEditPasswordlActivity()
    {
        Intent intent = new Intent(this, account.class);
        startActivity(intent);
    }

    private boolean formReadyToRequest()
    {
        return isNewPasswordOk() && isRepeatPasswordOk() && isPasswordOk();
    }

    // validation flags getters/ setters
    public boolean isNewPasswordOk() {
        return isNewPasswordOk;
    }

    public void setNewPasswordOk(boolean nPassOk) {
        isNewPasswordOk = nPassOk;
    }

    public boolean isRepeatPasswordOk() {
        return isRepeatPasswordOk;
    }

    public void setRepeatPasswordOk(boolean rPassOk) {
        isRepeatPasswordOk = rPassOk;
    }

    public boolean isPasswordOk() {
        return isPasswordOk;
    }

    public void setPasswordOk(boolean passwordOk) {
        isPasswordOk = passwordOk;
    }
}