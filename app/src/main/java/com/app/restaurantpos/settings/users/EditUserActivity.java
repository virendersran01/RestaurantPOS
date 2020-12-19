package com.app.restaurantpos.settings.users;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.restaurantpos.R;
import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.utils.BaseActivity;

import es.dmoral.toasty.Toasty;

public class EditUserActivity extends BaseActivity {


    EditText etxtUserName,etxtPhone,etxtPassword,etxtUserType;
    TextView txtEditUser,txtUpdateUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.edit_user);

        etxtUserName=findViewById(R.id.etxt_user_name);
        etxtPhone=findViewById(R.id.etxt_phone_number);
        etxtPassword=findViewById(R.id.etxt_user_password);
        etxtUserType=findViewById(R.id.etxt_user_type);

        txtEditUser=findViewById(R.id.txt_edit_user);
        txtUpdateUser=findViewById(R.id.txt_update_user);


        String id=getIntent().getExtras().getString("id");
        String userName=getIntent().getExtras().getString("user_name");
        String userPhone=getIntent().getExtras().getString("user_phone");
        String userPassword=getIntent().getExtras().getString("user_password");
        String userType=getIntent().getExtras().getString("user_type");

        etxtUserName.setText(userName);
        etxtPhone.setText(userPhone);
        etxtPassword.setText(userPassword);
        etxtUserType.setText(userType);

        etxtUserName.setEnabled(false);
        etxtPhone.setEnabled(false);
        etxtPassword.setEnabled(false);
        etxtUserType.setEnabled(false);

        txtUpdateUser.setVisibility(View.INVISIBLE);



        txtEditUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etxtUserName.setEnabled(true);
                etxtPhone.setEnabled(true);
                etxtPassword.setEnabled(true);

                txtUpdateUser.setVisibility(View.VISIBLE);
                etxtUserName.setTextColor(Color.RED);
                etxtPhone.setTextColor(Color.RED);
                etxtPassword.setTextColor(Color.RED);

                txtEditUser.setVisibility(View.GONE);

                if (!id.equals("1")) {

                    etxtUserType.setTextColor(Color.RED);
                    etxtUserType.setEnabled(true);

                }



            }
        });



        etxtUserType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String[] userType = {"Admin", "Manager","Waiter"};

                AlertDialog.Builder builder = new AlertDialog.Builder(EditUserActivity.this);
                builder.setTitle(R.string.select_user_type);

                builder.setCancelable(false);
                builder.setItems(userType, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        switch (position) {
                            case 0:
                                etxtUserType.setText(userType[position]);
                                break;

                            case 1:
                                etxtUserType.setText(userType[position]);
                                break;

                            case 2:
                                etxtUserType.setText(userType[position]);
                                break;

                            default:
                                Log.d("default","default");

                        }
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        dialog.dismiss();
                    }
                });


                AlertDialog userTypeDialog = builder.create();

                userTypeDialog.show();

            }
        });


        txtUpdateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userName = etxtUserName.getText().toString().trim();
                String userPhone = etxtPhone.getText().toString().trim();
                String userPassword = etxtPassword.getText().toString().trim();
                String userType = etxtUserType.getText().toString().toLowerCase().trim();

                if (userName.isEmpty()) {
                    etxtUserName.setError(getString(R.string.please_enter_user_name));
                    etxtUserName.requestFocus();
                }

                else if (userPhone.isEmpty())
                {
                    etxtPhone.setError(getString(R.string.please_enter_phone_number));
                    etxtPhone.requestFocus();
                }
                else if (userPassword.isEmpty())
                {
                    etxtPassword.setError(getString(R.string.please_enter_password));
                    etxtPassword.requestFocus();
                }

                else if (userType.isEmpty())
                {
                    etxtUserType.setError(getString(R.string.please_select_user_type));
                    etxtUserType.requestFocus();
                }

                else {

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(EditUserActivity.this);
                    databaseAccess.open();

                   boolean check = databaseAccess.updateUser(id,userName,userPhone,userPassword,userType);

                    if (check) {
                        Toasty.success(EditUserActivity.this, R.string.successfully_updated, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditUserActivity.this, UsersActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    } else {

                        Toasty.error(EditUserActivity.this, R.string.user_already_exist_with_this_phone_number, Toast.LENGTH_SHORT).show();

                    }
                }

            }
        });

    }


    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
