package com.app.restaurantpos.customers;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ajts.androidmads.library.ExcelToSQLite;
import com.app.restaurantpos.HomeActivity;
import com.app.restaurantpos.R;
import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.database.DatabaseOpenHelper;
import com.app.restaurantpos.utils.BaseActivity;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;

import es.dmoral.toasty.Toasty;

public class AddCustomersActivity extends BaseActivity {


    ProgressDialog loading;
    EditText etxtCustomerName, etxtAddress, etxtCustomerCell, etxtCustomerEmail;
    TextView txtAddCustomer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customers);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.add_customer);


        etxtCustomerName = findViewById(R.id.etxt_customer_name);
        etxtCustomerCell = findViewById(R.id.etxt_customer_cell);
        etxtCustomerEmail = findViewById(R.id.etxt_email);
        etxtAddress = findViewById(R.id.etxt_address);

        txtAddCustomer = findViewById(R.id.txt_add_customer);


        txtAddCustomer.setOnClickListener(new View.OnClickListener() {
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

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddCustomersActivity.this);
                    databaseAccess.open();

                    boolean check = databaseAccess.addCustomer(customerName, customerCell, customerEmail, customerAddress);

                    if (check) {
                        Toasty.success(AddCustomersActivity.this, R.string.customer_successfully_added, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddCustomersActivity.this, CustomersActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    } else {

                        Toasty.error(AddCustomersActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

                    }
                }


            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_product_menu, menu);
        return true;
    }


    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.menu_import:


                fileChooser();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //import data from Excel xls file
    public void onImport(String path) {

        String directoryPath = path;
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddCustomersActivity.this);
        databaseAccess.open();


        File file = new File(directoryPath);
        if (!file.exists()) {
            Toast.makeText(this, R.string.no_file_found, Toast.LENGTH_SHORT).show();
            return;
        }

        // if you want to add column in excel and import into DB, you must drop the table
        ExcelToSQLite excelToSQLite = new ExcelToSQLite(getApplicationContext(), DatabaseOpenHelper.DATABASE_NAME, false);
        // Import EXCEL FILE to SQLite
        excelToSQLite.importFromFile(directoryPath, new ExcelToSQLite.ImportListener() {
            @Override
            public void onStart() {

                loading = new ProgressDialog(AddCustomersActivity.this);
                loading.setMessage(getString(R.string.data_importing_please_wait));
                loading.setCancelable(false);
                loading.show();

            }

            @Override
            public void onCompleted(String dbName) {


                Handler mHand = new Handler();
                mHand.postDelayed(() -> {

                    loading.dismiss();
                    Toasty.success(AddCustomersActivity.this, R.string.data_successfully_imported, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddCustomersActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();


                }, 5000);


            }

            @Override
            public void onError(Exception e) {

                loading.dismiss();
                Log.d("Error : ", "" + e.getMessage());
                Toasty.error(AddCustomersActivity.this, R.string.data_import_fail, Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void fileChooser() {
        new ChooserDialog(AddCustomersActivity.this)


                .displayPath(true)
                .withFilter(false, false, "xls") //filter file type

                .withChosenListener((path, pathFile) -> {
                    onImport(path);
                })
                // to handle the back key pressed or clicked outside the dialog:
                .withOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        Log.d("CANCEL", "CANCEL");
                        dialog.cancel(); // MUST have
                    }
                })
                .build()
                .show();
    }


}
