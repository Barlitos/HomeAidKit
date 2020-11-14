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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class modifyDeleteCategory extends AppCompatActivity {

    private TextView name;
    protected EditText editCategoryName;
    private boolean isCategoryNameOk=false;
    private String postUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_deletecategory);
        postUrl=getString(R.string.host)+"editCategory.php";
        name=findViewById(R.id.selectedCategoryView);
        editCategoryName=findViewById(R.id.modifyCategoryInput);
        Bundle pack=getIntent().getExtras();

        final int user_id=getSharedPreferences("UserData",MODE_PRIVATE).getInt("user_id",-1);
        int category_id=0;
        if(pack!=null){
            name.setText(pack.getString("CategoryName"));
            category_id=pack.getInt("categoryId");
        }

        editCategoryName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(editCategoryName.getText().toString().isEmpty())
                {
                    setCategoryNameOk(false);
                }
                else setCategoryNameOk(true);
            }
        });
        editCategoryName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    if(!isCategoryNameOk)
                        editCategoryName.setError(getString(R.string.empty_name_error));
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

        Button accpetCategoryModify = findViewById(R.id.acceptCategoryModify);
        final int finalCategory_id = category_id;
        System.out.println(finalCategory_id);
        accpetCategoryModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formReadyToRequest()) {
                    PostRequest CategoryName = new PostRequest();
                    CategoryName.execute("categoryName", editCategoryName.getText().toString(),"user_id",String.valueOf(user_id),"categoryId",String.valueOf(finalCategory_id));
                }
                else
                {
                    Toast.makeText(modifyDeleteCategory.this, "Niepoprawnie wypełniono pole",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(modifyDeleteCategory.this,response.getString("message"),Toast.LENGTH_LONG).show();
                    openCategoriesActivity();
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