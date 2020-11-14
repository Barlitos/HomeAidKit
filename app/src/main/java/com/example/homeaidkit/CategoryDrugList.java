package com.example.homeaidkit;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
        System.out.println(userId+" "+category_id);
        getCategoryDrugList getlist=new getCategoryDrugList();
        getlist.execute("userId",String.valueOf(userId),"categoryId",String.valueOf(category_id));


    }

    @Override
    public void onItemClickListener(Drug drug) {
        System.out.println("TAK");
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
