package com.example.homeaidkit;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

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

public class mostUsed extends AppCompatActivity implements DrugListAdapter.OnItemClickListener{
    private List<Drug> drugList;
    private ListView drugListView;
    private DrugListAdapter adapter;
    private String getMostUsedUrl;
    private boolean reverseOrder=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_most_used);
        getMostUsedUrl=getString(R.string.host)+"/getMostUsed.php";
        final int user_id= getSharedPreferences("UserData",MODE_PRIVATE).getInt("user_id",-1);
        GetMostUsed getRequest=new GetMostUsed();
        getRequest.execute("user_id",String.valueOf(user_id));
        drugListView=findViewById(R.id.categoryList);

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
    }

    @Override
    public void onItemClickListener(Drug drug) {
        Intent openDrugModify=new Intent(mostUsed.this,modifyDrug.class);
        openDrugModify.putExtra("name",drug.getName())
                .putExtra("quantity",drug.getQuantity())
                .putExtra("id",drug.getId())
                .putExtra("expDate",drug.getExpDate())
                .putExtra("index",drugList.indexOf(drug))
                .putExtra("unit",drug.getUnit());
        startActivityForResult(openDrugModify,1);
    }

    private class GetMostUsed extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {

            OkHttpClient client =new OkHttpClient();
            RequestBody form=new FormBody.Builder()
                    .add(strings[0],strings[1])
                    //.add(strings[2],strings[3])
                    .build();
            Request request=new Request.Builder()
                    .url(getMostUsedUrl)
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
                    adapter=new DrugListAdapter(mostUsed.this,drugList);
                    drugListView.setAdapter(adapter);
                }
                else{
                    Toast.makeText(mostUsed.this,"Lista często użwanych jest pusta",Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void alphabeticalSort(View view) {
        if(isReverseOrder()) {
            Collections.sort(drugList,Collections.<Drug>reverseOrder());
            setReverseOrder(!isReverseOrder());
        }
        else {
            Collections.sort(drugList);
            setReverseOrder(!isReverseOrder());
        }
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("SimpleDateFormat")
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

    public boolean isReverseOrder() {
        return reverseOrder;
    }

    public void setReverseOrder(boolean reverseOrder) {
        this.reverseOrder = reverseOrder;
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

    public void openHomeActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {}
}