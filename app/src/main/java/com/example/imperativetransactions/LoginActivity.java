package com.example.imperativetransactions;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.google.gson.JsonObject;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    static EditText et_loginId;
    ProgressDialog dialog;
    static EditText et_Password;
    boolean status = false, passwordVisible;
    Button but_Login;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        update();
        but_Login.setEnabled(true);
        String logout = isnull(getIntent().getStringExtra("LogOut"));
        if (logout.equals("LogOut")) {
            et_loginId.setText("");
            et_Password.setText("");
        }
    }

    public void update() {
        but_Login = findViewById(R.id.but_logIn);
        et_loginId = findViewById(R.id.editTextloginId);
        et_Password = findViewById(R.id.editTextPassword);
        et_Password.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });
        but_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable(LoginActivity.this)) {
                    if (et_loginId.getText().toString().trim().length() > 0 && et_Password.getText().toString().trim().length() > 0) {
                        but_Login.setEnabled(false);
                        new LoginRequest().execute(et_loginId.getText().toString().trim(), et_Password.getText().toString().trim());
                    } else {
                        Toast.makeText(LoginActivity.this, "please fill the details", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "please verify internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        et_loginId.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });
        et_loginId.setLongClickable(false);
        et_loginId.setTextIsSelectable(false);
        et_Password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int right = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= et_Password.getRight() - et_Password.getCompoundDrawables()[right].getBounds().width()) {
                        int selection = et_Password.getSelectionEnd();
                        if (passwordVisible) {
                            et_Password.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.eye_off, 0);
                            et_Password.setTransformationMethod(new PasswordTransformationMethod());
                            passwordVisible = false;
                        } else {
                            et_Password.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.eye_on, 0);
                            et_Password.setTransformationMethod(null);
                            passwordVisible = true;
                        }
                        et_Password.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.but_logIn:
//                if (isNetworkAvailable(this)) {
//                    if (et_loginId.getText().toString().trim().length() > 0 && et_Password.getText().toString().trim().length() > 0) {
//                        new LoginRequest().execute(et_loginId.getText().toString().trim(), et_Password.getText().toString().trim());
//                    } else {
//                        Toast.makeText(this, "please fill the details", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(this, "please verify internet connection", Toast.LENGTH_SHORT).show();
//                }
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager objConnectivityManager;
        boolean isNetworkAvailable = false;
        try {
            objConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (objConnectivityManager.getActiveNetworkInfo() != null && objConnectivityManager.getActiveNetworkInfo().isAvailable() && objConnectivityManager.getActiveNetworkInfo().isConnected()) {
                return isNetworkAvailable = true;
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            objConnectivityManager = null;
        }
        return isNetworkAvailable;
    }

    private class LoginRequest extends AsyncTask<String, String, HttpResultDO> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(LoginActivity.this, "", "Processing, Please wait...");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected HttpResultDO doInBackground(String... strings) {
            HttpResultDO httpResult = null;
            try {
                JsonObject loginJson = new JsonObject();
                loginJson.addProperty("username", et_loginId.getText().toString());
                loginJson.addProperty("password", et_Password.getText().toString());
                httpResult = WebServiceHelper.callWebService(WebServiceHelper.RestMethodType.POST, "https://api.prepstripe.com/login", loginJson);
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
                        JSONObject obj = new JSONObject(httpResultDO.getResponseContent());
                        response = isnull(obj.getString("success"));
                        responseMsg = isnull(obj.getString("message"));
                        token = isnull(obj.getString("token"));
                        if (response.toUpperCase().equals("TRUE")) {
                            if (responseMsg.toUpperCase().equals("LOGIN SUCCESSFUL")) {
                                et_loginId.setText("");
                                et_Password.setText("");
                                savePreferenceDataValues("LoginTokenKey", isnull(token), LoginActivity.this);
                                Toast.makeText(LoginActivity.this, "Response:- " + responseMsg, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent();
                                intent.setClass(LoginActivity.this, TransactionActivity.class);
//                                intent.setClass(LoginActivity.this, BiometricActivity.class);
                                startActivity(intent);
                                but_Login.setEnabled(true);
                            } else {
                                but_Login.setEnabled(true);
                                Toast.makeText(LoginActivity.this, "Response:- " + responseMsg, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            but_Login.setEnabled(true);
                            Toast.makeText(LoginActivity.this, "Response:-" + response, Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    but_Login.setEnabled(true);
                    Toast.makeText(LoginActivity.this, httpResultDO.getErrorMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                but_Login.setEnabled(true);
            }
        }
    }

    public static void savePreferenceDataValues(String key, String value, Context context) {
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String masterKey = null;
        try {
            masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            SharedPreferences sharedPreferences1 = EncryptedSharedPreferences.create("secure_token", masterKey,
                    context, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            SharedPreferences.Editor editor = sharedPreferences1.edit();
            editor.putString(key, value);
            editor.commit();
        }catch (Exception e){

        }
    }

    public static String isnull(String value) {
        String s = "";
        if (value == null) {
            return s;
        }
        return value;
    }

    public static String getStringPreferences(Context context, String strKey, String defaultVal) {
        String result = defaultVal;
        String masterKey = null;

        try {
            masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create("secure_token", masterKey,
                    context, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            if (sharedPreferences.contains(strKey)) {
                result = sharedPreferences.getString(strKey, defaultVal);
            } else {
                sharedPreferences.edit().putString(strKey, defaultVal).commit();
            }
        } catch (Exception e) {
        }
        return result;
    }

    public void onBackPressed() {
        try {
            super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.getMessage();
        }
    }
}
