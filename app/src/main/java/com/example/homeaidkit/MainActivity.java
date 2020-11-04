package com.example.homeaidkit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements DrugListAdapter.OnItemClickListener {

    private String drugListUrl;
    private List<Drug> drugList;
    private ListView drugListView;
    private DrugListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drugListUrl=getString(R.string.host)+"getUsersDrugs.php";
        final int user_id= getSharedPreferences("UserData",MODE_PRIVATE).getInt("user_id",-1);
        GetUsersDrugs getlist=new GetUsersDrugs();
        getlist.execute("user_id",String.valueOf(user_id));

        drugListView=findViewById(R.id.drugList);
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

        ImageButton categories = findViewById(R.id.categoriesButton);
        categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCategoriesActivity();
            }
        });

        ImageButton addDrug = findViewById(R.id.addDrugButton);
        addDrug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent openAddDrug=new Intent(MainActivity.this,addDrug.class);
                startActivityForResult(openAddDrug,2);
                //openAddDrugActivity();
            }
        });
    }

    @Override
    public void onItemClickListener(Drug drug) {
        Intent openDrugModify=new Intent(MainActivity.this,modifyDrug.class);
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
        }
        //System.out.println(drugList);
    }

    private class GetUsersDrugs extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client =new OkHttpClient();
            RequestBody form=new FormBody.Builder()
                    .add(strings[0],strings[1])
                    //.add(strings[2],strings[3])
                    .build();
            Request request=new Request.Builder()
                    .url(drugListUrl)
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
                    JSONArray drugArr=object.getJSONArray("drugList");
                    drugList= new ArrayList<>();
                    for (int i = 0; i <drugArr.length(); i++) {
                        object=drugArr.getJSONObject(i);
                        drugList.add(new Drug(object.getInt("id"),object.getString("name"),String.valueOf(object.get("expiration_date")),object.getInt("quantity"),object.getInt("unit_id")));
                    }
                    adapter=new DrugListAdapter(MainActivity.this,drugList);
                    drugListView.setAdapter(adapter);
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

    public void openCategoriesActivity()
    {
        Intent intent = new Intent(this, categories.class);
        startActivity(intent);
    }

    public void openAddDrugActivity()
    {
        Intent intent = new Intent(this, addDrug.class);
        startActivity(intent);
    }

}
