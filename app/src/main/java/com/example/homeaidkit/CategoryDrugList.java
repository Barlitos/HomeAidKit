package com.example.homeaidkit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CategoryDrugList extends AppCompatActivity implements DrugListAdapter.OnItemClickListener{
    private TextView name;
    private ListView drugListView;
    private DrugListAdapter adapter;
    private String url;
    private List<Drug> drugList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_drug_list);
        name=findViewById(R.id.selectedCategoryView);
        drugListView=findViewById(R.id.categorydrugList);
        url=getString(R.string.host)+"getCategoryDrugs.php";
        Bundle pack=getIntent().getExtras();

        final int userId=getSharedPreferences("UserData",MODE_PRIVATE).getInt("user_id",-1);
        int category_id=0;
        if(pack!=null){
            name.setText(pack.getString("name"));
            category_id=pack.getInt("categoryId");
        }

        getCategoryDrugList getlist=new getCategoryDrugList();
        getlist.execute("userId",String.valueOf(userId),"categoryId",String.valueOf(category_id));

        Button modifyCategory = findViewById(R.id.goToCategoryModify);
        final int finalCategory_id = category_id;
        modifyCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeleteModifyCategory(finalCategory_id);
            }
        });

        ImageButton home = findViewById(R.id.homeButton);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomeActivity();
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

        ImageButton categories = findViewById(R.id.categoriesButton);
        categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCategoriesActivity();
            }
        });
    }

    @Override
    public void onItemClickListener(Drug drug) {
        Intent openDrugModify=new Intent(CategoryDrugList.this,modifyDrug.class);
        openDrugModify.putExtra("name",drug.getName())
                .putExtra("quantity",drug.getQuantity())
                .putExtra("id",drug.getId())
                .putExtra("expDate",drug.getExpDate())
                .putExtra("index",drugList.indexOf(drug))
                .putExtra("unit",drug.getUnit());
        System.out.println(drugList.indexOf(drug));
        startActivityForResult(openDrugModify,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("RESULT");
        switch (requestCode)
        {
            case 1:
                if (resultCode == RESULT_OK && data!=null && drugList!=null) {
                    Bundle pack=data.getExtras();
                    drugList.get(pack.getInt("index")).setQuantity(pack.getInt("quantity"));
                    adapter.notifyDataSetChanged();
                }
                break;
            case 2:
                if (resultCode == RESULT_OK && data!=null && drugList!=null) {
                    Bundle pack=data.getExtras();
                    drugList.add(new Drug(pack.getInt("itemId"),
                            pack.getString("name"),
                            pack.getString("expDate"),
                            pack.getInt("quantity"),
                            pack.getInt("unit")));
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    private class getCategoryDrugList extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client =new OkHttpClient();
            RequestBody form=new FormBody.Builder()
                    .add(strings[0],strings[1])
                    .add(strings[2],strings[3])
                    .build();
            Request request=new Request.Builder()
                    .url(url)
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
                System.out.println(s);
                JSONObject object=new JSONObject(s);
                if(object.has("empty") && object.getInt("empty")==0) {
                    JSONArray drugArr=object.getJSONArray("drugList");
                    drugList= new ArrayList<>();
                    for (int i = 0; i <drugArr.length(); i++) {
                        object=drugArr.getJSONObject(i);
                        drugList.add(new Drug(object.getInt("id"),object.getString("name"),String.valueOf(object.get("expiration_date")),object.getInt("quantity"),object.getInt("unit_id")));
                    }
                    adapter=new DrugListAdapter(CategoryDrugList.this,drugList);
                    drugListView.setAdapter(adapter);
                }
                else{
                    Toast.makeText(CategoryDrugList.this,object.getString("message"),Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @SuppressWarnings("simpledateformat")
    public void dateSort(View view) {
        Collections.sort(drugList, new Comparator<Drug>() {
            @Override
            public int compare(Drug o1, Drug o2) {
                 SimpleDateFormat format=new SimpleDateFormat("dd-MM-yy");
                try {
                    Date date1=format.parse(o1.getExpDate());
                    Date date2=format.parse(o2.getExpDate());
                    assert date1 != null;
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        adapter.notifyDataSetChanged();
    }

    public void alphabeticalSort(View view) {
        Collections.sort(drugList);
        adapter.notifyDataSetChanged();
    }

    public void openDeleteModifyCategory(int a)
    {
        Intent intent = new Intent(this, modifyDeleteCategory.class);
        intent.putExtra("CategoryName",name.getText().toString()).putExtra("categoryId",a);
        startActivity(intent);
    }

    public void openHomeActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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

    public void openCategoriesActivity()
    {
        Intent intent = new Intent(this, categories.class);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {}
}
