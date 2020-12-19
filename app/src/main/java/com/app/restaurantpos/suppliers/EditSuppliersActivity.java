package com.app.restaurantpos.suppliers;

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

public class EditSuppliersActivity extends BaseActivity {

    EditText etxtSuppliersName, etxtSuppliersContactPerson, etxtSuppliersAddress, etxtSuppliersCell, etxtSuppliersEmail;
    TextView txtEditSuppliers, txtUpdateSuppliers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_suppliers);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.edit_suppliers);


        etxtSuppliersName = findViewById(R.id.etxt_supplier_name);
        etxtSuppliersContactPerson = findViewById(R.id.etxt_supplier_contact_name);
        etxtSuppliersCell = findViewById(R.id.etxt_supplier_cell);
        etxtSuppliersEmail = findViewById(R.id.etxt_supplier_email);
        etxtSuppliersAddress = findViewById(R.id.etxt_supplier_address);

        txtUpdateSuppliers = findViewById(R.id.txt_update_suppliers);
        txtEditSuppliers = findViewById(R.id.txt_edit_suppliers);

        String getSuppliersId = getIntent().getExtras().getString("suppliers_id");
        String getSuppliersName = getIntent().getExtras().getString("suppliers_name");
        String getSuppliersContactPerson = getIntent().getExtras().getString("suppliers_contact_person");
        String getSuppliersCell = getIntent().getExtras().getString("suppliers_cell");
        String getSuppliersEmail = getIntent().getExtras().getString("suppliers_email");
        String getSuppliersAddress = getIntent().getExtras().getString("suppliers_address");


        etxtSuppliersName.setText(getSuppliersName);
        etxtSuppliersContactPerson.setText(getSuppliersContactPerson);
        etxtSuppliersCell.setText(getSuppliersCell);
        etxtSuppliersEmail.setText(getSuppliersEmail);
        etxtSuppliersAddress.setText(getSuppliersAddress);

        etxtSuppliersName.setEnabled(false);
        etxtSuppliersContactPerson.setEnabled(false);
        etxtSuppliersCell.setEnabled(false);
        etxtSuppliersEmail.setEnabled(false);
        etxtSuppliersAddress.setEnabled(false);


        txtUpdateSuppliers.setVisibility(View.INVISIBLE);

        txtEditSuppliers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                etxtSuppliersName.setEnabled(true);
                etxtSuppliersContactPerson.setEnabled(true);
                etxtSuppliersCell.setEnabled(true);
                etxtSuppliersEmail.setEnabled(true);
                etxtSuppliersAddress.setEnabled(true);


                etxtSuppliersName.setTextColor(Color.RED);
                etxtSuppliersContactPerson.setTextColor(Color.RED);
                etxtSuppliersCell.setTextColor(Color.RED);
                etxtSuppliersEmail.setTextColor(Color.RED);
                etxtSuppliersAddress.setTextColor(Color.RED);


                txtUpdateSuppliers.setVisibility(View.VISIBLE);
                txtEditSuppliers.setVisibility(View.GONE);

            }
        });


        txtUpdateSuppliers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String suppliersName = etxtSuppliersName.getText().toString().trim();
                String suppliersContactPerson = etxtSuppliersContactPerson.getText().toString().trim();
                String suppliersCell = etxtSuppliersCell.getText().toString().trim();
                String suppliersEmail = etxtSuppliersEmail.getText().toString().trim();
                String suppliersAddress = etxtSuppliersAddress.getText().toString().trim();


                if (suppliersName.isEmpty()) {
                    etxtSuppliersName.setError(getString(R.string.enter_suppliers_name));
                    etxtSuppliersName.requestFocus();
                } else if (suppliersContactPerson.isEmpty()) {
                    etxtSuppliersContactPerson.setError(getString(R.string.enter_suppliers_contact_person_name));
                    etxtSuppliersContactPerson.requestFocus();
                } else if (suppliersCell.isEmpty()) {
                    etxtSuppliersCell.setError(getString(R.string.enter_suppliers_cell));
                    etxtSuppliersCell.requestFocus();
                } else if (suppliersEmail.isEmpty() || !suppliersEmail.contains("@") || !suppliersEmail.contains(".")) {
                    etxtSuppliersEmail.setError(getString(R.string.enter_valid_email));
                    etxtSuppliersEmail.requestFocus();
                } else if (suppliersAddress.isEmpty()) {
                    etxtSuppliersAddress.setError(getString(R.string.enter_suppliers_address));
                    etxtSuppliersAddress.requestFocus();
                } else {

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(EditSuppliersActivity.this);
                    databaseAccess.open();

                    boolean check = databaseAccess.updateSuppliers(getSuppliersId, suppliersName, suppliersContactPerson, suppliersCell, suppliersEmail, suppliersAddress);

                    if (check) {
                        Toasty.success(EditSuppliersActivity.this, R.string.update_successfully, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditSuppliersActivity.this, SuppliersActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    } else {

                        Toasty.error(EditSuppliersActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

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
