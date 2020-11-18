package com.example.homeaidkit;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class editDrug extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    protected EditText editDrugName;
    protected Spinner editDrugForm;
    protected EditText editDrugQuantity;
    protected Spinner editDrugCategory;

    private int unitId;
    private boolean isNameOk=true,isQuantityOk=true;

    private  String postUrl;
    private  String categoriesUrl;

    TextView date;
    Button selectDate;
    Calendar calendar;
    DatePickerDialog dpd;
    private int chosenCategoryId=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_drug);
        postUrl=getString(R.string.host)+"editDrug.php";
        categoriesUrl=getString(R.string.host)+"getUsersCategories.php";

        final int user_id= getSharedPreferences("UserData",MODE_PRIVATE).getInt("user_id",-1);
        GetCategories request=new GetCategories();
        request.execute("user_id",String.valueOf(user_id));

        editDrugName=findViewById(R.id.drugNameInput);
        editDrugQuantity=findViewById(R.id.drugQuantityInput);
        editDrugCategory=findViewById(R.id.drugCategorySelector);
        final Bundle pack=getIntent().getExtras();
        date = findViewById(R.id.dateView);
        selectDate = findViewById(R.id.selectDateButton);
        //TextView drugName=findViewById(R.id.selectedDrug);
        if (pack!=null){
            //drugName.setText(pack.getString("name"));
            editDrugName.setText(pack.getString("name"));
            date.setText(pack.getString("expDate"));
            editDrugQuantity.setText(String.valueOf(pack.getInt("quantity")));
        }
        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                dpd = new DatePickerDialog(editDrug.this, R.style.DatePicker, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int mYear, int mMonth, int mDay) {
                        date.setText(mDay + "-" + (mMonth+1) + "-" + mYear);
                    }

                },year,month,day);
                dpd.show();
            }
        });

        // spinner drug form
        editDrugForm=findViewById(R.id.drugFormSelector);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this,
                R.array.form,
                R.layout.spinner_color
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        editDrugForm.setAdapter(adapter);
        editDrugForm.setOnItemSelectedListener(this);

        editDrugName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(editDrugName.getText().toString().isEmpty())
                {
                    setNameOk(false);
                }
                else setNameOk(true);
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(editDrugName.getText().toString().isEmpty())
                {
                    setNameOk(false);
                }
                else setNameOk(true);
            }
        });
        editDrugName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    if(!isNameOk)
                        editDrugName.setError(getString(R.string.empty_name_error));
                }
            }
        });

        editDrugQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(editDrugQuantity.getText().toString().isEmpty())
                {
                    setQuantityOk(false);
                }
                else setQuantityOk(true);
            }
        });
        editDrugQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    if(!isQuantityOk)
                        editDrugQuantity.setError(getString(R.string.empty_quantity_error));
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

        Button approveEditDrug = findViewById(R.id.approveEditDrugButton);
        approveEditDrug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(formReadyToRequest()) {
                    PostRequest editDrugRequest = new PostRequest();
                    editDrugRequest.execute("itemId",String.valueOf(pack.getInt("id")),
                            "drugName", editDrugName.getText().toString(),
                            "drugExpDate", date.getText().toString(),
                            "drugQuantity", editDrugQuantity.getText().toString(),
                            "unit_id",String.valueOf(unitId),
                            "category_id",String.valueOf(chosenCategoryId));
                }
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        setUnitId(++position);
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private class PostRequest extends AsyncTask<String,Void ,String >{

        @Override
        protected void onPreExecute() {
        }
        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject object=new JSONObject(s);
                if(object.has("success"))
                {
                    Intent backToMain =new Intent(editDrug.this,MainActivity.class);
                    Toast.makeText(editDrug.this,object.getString("message"),Toast.LENGTH_LONG).show();
                    startActivity(backToMain);
                    finish();
                }
            }
            catch (JSONException e) {
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
                    .add(strings[8],strings[9])
                    .add(strings[10],strings[11])
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
    private class GetCategories extends AsyncTask <String,Void,String>
    {
        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client =new OkHttpClient();
            RequestBody form=new FormBody.Builder()
                    .add(strings[0],strings[1])
                    .build();
            Request request=new Request.Builder()
                    .url(categoriesUrl)
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
                if(object.has("empty") && object.getInt("empty")==0)
                {
                    final ArrayList<Category> userCategories=new ArrayList<>();
                    JSONArray categories =object.getJSONArray("categories");
                    userCategories.add(new Category(0,"wybierz kategorię"));

                    for (int i = 0; i <categories.length() ; i++) {
                        userCategories.add(new Category(categories.getJSONObject(i).getInt("id"),
                                categories.getJSONObject(i).getString("category_name")));
                    }

                    ArrayAdapter <Category>categoriesAdapter=new ArrayAdapter<>(editDrug.this,R.layout.spinner_color,userCategories);
                    categoriesAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
                    editDrugCategory.setAdapter(categoriesAdapter);
                    editDrugCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            chosenCategoryId=userCategories.get(position).getId();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) { chosenCategoryId=0;}
                    });
                }
                else{}
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }
    private boolean formReadyToRequest() { return isNameOk() && isQuantityOk();}

    public boolean isNameOk() {
        return isNameOk;
    }
    public void setNameOk(boolean NameOk) {
        isNameOk = NameOk;
    }

    public boolean isQuantityOk() {
        return isQuantityOk;
    }
    public void setQuantityOk(boolean QuantityOk) {
        isQuantityOk = QuantityOk;
    }

    @Override
    public void onBackPressed() {}
}