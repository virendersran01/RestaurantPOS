package com.app.restaurantpos;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.app.restaurantpos.about.AboutActivity;
import com.app.restaurantpos.customers.CustomersActivity;
import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.expense.ExpenseActivity;
import com.app.restaurantpos.login.LoginActivity;
import com.app.restaurantpos.orders.OrdersActivity;
import com.app.restaurantpos.pos.PosActivity;
import com.app.restaurantpos.product.ProductActivity;
import com.app.restaurantpos.report.ReportActivity;
import com.app.restaurantpos.settings.SettingsActivity;
import com.app.restaurantpos.suppliers.SuppliersActivity;
import com.app.restaurantpos.utils.BaseActivity;
import com.app.restaurantpos.utils.LocaleManager;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.Slidetop;

public class HomeActivity extends BaseActivity {


    CardView cardCustomers, cardProducts, cardSupplier, cardPos, cardOrderList, cardReport, cardSettings, cardExpense, cardAbout, cardLogout;
    //for double back press to exit
    private static final int TIME_DELAY = 2000;
    private static long backPressed;

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String userType;
    TextView txtShopName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().setTitle(R.string.app_name);
        cardCustomers = findViewById(R.id.card_customers);
        cardSupplier = findViewById(R.id.card_suppliers);
        cardProducts = findViewById(R.id.card_products);
        cardPos = findViewById(R.id.card_pos);
        cardOrderList = findViewById(R.id.card_all_orders);
        cardReport = findViewById(R.id.card_reports);
        cardSettings = findViewById(R.id.card_settings);
        cardExpense = findViewById(R.id.card_expense);
        cardAbout = findViewById(R.id.card_about_us);
        cardLogout = findViewById(R.id.card_logout);
        txtShopName = findViewById(R.id.txt_shop_name);


        sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();

        userType = sp.getString(Constant.SP_USER_TYPE, "");


        if (Build.VERSION.SDK_INT >= 23) //Android MarshMellow Version or above
        {
            requestPermission();

        }

//        Admob initialization
        MobileAds.initialize(this, initializationStatus -> {
            //add your action here if need
        });

        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(HomeActivity.this);
        databaseAccess.open();

        //get data from local database
        List<HashMap<String, String>> shopData;
        shopData = databaseAccess.getShopInformation();

        String shopName = shopData.get(0).get("shop_name");
        txtShopName.setText(shopName);


        cardCustomers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CustomersActivity.class);
                startActivity(intent);


            }
        });

        cardSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SuppliersActivity.class);
                startActivity(intent);


            }
        });


        cardProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProductActivity.class);
                startActivity(intent);


            }
        });


        cardPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PosActivity.class);
                startActivity(intent);


            }
        });

        cardOrderList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, OrdersActivity.class);
                startActivity(intent);


            }
        });


        cardReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userType.equals(Constant.ADMIN) || userType.equals(Constant.MANAGER)) {
                    Intent intent = new Intent(HomeActivity.this, ReportActivity.class);
                    startActivity(intent);
                } else {
                    Toasty.error(HomeActivity.this, R.string.you_dont_have_permission_to_access_this_page, Toast.LENGTH_SHORT).show();
                }

            }
        });


        cardExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userType.equals("admin") || userType.equals("manager")) {
                    Intent intent = new Intent(HomeActivity.this, ExpenseActivity.class);
                    startActivity(intent);
                } else {
                    Toasty.error(HomeActivity.this, R.string.you_dont_have_permission_to_access_this_page, Toast.LENGTH_SHORT).show();
                }
            }
        });


        cardSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userType.equals("admin")) {
                    Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                    startActivity(intent);
                } else {
                    Toasty.error(HomeActivity.this, R.string.you_dont_have_permission_to_access_this_page, Toast.LENGTH_SHORT).show();
                }
            }
        });


        cardAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
                startActivity(intent);

            }
        });


        cardLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(HomeActivity.this);
                dialogBuilder
                        .withTitle(getString(R.string.logout))
                        .withMessage(R.string.want_to_logout_from_app)
                        .withEffect(Slidetop)
                        .withDialogColor("#f29161") //use color code for dialog
                        .withButton1Text(getString(R.string.yes))
                        .withButton2Text(getString(R.string.cancel))
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                editor.putString(Constant.SP_PHONE, "");
                                editor.putString(Constant.SP_PASSWORD, "");
                                editor.putString(Constant.SP_USER_NAME, "");
                                editor.putString(Constant.SP_USER_TYPE, "");
                                editor.apply();

                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                                dialogBuilder.dismiss();
                            }
                        })
                        .setButton2Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialogBuilder.dismiss();
                            }
                        })
                        .show();


            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.language_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {


            case R.id.local_french:
                setNewLocale(this, LocaleManager.FRENCH);
                return true;


            case R.id.local_english:
                setNewLocale(this, LocaleManager.ENGLISH);
                return true;


            case R.id.local_bangla:
                setNewLocale(this, LocaleManager.BANGLA);
                return true;

            case R.id.local_spanish:
                setNewLocale(this, LocaleManager.SPANISH);
                return true;
            default:
                Log.d("Default", "default");

        }

        return super.onOptionsItemSelected(item);
    }


    private void setNewLocale(AppCompatActivity mContext, @LocaleManager.LocaleDef String language) {
        LocaleManager.setNewLocale(this, language);
        Intent intent = mContext.getIntent();
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    //double back press to exit
    @Override
    public void onBackPressed() {
        if (backPressed + TIME_DELAY > System.currentTimeMillis()) {

            finishAffinity();

        } else {
            Toasty.info(this, R.string.press_once_again_to_exit,
                    Toast.LENGTH_SHORT).show();
        }
        backPressed = System.currentTimeMillis();
    }


    private void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {

                            //write your action if needed
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings

                        }
                    }


                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(error -> Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
    }
}
