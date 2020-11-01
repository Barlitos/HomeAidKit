package com.example.homeaidkit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
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

public class modifyDrug extends AppCompatActivity {

    private EditText modifyDrugQuantity;
    private ImageButton addButton;
    private ImageButton minusButton;
    private int counter;
    private boolean isModifyDrugQuantityOk=false;

    private String postUrl;

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

        postUrl=getString(R.string.host)+"modifyDrug.php";

        SharedPreferences data=getSharedPreferences("UserData",MODE_PRIVATE);
        final int user_id=data.getInt("user_id",-1);

        modifyDrugQuantity = (EditText) findViewById(R.id.quantityOfDrug);
        addButton = (ImageButton) findViewById(R.id.addQuantityButton);
        addButton.setOnClickListener(clickListener);
        minusButton = (ImageButton) findViewById(R.id.substractQuantityButton);
        minusButton.setOnClickListener(clickListener);
        initQuantityCounter();

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
                    drugQuantity.execute("drugQuantity", modifyDrugQuantity.getText().toString(),"user_id",String.valueOf(user_id));
                }
                else
                {
                    Toast.makeText(modifyDrug.this, "Niepoprawnie wypełniono pole",Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button editDrug = findViewById(R.id.editDrugButton);
        editDrug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditDrugActivity();
            }
        });

        Button deleteDrug = findViewById(R.id.DeleteDrugButton);
        deleteDrug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeleteDrugActivity();
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
                    Toast.makeText(modifyDrug.this,response.getString("message"),Toast.LENGTH_LONG).show();
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

    private void initQuantityCounter()
    {
        counter = 0;
        modifyDrugQuantity.setText(counter + "");
    }

    private void plusQuantityCounter()
    {
        counter++;
        modifyDrugQuantity.setText(counter + "");
    }

    private void minusQuantityCounter()
    {
        counter--;
        modifyDrugQuantity.setText(counter + "");
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

    public void openMainActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openEditDrugActivity()
    {
        Intent intent = new Intent(this, editDrug.class);
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
}