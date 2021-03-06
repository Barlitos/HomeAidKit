package com.example.homeaidkit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AlertDialogLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
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

public class modifyDrug extends AppCompatActivity {

    private EditText modifyDrugQuantity;
    private ImageButton addButton;
    private ImageButton minusButton;
    private TextView drugName;
    private Switch mostUsed;
    private int counter;
    int index;
    private boolean isModifyDrugQuantityOk=true,addToMostUsed=false;
    private Drug modifiedDrug;
    private String postUrl;
    private String deleteUrl;
    AlertDialog.Builder builder;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.substractQuantityButton :
                    minusQuantityCounter();
                    break;
                case R.id.addQuantityButton:
                    plusQuantityCounter();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_drug);
        Bundle pack= getIntent().getExtras();

        postUrl=getString(R.string.host)+"modifyDrug.php";
        deleteUrl=getString(R.string.host)+"deleteDrug.php";

        modifyDrugQuantity = findViewById(R.id.quantityOfDrug);
        addButton = findViewById(R.id.addQuantityButton);
        addButton.setOnClickListener(clickListener);
        minusButton = findViewById(R.id.substractQuantityButton);
        minusButton.setOnClickListener(clickListener);
        drugName=findViewById(R.id.sselectedDrugName);
        mostUsed=findViewById(R.id.mostUsedSwitch);
        TextView expdate=findViewById(R.id.sselectedDrugDate);
        TextView quantity=findViewById(R.id.sselectedDrugQuantity);

        if(pack!=null){
            modifiedDrug=new Drug(pack.getInt("id"),pack.getString("name"),pack.getString("expDate"),pack.getInt("quantity"),pack.getInt("unit"));
            initQuantityCounter(modifiedDrug.getQuantity());
            drugName.setText(modifiedDrug.getName());
            expdate.setText(modifiedDrug.getExpDate());
            if(modifiedDrug.getUnit()==1) {
                quantity.setText(String.valueOf(modifiedDrug.getQuantity())+" szt");
            }
            else{
                quantity.setText(String.valueOf(modifiedDrug.getQuantity())+" ml");
            }
            index=pack.getInt("index");
        }

        final int itemId=modifiedDrug.getId();

        mostUsed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    addToMostUsed=true;
                }
                else {
                    addToMostUsed=false;
                }
            }
        });
        SharedPreferences data=getSharedPreferences("UserData",MODE_PRIVATE);
        final int user_id=data.getInt("user_id",-1);

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

        modifyDrugQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(modifyDrugQuantity.getText().toString().isEmpty())
                {
                    setModifyDrugQuantityOk(false);
                }
                else setModifyDrugQuantityOk(true);
            }
        });
        modifyDrugQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    if(!isModifyDrugQuantityOk)
                        modifyDrugQuantity.setError(getString(R.string.empty_name_error));
                }
            }
        });

        Button acceptDrugModify = findViewById(R.id.acceptDrugModify);
        acceptDrugModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formReadyToRequest()) {
                    PostRequest drugQuantity = new PostRequest();
                    drugQuantity.execute("item_id",String.valueOf(itemId),
                            "drugQuantity", modifyDrugQuantity.getText().toString(),
                            "user_id",String.valueOf(user_id),
                            "mostUsed",String.valueOf(addToMostUsed));
                }
                else {
                    Toast.makeText(modifyDrug.this, "Niepoprawnie wypełniono pole", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button editDrug = findViewById(R.id.editDrugButton);
        editDrug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(modifyDrug.this, editDrug.class);
                intent.putExtra("name",modifiedDrug.getName())
                        .putExtra("quantity",modifiedDrug.getQuantity())
                        .putExtra("id",modifiedDrug.getId())
                        .putExtra("expDate",modifiedDrug.getExpDate())
                        .putExtra("index",index)
                        .putExtra("unit",modifiedDrug.getUnit());
                startActivity(intent);
            }
        });

        builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        Button deleteDrug = findViewById(R.id.DeleteDrugButton);
        deleteDrug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                builder.setMessage("Czy na pewno chcesz usunąć lek?")
                        .setCancelable(false)
                        .setPositiveButton("Tak", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                DeleteRequest drugDelete = new DeleteRequest();
                                drugDelete.execute("item_id",String.valueOf(itemId));
                            }
                        })
                        .setNegativeButton("Nie", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                openHomeActivity();
                                finish();
                                dialog.cancel();
                                Toast.makeText(getApplicationContext(),"Nie usunięto leku",Toast.LENGTH_SHORT).show();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("Usuwanie leku");
                alert.show();
            }
        });

        ImageButton mostUsed = findViewById(R.id.mostUsedButton);
        mostUsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMostUsedActivity();
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

        ImageButton addDrug = findViewById(R.id.addDrugButton);
        addDrug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddDrugActivity();
            }
        });

    }

    private class DeleteRequest extends AsyncTask<String,Void ,String > {

        @Override
        protected void onPreExecute() {
        }
        @Override
        protected void onPostExecute(String s) {
            try
            {
                JSONObject response = new JSONObject(s);
                if(response.has("success") && response.getInt("success")==1)
                {
                    Toast.makeText(modifyDrug.this,response.getString("message"),Toast.LENGTH_LONG).show();
                    openHomeActivity();
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
                    .build();
            Request request=new Request.Builder()
                    .url(deleteUrl)
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

    private class PostRequest extends AsyncTask<String,Void ,String > {

        @Override
        protected void onPreExecute() {
        }
        @Override
        protected void onPostExecute(String s) {
            System.out.println(s);
            try
            {
                JSONObject response = new JSONObject(s);
                if(response.has("success"))
                {
                    //System.out.println(" PARENT ACTIVITY : "+getParent());
                    Toast.makeText(modifyDrug.this,response.getString("message"),
                            Toast.LENGTH_LONG).show();
                    Intent backtoMain=new Intent(modifyDrug.this,MainActivity.class);
                    backtoMain.putExtra("quantity",
                            Integer.parseInt(modifyDrugQuantity.getText().toString()))
                            .putExtra("index",index);
                    setResult(RESULT_OK,backtoMain);
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
                    .add(strings[6],strings[7])
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

    private void initQuantityCounter(int quantity)
    {
        counter=quantity;
        modifyDrugQuantity.setText(String.valueOf(counter));
    }

    private void plusQuantityCounter()
    {
        counter++;
        modifyDrugQuantity.setText(String.valueOf(counter));
    }

    private void minusQuantityCounter()
    {
        counter--;
        modifyDrugQuantity.setText(String.valueOf(counter));
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

    public void openDeleteDrugActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openMostUsedActivity()
    {
        Intent intent = new Intent(this, mostUsed.class);
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

    public void openAddDrugActivity()
    {
        Intent intent = new Intent(this, addDrug.class);
        startActivity(intent);
    }

    private boolean formReadyToRequest() { return isModifyDrugQuantityOk();}

    public boolean isModifyDrugQuantityOk() {
        return isModifyDrugQuantityOk;
    }
    public void setModifyDrugQuantityOk(boolean modifyDrugQuantityOk) {
        isModifyDrugQuantityOk = modifyDrugQuantityOk;
    }

    @Override
    public void onBackPressed() {}
}