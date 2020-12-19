package com.app.restaurantpos.settings.payment_method;

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

public class EditPaymentMethodActivity extends BaseActivity {


    EditText etxtPaymentMethodName;
    TextView txtUpdatePaymentMethod, txtEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_payment_method);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.update_payment_method);

        txtEdit = findViewById(R.id.txt_edit);
        txtUpdatePaymentMethod = findViewById(R.id.txt_update_payment_method);
        etxtPaymentMethodName = findViewById(R.id.etxt_payment_method_name);

        String paymentMethodId = getIntent().getExtras().getString("payment_method_id");
        String paymentMethodName = getIntent().getExtras().getString("payment_method_name");


        etxtPaymentMethodName.setText(paymentMethodName);
        etxtPaymentMethodName.setEnabled(false);
        txtUpdatePaymentMethod.setVisibility(View.INVISIBLE);


        txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etxtPaymentMethodName.setEnabled(true);
                txtUpdatePaymentMethod.setVisibility(View.VISIBLE);
                etxtPaymentMethodName.setTextColor(Color.RED);

                txtEdit.setVisibility(View.GONE);

            }
        });


        txtUpdatePaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String paymentMethodName = etxtPaymentMethodName.getText().toString().trim();

                if (paymentMethodName.isEmpty()) {
                    etxtPaymentMethodName.setError(getString(R.string.payment_method_name));
                    etxtPaymentMethodName.requestFocus();
                } else {

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(EditPaymentMethodActivity.this);
                    databaseAccess.open();

                    boolean check = databaseAccess.updatePaymentMethod(paymentMethodId, paymentMethodName);

                    if (check) {
                        Toasty.success(EditPaymentMethodActivity.this, R.string.successfully_updated, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditPaymentMethodActivity.this, PaymentMethodActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    } else {

                        Toasty.error(EditPaymentMethodActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

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
