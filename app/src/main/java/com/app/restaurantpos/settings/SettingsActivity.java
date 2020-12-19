package com.app.restaurantpos.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.cardview.widget.CardView;

import com.app.restaurantpos.R;
import com.app.restaurantpos.settings.backup.BackupActivity;
import com.app.restaurantpos.settings.categories.CategoriesActivity;
import com.app.restaurantpos.settings.payment_method.PaymentMethodActivity;
import com.app.restaurantpos.settings.shop.ShopInformationActivity;
import com.app.restaurantpos.settings.users.UsersActivity;
import com.app.restaurantpos.utils.BaseActivity;
import com.app.restaurantpos.utils.Utils;

public class SettingsActivity extends BaseActivity {


    CardView cardShopInfo,cardAllUsers, cardBackup,cardCategory,cardPaymentMethod;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.action_settings);


        cardShopInfo = findViewById(R.id.card_shop_info);
        cardBackup = findViewById(R.id.card_backup);
        cardCategory=findViewById(R.id.card_category);
        cardPaymentMethod=findViewById(R.id.card_payment_method);
        cardAllUsers=findViewById(R.id.card_all_users);



        //for interstitial ads show
        Utils utils=new Utils();
        utils.interstitialAdsShow(SettingsActivity.this);

        cardShopInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SettingsActivity.this, ShopInformationActivity.class);
                startActivity(intent);
            }
        });


        cardAllUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SettingsActivity.this, UsersActivity.class);
                startActivity(intent);
            }
        });



        cardCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SettingsActivity.this, CategoriesActivity.class);
                startActivity(intent);
            }
        });


        cardPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SettingsActivity.this, PaymentMethodActivity.class);
                startActivity(intent);
            }
        });


        cardBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SettingsActivity.this, BackupActivity.class);
                startActivity(intent);
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
