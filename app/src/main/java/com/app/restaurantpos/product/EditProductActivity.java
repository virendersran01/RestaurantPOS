package com.app.restaurantpos.product;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.restaurantpos.R;
import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.utils.BaseActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

public class EditProductActivity extends BaseActivity {

    public static EditText etxtProductCode;
    EditText etxtProductName, etxtProductCategory, etxtProductDescription, etxtProductSellPrice, etxtProductSupplier, etxtProdcutWeightUnit, etxtProductWeight;
    TextView txtUpdate, txtChooseImage, txtEditProduct;
    ImageView imgProduct, imgScanCode;
    String mediaPath, encodedImage = "N/A";
    ArrayAdapter<String> categoryAdapter, supplierAdapter, weightUnitAdapter;
    List<String> categoryNames, supplierNames, weightUnitNames;

    String selectedCategoryID, selectedSupplierID, selectedWeightUnitID, productID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.product_details);

        etxtProductName = findViewById(R.id.etxt_product_name);
        etxtProductCode = findViewById(R.id.etxt_product_code);
        etxtProductCategory = findViewById(R.id.etxt_product_category);
        etxtProductDescription = findViewById(R.id.etxt_product_description);
        etxtProductSellPrice = findViewById(R.id.etxt_product_sell_price);

        etxtProductSupplier = findViewById(R.id.etxt_supplier);
        etxtProdcutWeightUnit = findViewById(R.id.etxt_product_weight_unit);
        etxtProductWeight = findViewById(R.id.etxt_product_weight);

        txtUpdate = findViewById(R.id.txt_update);
        txtChooseImage = findViewById(R.id.txt_choose_image);
        imgProduct = findViewById(R.id.image_product);
        imgScanCode = findViewById(R.id.img_scan_code);
        txtEditProduct = findViewById(R.id.txt_edit_product);


        etxtProductName.setEnabled(false);
        etxtProductCode.setEnabled(false);
        etxtProductCategory.setEnabled(false);
        etxtProductDescription.setEnabled(false);
        etxtProductSellPrice.setEnabled(false);

        etxtProductSupplier.setEnabled(false);
        etxtProdcutWeightUnit.setEnabled(false);
        etxtProductWeight.setEnabled(false);
        txtChooseImage.setEnabled(false);
        imgProduct.setEnabled(false);
        imgScanCode.setEnabled(false);

        txtUpdate.setVisibility(View.GONE);


        productID = getIntent().getExtras().getString("product_id");


        imgProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(EditProductActivity.this, ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                startActivityForResult(intent, 1213);
            }
        });


        txtChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(EditProductActivity.this, ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                startActivityForResult(intent, 1213);
            }
        });


        imgScanCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imgScanCode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(EditProductActivity.this, EditProductScannerViewActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });

        categoryNames = new ArrayList<>();
        supplierNames = new ArrayList<>();
        weightUnitNames = new ArrayList<>();


        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(EditProductActivity.this);
        databaseAccess.open();

        //get data from local database
        List<HashMap<String, String>> productData;
        productData = databaseAccess.getProductsInfo(productID);
        String productName = productData.get(0).get("product_name");
        String productCode = productData.get(0).get("product_code");
        String productCategoryId = productData.get(0).get("product_category");
        String productDescription = productData.get(0).get("product_description");

        String productSellPrice = productData.get(0).get("product_sell_price");
        String productSupplierId = productData.get(0).get("product_supplier");
        String productImage = productData.get(0).get("product_image");


        String productWeightUnitId = productData.get(0).get("product_weight_unit_id");
        String productWeight = productData.get(0).get("product_weight");

        etxtProductName.setText(productName);
        etxtProductCode.setText(productCode);


        databaseAccess.open();
        String categoryName = databaseAccess.getCategoryName(productCategoryId);
        etxtProductCategory.setText(categoryName);

        etxtProductDescription.setText(productDescription);

        etxtProductSellPrice.setText(productSellPrice);


        databaseAccess.open();
        String supplierName = databaseAccess.getSupplierName(productSupplierId);
        etxtProductSupplier.setText(supplierName);


        databaseAccess.open();
        String weightUnitName = databaseAccess.getWeightUnitName(productWeightUnitId);
        etxtProdcutWeightUnit.setText(weightUnitName);

        etxtProductWeight.setText(productWeight);

        if (productImage != null) {
            if (productImage.length() < 6) {

                imgProduct.setImageResource(R.drawable.image_placeholder);
            } else {


                byte[] bytes = Base64.decode(productImage, Base64.DEFAULT);
                imgProduct.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

            }
        }


        selectedCategoryID = productCategoryId;
        selectedSupplierID = productSupplierId;
        selectedWeightUnitID = productWeightUnitId;
        encodedImage = productImage;


        databaseAccess.open();
        //get data from local database
        final List<HashMap<String, String>> productCategory, productSupplier, weightUnit;
        productCategory = databaseAccess.getProductCategory();

        //need to open database in every query to get data from local db
        databaseAccess.open();
        productSupplier = databaseAccess.getProductSupplier();


        //need to open database in every query to get data from local db
        databaseAccess.open();
        weightUnit = databaseAccess.getWeightUnit();

        for (int i = 0; i < productCategory.size(); i++) {

            // Get the ID of selected Country
            categoryNames.add(productCategory.get(i).get("category_name"));

        }

        for (int i = 0; i < productSupplier.size(); i++) {

            // Get the ID of selected supplier
            supplierNames.add(productSupplier.get(i).get("suppliers_name"));

        }

        for (int i = 0; i < weightUnit.size(); i++) {

            // Get the ID of selected weight unit
            weightUnitNames.add(weightUnit.get(i).get("weight_unit"));

        }


        etxtProductCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryAdapter = new ArrayAdapter<>(EditProductActivity.this, android.R.layout.simple_list_item_1);
                categoryAdapter.addAll(categoryNames);

                AlertDialog.Builder dialog = new AlertDialog.Builder(EditProductActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialogButton = (Button) dialogView.findViewById(R.id.dialog_button);
                EditText dialogInput = (EditText) dialogView.findViewById(R.id.dialog_input);
                TextView dialogTitle = (TextView) dialogView.findViewById(R.id.dialog_title);
                ListView dialogList = (ListView) dialogView.findViewById(R.id.dialog_list);


                dialogTitle.setText(R.string.product_category);
                dialogList.setVerticalScrollBarEnabled(true);
                dialogList.setAdapter(categoryAdapter);

                dialogInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        Log.d("data", s.toString());
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        categoryAdapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Log.d("data", s.toString());
                    }
                });


                final AlertDialog alertDialog = dialog.create();

                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();


                dialogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        alertDialog.dismiss();
                        final String selectedItem = categoryAdapter.getItem(position);

                        String categoryId = "0";
                        etxtProductCategory.setText(selectedItem);


                        for (int i = 0; i < categoryNames.size(); i++) {
                            if (categoryNames.get(i).equalsIgnoreCase(selectedItem)) {
                                // Get the ID of selected Country
                                categoryId = productCategory.get(i).get("category_id");
                            }
                        }


                        selectedCategoryID = categoryId;
                        Log.d("category_id", categoryId);
                    }
                });
            }
        });


        etxtProductSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supplierAdapter = new ArrayAdapter<>(EditProductActivity.this, android.R.layout.simple_list_item_1);
                supplierAdapter.addAll(supplierNames);

                AlertDialog.Builder dialog = new AlertDialog.Builder(EditProductActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialogButton = (Button) dialogView.findViewById(R.id.dialog_button);
                EditText dialogInput = (EditText) dialogView.findViewById(R.id.dialog_input);
                TextView dialogTitle = (TextView) dialogView.findViewById(R.id.dialog_title);
                ListView dialogList = (ListView) dialogView.findViewById(R.id.dialog_list);

                dialogTitle.setText("Suppliers");
                dialogList.setVerticalScrollBarEnabled(true);
                dialogList.setAdapter(supplierAdapter);

                dialogInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        Log.d("data", s.toString());
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        supplierAdapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Log.d("data", s.toString());
                    }
                });


                final AlertDialog alertDialog = dialog.create();

                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();


                dialogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        alertDialog.dismiss();
                        final String selectedItem = supplierAdapter.getItem(position);

                        String supplierId = "0";
                        etxtProductSupplier.setText(selectedItem);


                        for (int i = 0; i < supplierNames.size(); i++) {
                            if (supplierNames.get(i).equalsIgnoreCase(selectedItem)) {

                                supplierId = productSupplier.get(i).get("suppliers_id");
                            }
                        }


                        selectedSupplierID = supplierId;

                    }
                });
            }
        });


        etxtProdcutWeightUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weightUnitAdapter = new ArrayAdapter<>(EditProductActivity.this, android.R.layout.simple_list_item_1);
                weightUnitAdapter.addAll(weightUnitNames);

                AlertDialog.Builder dialog = new AlertDialog.Builder(EditProductActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialogButton = (Button) dialogView.findViewById(R.id.dialog_button);
                EditText dialogInput = (EditText) dialogView.findViewById(R.id.dialog_input);
                TextView dialogTitle = (TextView) dialogView.findViewById(R.id.dialog_title);
                ListView dialogList = (ListView) dialogView.findViewById(R.id.dialog_list);


                dialogTitle.setText(R.string.product_weight_unit);
                dialogList.setVerticalScrollBarEnabled(true);
                dialogList.setAdapter(weightUnitAdapter);

                dialogInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        Log.d("data", s.toString());
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        weightUnitAdapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Log.d("data", s.toString());
                    }
                });


                final AlertDialog alertDialog = dialog.create();

                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();


                dialogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        alertDialog.dismiss();
                        final String selectedItem = weightUnitAdapter.getItem(position);

                        String weightUnitId = "0";
                        etxtProdcutWeightUnit.setText(selectedItem);


                        for (int i = 0; i < weightUnitNames.size(); i++) {
                            if (weightUnitNames.get(i).equalsIgnoreCase(selectedItem)) {
                                // Get the ID of selected Country
                                weightUnitId = weightUnit.get(i).get("weight_id");
                            }
                        }


                        selectedWeightUnitID = weightUnitId;

                        Log.d("weight_unit", selectedWeightUnitID);
                    }
                });
            }
        });


        txtEditProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                etxtProductName.setEnabled(true);
                etxtProductCode.setEnabled(true);
                etxtProductCategory.setEnabled(true);
                etxtProductDescription.setEnabled(true);
                etxtProductSellPrice.setEnabled(true);

                etxtProductSupplier.setEnabled(true);
                etxtProdcutWeightUnit.setEnabled(true);
                etxtProductWeight.setEnabled(true);
                txtChooseImage.setEnabled(true);
                imgProduct.setEnabled(true);
                imgScanCode.setEnabled(true);


                etxtProductName.setTextColor(Color.RED);
                etxtProductCode.setTextColor(Color.RED);
                etxtProductCategory.setTextColor(Color.RED);
                etxtProductDescription.setTextColor(Color.RED);
                etxtProductSellPrice.setTextColor(Color.RED);

                etxtProductSupplier.setTextColor(Color.RED);
                etxtProdcutWeightUnit.setTextColor(Color.RED);
                etxtProductWeight.setTextColor(Color.RED);
                imgProduct.setEnabled(true);
                imgScanCode.setEnabled(true);


                txtUpdate.setVisibility(View.VISIBLE);
                txtEditProduct.setVisibility(View.GONE);


            }
        });


        txtUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = etxtProductName.getText().toString();
                String productCode = etxtProductCode.getText().toString();
                String productCategory = selectedCategoryID;
                String productDescription = etxtProductDescription.getText().toString();

                String productSellPrice = etxtProductSellPrice.getText().toString();

                String productSupplier = selectedSupplierID;
                String productWeightUnitId = selectedWeightUnitID;
                String productWeight = etxtProductWeight.getText().toString();


                if (productName.isEmpty()) {
                    etxtProductName.setError(getString(R.string.product_name_cannot_be_empty));
                    etxtProductName.requestFocus();
                } else if (productCode.isEmpty()) {
                    etxtProductCode.setError(getString(R.string.product_code_cannot_be_empty));
                    etxtProductCode.requestFocus();
                } else if (productCategory.isEmpty()) {
                    etxtProductCategory.setError(getString(R.string.product_category_cannot_be_empty));
                    etxtProductCategory.requestFocus();
                } else if (productSellPrice.isEmpty()) {
                    etxtProductSellPrice.setError(getString(R.string.product_sell_price_cannot_be_empty));
                    etxtProductSellPrice.requestFocus();
                } else if (productSupplier.isEmpty()) {
                    etxtProductSupplier.setError(getString(R.string.product_supplier_cannot_be_empty));
                    etxtProductSupplier.requestFocus();
                } else if (productWeight.isEmpty()) {
                    etxtProductWeight.setError(getString(R.string.product_weight_cannot_be_empty));
                    etxtProductWeight.requestFocus();
                } else {
                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(EditProductActivity.this);
                    databaseAccess.open();

                    boolean check = databaseAccess.updateProduct(productName, productCode, productCategory, productDescription, productSellPrice, productSupplier, encodedImage, productWeightUnitId, productWeight, productID);

                    if (check) {
                        Toasty.success(EditProductActivity.this, R.string.update_successfully, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditProductActivity.this, ProductActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    } else {

                        Toasty.error(EditProductActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

                    }


                }

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            // When an Image is picked
            if (requestCode == 1213 && resultCode == RESULT_OK && null != data) {


                mediaPath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
                Bitmap selectedImage = BitmapFactory.decodeFile(mediaPath);
                imgProduct.setImageBitmap(selectedImage);

                encodedImage = encodeImage(selectedImage);


            }


        } catch (Exception e) {
            Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
        }

    }


    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
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
