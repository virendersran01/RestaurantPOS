package com.app.restaurantpos.settings.categories;

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

public class AddCategoryActivity extends BaseActivity {


    EditText etxtCategoryName;
    TextView txtAddCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.add_category);

        etxtCategoryName = findViewById(R.id.etxt_category_name);
        txtAddCategory = findViewById(R.id.txt_add_category);


        txtAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String categoryName = etxtCategoryName.getText().toString().trim();

                if (categoryName.isEmpty()) {
                    etxtCategoryName.setError(getString(R.string.enter_category_name));
                    etxtCategoryName.requestFocus();
                } else {

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddCategoryActivity.this);
                    databaseAccess.open();

                    boolean check = databaseAccess.addCategory(categoryName);

                    if (check) {
                        Toasty.success(AddCategoryActivity.this, R.string.category_added_successfully, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddCategoryActivity.this, CategoriesActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    } else {

                        Toasty.error(AddCategoryActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

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
