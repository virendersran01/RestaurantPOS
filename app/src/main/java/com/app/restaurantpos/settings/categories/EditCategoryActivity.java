package com.app.restaurantpos.settings.categories;

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

public class EditCategoryActivity extends BaseActivity {


    EditText etxtCategoryName;
    TextView txtUpdateCategory, txtEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.edit_category);

        txtEdit = findViewById(R.id.txt_edit_category);
        txtUpdateCategory = findViewById(R.id.txt_update_category);
        etxtCategoryName = findViewById(R.id.etxt_category_name);

        String categoryId = getIntent().getExtras().getString("category_id");
        String categoryName = getIntent().getExtras().getString("category_name");


        etxtCategoryName.setText(categoryName);
        etxtCategoryName.setEnabled(false);
        txtUpdateCategory.setVisibility(View.INVISIBLE);


        txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etxtCategoryName.setEnabled(true);
                txtUpdateCategory.setVisibility(View.VISIBLE);
                etxtCategoryName.setTextColor(Color.RED);

                txtEdit.setVisibility(View.GONE);

            }
        });


        txtUpdateCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoryName = etxtCategoryName.getText().toString().trim();

                if (categoryName.isEmpty()) {
                    etxtCategoryName.setError(getString(R.string.enter_category_name));
                    etxtCategoryName.requestFocus();
                } else {

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(EditCategoryActivity.this);
                    databaseAccess.open();

                    boolean check = databaseAccess.updateCategory(categoryId, categoryName);

                    if (check) {
                        Toasty.success(EditCategoryActivity.this, R.string.category_updated, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditCategoryActivity.this, CategoriesActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    } else {

                        Toasty.error(EditCategoryActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

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
