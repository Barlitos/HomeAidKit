package com.example.homeaidkit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class categories extends AppCompatActivity implements CategoryListAdapter.OnItemClickListener{

    private String categoryListUrl;
    private List<Category> categoryList;
    private ListView categoryListView;
    private CategoryListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        categoryListUrl=getString(R.string.host)+"getUsersCategories.php";
        final int user_id= getSharedPreferences("UserData",MODE_PRIVATE).getInt("user_id",-1);
        GetUsersCategories getlist=new GetUsersCategories();
        getlist.execute("user_id",String.valueOf(user_id));

        categoryListView=findViewById(R.id.categoryList);

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

        Button addCategory = findViewById(R.id.addingCategoryButton);
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddCategoryActivity();
            }
        });
    }

    @Override
    public void onItemClickListener(Category cat) {
        Intent openModCategory=new Intent(categories.this,CategoryDrugList.class);
        openModCategory.putExtra("name",cat.getName())
        .putExtra("categoryId",cat.getId());
        startActivity(openModCategory);
    }


    private class GetUsersCategories extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client =new OkHttpClient();
            RequestBody form=new FormBody.Builder()
                    .add(strings[0],strings[1])
                    //.add(strings[2],strings[3])
                    .build();
            Request request=new Request.Builder()
                    .url(categoryListUrl)
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

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject object=new JSONObject(s);
                if(object.has("empty") && object.getInt("empty")==0) {
                    JSONArray categoryArr=object.getJSONArray("categories");
                    categoryList= new ArrayList<>();
                    for (int i = 0; i <categoryArr.length(); i++) {
                        object=categoryArr.getJSONObject(i);
                        categoryList.add(new Category(object.getInt("id"),object.getString("category_name")));
                    }
                    adapter=new CategoryListAdapter(categories.this,categoryList);
                    categoryListView.setAdapter(adapter);
                    System.out.println(categoryList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        startActivity(intent);
    }

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

    public void openAddCategoryActivity()
    {
        Intent intent = new Intent(this, addCategory.class);
        startActivity(intent);
    }
}