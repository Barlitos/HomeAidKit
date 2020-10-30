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
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class addCategory extends AppCompatActivity {

    protected EditText categoryName;
    private boolean isCategoryNameOk=false;

    private String postUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        postUrl=getString(R.string.host)+"addCategory.php";

        categoryName=findViewById(R.id.categoryInput);
        SharedPreferences data=getSharedPreferences("UserData",MODE_PRIVATE);
        final int user_id=data.getInt("user_id",-1);
        categoryName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(categoryName.getText().toString().isEmpty())
                {
                    setCategoryNameOk(false);
                }
                else setCategoryNameOk(true);
            }
        });
        categoryName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    if(!isCategoryNameOk)
                        categoryName.setError(getString(R.string.empty_name_error));
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

        ImageButton mostUsed = findViewById(R.id.mostUsedButton);
        mostUsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMostUsedActivity();
            }
        });

        ImageButton addDrug = findViewById(R.id.addDrugButton);
        addDrug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddDrugActivity();
            }
        });

        ImageButton home = findViewById(R.id.hometButton);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomeActivity();
            }
        });

        ImageButton categories = findViewById(R.id.categoriesButton);
        categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCategoriesActivity();
            }
        });

        Button addCategory = findViewById(R.id.addCategoryButton);
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formReadyToRequest()) {
                    PostRequest CategoryName = new PostRequest();
                    CategoryName.execute("categoryName", categoryName.getText().toString(),"user_id",String.valueOf(user_id));
                    }
                    else
                        {
                            Toast.makeText(addCategory.this, "Niepoprawnie wypełniono pole",Toast.LENGTH_SHORT).show();
                    }
                }
        });
    }

    private class PostRequest extends AsyncTask<String,Void ,String > {

        @Override
        protected void onPreExecute() {
        }
        @Override
        protected void onPostExecute(String s) {
            try
            {
                JSONObject response = new JSONObject(s);
                if(response.has("message"))
                {
                    Toast.makeText(addCategory.this,response.getString("message"),Toast.LENGTH_LONG).show();
                    finish();
                }
            }
            catch (JSONException e)
            {
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

    public void openMostUsedActivity()
    {
        Intent intent = new Intent(this, mostUsed.class);
        startActivity(intent);    }

    public void openAddDrugActivity()
    {
        Intent intent = new Intent(this, addDrug.class);
        startActivity(intent);
    }

    public void openHomeActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openCategoriesActivity()
    {
        Intent intent = new Intent(this, categories.class);
        startActivity(intent);
    }

    public void openAddCategoryActivity()
    {
        Intent intent = new Intent(this, categories.class);
        startActivity(intent);
    }

    private boolean formReadyToRequest() { return isCategoryNameOk();}

    public boolean isCategoryNameOk() {
        return isCategoryNameOk;
    }
    public void setCategoryNameOk(boolean CategoryNameOk) {
        isCategoryNameOk = CategoryNameOk;
    }
}