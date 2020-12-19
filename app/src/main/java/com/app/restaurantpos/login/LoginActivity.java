package com.app.restaurantpos.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.restaurantpos.Constant;
import com.app.restaurantpos.HomeActivity;
import com.app.restaurantpos.R;
import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.utils.BaseActivity;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends BaseActivity {

    EditText etxtPhone, etxtPassword;
    TextView txtLogin;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        getSupportActionBar().hide();

        etxtPhone = findViewById(R.id.etxt_phone);
        etxtPassword = findViewById(R.id.etxt_password);
        txtLogin = findViewById(R.id.txt_login);

        sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        String phone = sp.getString(Constant.SP_PHONE, "");
        String password = sp.getString(Constant.SP_PASSWORD, "");


        etxtPhone.setText(phone);
        etxtPassword.setText(password);

        if (phone.length() >= 3 && password.length() >= 3) {
            login(phone, password);
        }


        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = etxtPhone.getText().toString().trim();
                String password = etxtPassword.getText().toString().trim();

                if (phone.isEmpty()) {
                    etxtPhone.setError(getString(R.string.please_enter_phone_number));
                    etxtPhone.requestFocus();
                } else if (password.isEmpty()) {
                    etxtPassword.setError(getString(R.string.please_enter_password));
                    etxtPassword.requestFocus();
                } else {
                    login(phone, password);
                }
            }
        });


    }

    private void login(String phone, String password) {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(LoginActivity.this);
        databaseAccess.open();

        //get data from local database
        List<HashMap<String, String>> userData;
        userData = databaseAccess.checkUser(phone, password);
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);

        if (userData.isEmpty()) {
            Toasty.error(this, R.string.invalid_information, Toast.LENGTH_SHORT).show();
        } else {

            String userName = userData.get(0).get("user_name");
            String userType = userData.get(0).get("user_type");
            Log.d("TAG", "login: "+userName);
            Log.d("TAG", "login: "+userType);

            //Creating editor to store values to shared preferences
            SharedPreferences.Editor editor = sp.edit();
            //Adding values to editor

            editor.putString(Constant.SP_PHONE, phone);
            editor.putString(Constant.SP_PASSWORD, password);

            editor.putString(Constant.SP_USER_NAME, userName);
            editor.putString(Constant.SP_USER_TYPE, userType);

            editor.apply();

            Intent intent1 = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent1);

            Toasty.success(this, R.string.login_successful, Toast.LENGTH_SHORT).show();


        }


    }
}
