package com.example.homeaidkit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class addDrug extends AppCompatActivity {
    protected EditText drugName;
    protected EditText drugExpDate;
    protected Spinner drugForm;
    protected EditText drugQuantity;
    protected Spinner drugCategory;

    private boolean isNameOk=false,isExpDateOk=false,isFormOk=false,isQuantityOk=false;

    private static final String postUrl="http://192.168.0.3/HomeAidKit/addDrug.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drug);

        drugName=findViewById(R.id.drugNameInput);
        drugExpDate=findViewById(R.id.drugDateInput);
        drugForm=findViewById(R.id.drugFormSelector);
        drugQuantity=findViewById(R.id.drugQuantityInput);
        drugCategory=findViewById(R.id.drugCategorySelector);

        drugName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(drugName.getText().toString().isEmpty())
                {
                    setNameOk(false);
                }
                else setNameOk(true);
            }
        });
        drugName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    if(!isNameOk)
                        drugName.setError(getString(R.string.empty_name_error));
                }
            }
        });

        drugExpDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(drugExpDate.getText().toString().isEmpty())
                {
                    setExpDateOk(false);
                }
                else setExpDateOk(true);
            }
        });
        drugExpDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    if(!isExpDateOk)
                        drugExpDate.setError(getString(R.string.empty_expDate_error));
                }
            }
        });

        drugQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(drugQuantity.getText().toString().isEmpty())
                {
                    setQuantityOk(false);
                }
                else setQuantityOk(true);
            }
        });
        drugQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    if(!isQuantityOk)
                        drugQuantity.setError(getString(R.string.empty_quantity_error));
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

        Button addDrug = findViewById(R.id.addingDrugButton);
        addDrug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(formReadyToRequest()) {
                    PostRequest addDrugRequest = new PostRequest();
                    addDrugRequest.execute("drugName", drugName.getText().toString(), "drugExpDate", drugExpDate.getText().toString(), "drugQuantity", drugQuantity.getText().toString());
                }
                //openAddDrugActivity();
            }
        });
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
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private class PostRequest extends AsyncTask<String,Void ,String >{

        @Override
        protected void onPreExecute() {
        }
        @Override
        protected void onPostExecute(String s) {
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

    private boolean formReadyToRequest() { return isNameOk() && isExpDateOk() && isQuantityOk();}

    public boolean isNameOk() {
        return isNameOk;
    }
    public void setNameOk(boolean NameOk) {
        isNameOk = NameOk;
    }

    public boolean isExpDateOk() {
        return isExpDateOk;
    }
    public void setExpDateOk(boolean expDateOk) {
        isExpDateOk = expDateOk;
    }

    public boolean isQuantityOk() {
        return isQuantityOk;
    }
    public void setQuantityOk(boolean NameOk) {
        isQuantityOk = NameOk;
    }
}