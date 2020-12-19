package com.app.restaurantpos.settings.payment_method;

import android.content.Intent;
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

public class AddPaymentMethodActivity extends BaseActivity {


    EditText etxtPaymentMethod;
    TextView txtAddPaymentMethod;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment_method);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.add_payment_method);

        etxtPaymentMethod = findViewById(R.id.etxt_payment_method_name);
        txtAddPaymentMethod = findViewById(R.id.txt_add_payment_method);


        txtAddPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String paymentMethodName = etxtPaymentMethod.getText().toString().trim();

                if (paymentMethodName.isEmpty()) {
                    etxtPaymentMethod.setError(getString(R.string.enter_payment_method_name));
                    etxtPaymentMethod.requestFocus();
                } else {

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddPaymentMethodActivity.this);
                    databaseAccess.open();

                    boolean check = databaseAccess.addPaymentMethod(paymentMethodName);

                    if (check) {
                        Toasty.success(AddPaymentMethodActivity.this, R.string.successfully_added, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddPaymentMethodActivity.this, PaymentMethodActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    } else {

                        Toasty.error(AddPaymentMethodActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

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
