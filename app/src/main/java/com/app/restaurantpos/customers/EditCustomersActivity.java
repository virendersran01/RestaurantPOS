package com.app.restaurantpos.customers;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.restaurantpos.R;
import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.utils.BaseActivity;

import es.dmoral.toasty.Toasty;

public class EditCustomersActivity extends BaseActivity {


    EditText etxtCustomerName, etxtAddress, etxtCustomerCell, etxtCustomerEmail;
    TextView txtEditCustomer, txtUpdateInformation;
    String getCustomerId, getCustomerName, getCustomerCell, getCustomerEmail, getCustomerAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customers);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.edit_customer);


        etxtCustomerName = findViewById(R.id.etxt_customer_name);
        etxtCustomerCell = findViewById(R.id.etxt_customer_cell);
        etxtCustomerEmail = findViewById(R.id.etxt_email);
        etxtAddress = findViewById(R.id.etxt_address);

        txtEditCustomer = findViewById(R.id.txt_edit_customer);
        txtUpdateInformation = findViewById(R.id.txt_update_customer);

        getCustomerId = getIntent().getExtras().getString("customer_id");
        getCustomerName = getIntent().getExtras().getString("customer_name");
        getCustomerCell = getIntent().getExtras().getString("customer_cell");
        getCustomerEmail = getIntent().getExtras().getString("customer_email");
        getCustomerAddress = getIntent().getExtras().getString("customer_address");


        etxtCustomerName.setText(getCustomerName);
        etxtCustomerCell.setText(getCustomerCell);
        etxtCustomerEmail.setText(getCustomerEmail);
        etxtAddress.setText(getCustomerAddress);


        etxtCustomerName.setEnabled(false);
        etxtCustomerCell.setEnabled(false);
        etxtCustomerEmail.setEnabled(false);
        etxtAddress.setEnabled(false);

        txtUpdateInformation.setVisibility(View.INVISIBLE);


        txtEditCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etxtCustomerName.setEnabled(true);
                etxtCustomerCell.setEnabled(true);
                etxtCustomerEmail.setEnabled(true);
                etxtAddress.setEnabled(true);

                etxtCustomerName.setTextColor(Color.RED);
                etxtCustomerCell.setTextColor(Color.RED);
                etxtCustomerEmail.setTextColor(Color.RED);
                etxtAddress.setTextColor(Color.RED);
                txtUpdateInformation.setVisibility(View.VISIBLE);

                txtEditCustomer.setVisibility(View.GONE);

            }
        });


        txtUpdateInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String customerName = etxtCustomerName.getText().toString().trim();
                String customerCell = etxtCustomerCell.getText().toString().trim();
                String customerEmail = etxtCustomerEmail.getText().toString().trim();
                String customerAddress = etxtAddress.getText().toString().trim();

                if (customerName.isEmpty()) {
                    etxtCustomerName.setError(getString(R.string.enter_customer_name));
                    etxtCustomerName.requestFocus();
                } else if (customerCell.isEmpty()) {
                    etxtCustomerCell.setError(getString(R.string.enter_customer_cell));
                    etxtCustomerCell.requestFocus();
                } else if (customerEmail.isEmpty() || !customerEmail.contains("@") || !customerEmail.contains(".")) {
                    etxtCustomerEmail.setError(getString(R.string.enter_valid_email));
                    etxtCustomerEmail.requestFocus();
                } else if (customerAddress.isEmpty()) {
                    etxtAddress.setError(getString(R.string.enter_customer_address));
                    etxtAddress.requestFocus();
                } else {

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(EditCustomersActivity.this);
                    databaseAccess.open();

                    boolean check = databaseAccess.updateCustomer(getCustomerId, customerName, customerCell, customerEmail, customerAddress);

                    if (check) {
                        Toasty.success(EditCustomersActivity.this, R.string.update_successfully, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditCustomersActivity.this, CustomersActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {

                        Toasty.error(EditCustomersActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

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
