package com.app.restaurantpos.settings.users;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

public class AddUserActivity extends BaseActivity {


    EditText etxtUserName,etxtPhone,etxtPassword,etxtUserType;
    TextView txtAddUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.add_user);

        etxtUserName=findViewById(R.id.etxt_user_name);
        etxtPhone=findViewById(R.id.etxt_phone_number);
        etxtPassword=findViewById(R.id.etxt_user_password);
        etxtUserType=findViewById(R.id.etxt_user_type);

        txtAddUser=findViewById(R.id.txt_add_user);

        etxtUserType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String[] userType = {"Admin", "Manager","Waiter"};

                AlertDialog.Builder builder = new AlertDialog.Builder(AddUserActivity.this);
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

        txtAddUser.setOnClickListener(new View.OnClickListener() {
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

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddUserActivity.this);
                    databaseAccess.open();

                    boolean check = databaseAccess.addUser(userName,userPhone,userPassword,userType);

                    if (check) {
                        Toasty.success(AddUserActivity.this, R.string.successfully_added, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddUserActivity.this, UsersActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    } else {

                        Toasty.error(AddUserActivity.this, R.string.user_already_exist_with_this_phone_number, Toast.LENGTH_SHORT).show();

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
