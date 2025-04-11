package com.example.imperativetransactions;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TransactionActivity extends AppCompatActivity implements View.OnClickListener {
    ProgressDialog dialog;
    View view;
    TextView tv_id, tv_date, tv_amt, tv_category, tv_description;
    ArrayList<TransactionsDO> transDOArrayList = new ArrayList<>();
    ArrayList<TransactionsDO> searchNameList = new ArrayList<>();
    LinearLayout ll_TransView_list;
    AlertDialog dialog1;
    EditText et_Category;
    Button btn_logout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        ll_TransView_list = findViewById(R.id.ll_TransDetails_list);
        et_Category = findViewById(R.id.et_Category);
        btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        new GetTransaction().execute();
        et_Category.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchData();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void searchData() {
        try {
            searchNameList.clear();
            String str_search = isnull(et_Category.getText().toString()).toUpperCase();
            for (TransactionsDO transactionsDO : transDOArrayList) {
                if (transactionsDO.getCategory().toUpperCase().contains(str_search)) {
                    searchNameList.add(transactionsDO);
                }
            }
            populateSearchedRow(searchNameList);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void populateSearchedRow(ArrayList<TransactionsDO> searchNameList) {
        try {
            ll_TransView_list.removeAllViews();
            for (int i = 0; i < searchNameList.size(); i++) {
                view = getLayoutInflater().inflate(R.layout.transcsview, null);
                tv_id = view.findViewById(R.id.tv_Id);
                tv_date = view.findViewById(R.id.tv_Date);
                tv_amt = view.findViewById(R.id.tv_Amount);
                tv_category = view.findViewById(R.id.tv_Category);
                tv_description = view.findViewById(R.id.tv_Description);
                tv_id.setText(String.valueOf(searchNameList.get(i).getId()));
                tv_date.setText(searchNameList.get(i).getDate());
                tv_amt.setText(String.valueOf(searchNameList.get(i).getAmount()));
                tv_category.setText(String.valueOf(searchNameList.get(i).getCategory()));
                tv_description.setText(String.valueOf(searchNameList.get(i).getDescription()));
                ll_TransView_list.addView(view);
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
//                case R.id.btn_logout:
//
//                    break;
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private class GetTransaction extends AsyncTask<String, String, HttpResultDO> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(TransactionActivity.this, "", "Processing, Please wait...");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected HttpResultDO doInBackground(String... strings) {
            HttpResultDO httpResult = null;
            try {
                JsonObject loginJson = new JsonObject();
                String publisherTokenId = LoginActivity.getStringPreferences(TransactionActivity.this, "LoginTokenKey", "");
                loginJson.addProperty("TokennId", publisherTokenId);
                loginJson.addProperty("Screen", "Prices");
                loginJson.addProperty("Param", "");
                httpResult = WebServiceHelper.callWebService(WebServiceHelper.RestMethodType.GET, "https://api.prepstripe.com/v1/transactions", loginJson);
            } catch (Exception e) {
                httpResult = new HttpResultDO();
                httpResult.setErrorMessage("Error: " + e.getMessage());
                httpResult.setResult(WebServiceHelper.ServiceCallStatus.Failed);
                e.getMessage();
            }
            return httpResult;
        }

        @Override
        protected void onPostExecute(HttpResultDO httpResultDO) {
            super.onPostExecute(httpResultDO);
            dialog.cancel();
            String response = "", responseMsg = "", token = "";
            try {
                if (httpResultDO.getResult() == WebServiceHelper.ServiceCallStatus.Success) {
                    if (httpResultDO.getResponseContent() != null && !httpResultDO.getResponseContent().equalsIgnoreCase("null")) {
                        String responseContent = httpResultDO.getResponseContent();
                        JSONArray jsonArray = new JSONArray(responseContent);
                        if (jsonArray.length() > 0) {
                            transDOArrayList.clear();
                            TransactionsDO transDo;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                transDo = new TransactionsDO();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                transDo.setId(jsonObject.getInt("id"));
                                transDo.setAmount(jsonObject.getInt("amount"));
                                transDo.setDate(jsonObject.getString("date"));
                                transDo.setCategory(jsonObject.getString("category"));
                                transDo.setDescription(jsonObject.getString("description"));
                                transDOArrayList.add(transDo);
                            }
                            if (transDOArrayList.size() > 0) {
                                loadTransList(transDOArrayList);
                            }
                        } else {
                            Toast.makeText(TransactionActivity.this, "No data found", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(TransactionActivity.this, httpResultDO.getErrorMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }

    private void loadTransList(ArrayList<TransactionsDO> transDOArrayList) {
        try {
            ll_TransView_list.removeAllViews();
            for (int i = 0; i < transDOArrayList.size(); i++) {
                view = getLayoutInflater().inflate(R.layout.transcsview, null);
                tv_id = view.findViewById(R.id.tv_Id);
                tv_date = view.findViewById(R.id.tv_Date);
                tv_amt = view.findViewById(R.id.tv_Amount);
                tv_category = view.findViewById(R.id.tv_Category);
                tv_description = view.findViewById(R.id.tv_Description);
                tv_id.setText(String.valueOf(transDOArrayList.get(i).getId()));
                tv_date.setText(transDOArrayList.get(i).getDate());
                tv_amt.setText(String.valueOf(transDOArrayList.get(i).getAmount()));
                tv_category.setText(String.valueOf(transDOArrayList.get(i).getCategory()));
                tv_description.setText(String.valueOf(transDOArrayList.get(i).getDescription()));
                ll_TransView_list.addView(view);
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public static String isnull(String value) {
        String s = "";
        if (value == null) {
            return s;
        }
        return value;
    }

    @Override
    public void onBackPressed() {
        logout();
    }

    private void logout() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.logout, null);
            Button bt_Okay = (Button) view.findViewById(R.id.but_okay);
            Button bt_okayCancel = (Button) view.findViewById(R.id.but_okaycancel);
            builder.setView(view);
            bt_Okay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearToken();
                    Intent intent = new Intent();
                    intent.setClass(TransactionActivity.this, LoginActivity.class);
                    intent.putExtra("LogOut", "LogOut");
                    startActivity(intent);
                    finish();
                }
            });
            bt_okayCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog1.dismiss();
                }
            });
            builder.setCancelable(false);
            dialog1 = builder.create();
            dialog1.show();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void clearToken() {
        String masterKey=null;
        try {
            masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create("secure_token", masterKey,
                    TransactionActivity.this, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("LoginTokenKey");
            editor.apply();
        } catch (Exception e) {
            e.getMessage();
        }
    }

}
