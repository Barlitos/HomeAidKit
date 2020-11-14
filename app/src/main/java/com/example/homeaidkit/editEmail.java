package com.example.homeaidkit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class editEmail extends AppCompatActivity {
    protected EditText newEmail;
    protected EditText repeatEmail;
    protected EditText password;
    private  String postUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email);
        postUrl=getString(R.string.host)+"changeEmail.php";
        newEmail=findViewById(R.id.emailEdit1Input);
        repeatEmail=findViewById(R.id.emailEdit2Input);
        password=findViewById(R.id.passwordInput);
        SharedPreferences data=getSharedPreferences("UserData",MODE_PRIVATE);
         final int user_id=data.getInt("user_id",-1);
        System.out.println("USER ID"+user_id);
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

        Button approveEmailEdit = findViewById(R.id.approveEditEmailButton);
        approveEmailEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail.getText()).matches()
                && android.util.Patterns.EMAIL_ADDRESS.matcher(repeatEmail.getText()).matches()
                && !password.getText().toString().isEmpty())
                {
                    PostRequest changeEmail=new PostRequest();
                    changeEmail.execute("user_id",String.valueOf(user_id),"newEmail",newEmail.getText().toString(),"password",password.getText().toString());
                }
                else
                {
                    Toast.makeText(editEmail.this,"Niepoprawnie wypełniono pola",Toast.LENGTH_SHORT).show();
                    if(newEmail.getText().toString().isEmpty()){
                        newEmail.setError(getString(R.string.empty_login_error));
                    }
                    if(repeatEmail.getText().toString().isEmpty()){
                        repeatEmail.setError(getString(R.string.empty_login_error));
                    }
                    if(password.getText().toString().isEmpty()){
                        password.setError(getString(R.string.empty_login_error));
                    }
                }
               openEditEmailActivity();
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
                    Toast.makeText(editEmail.this,response.getString("message"),Toast.LENGTH_LONG).show();
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

    public void openEditEmailActivity()
    {
        Intent intent = new Intent(this, account.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {}
}