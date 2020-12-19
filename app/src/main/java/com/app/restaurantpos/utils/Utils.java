package com.app.restaurantpos.utils;

import android.content.Context;

import com.app.restaurantpos.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class Utils {


    //call this function where you want to show interstitial ads
    public void interstitialAdsShow(Context context) {

        InterstitialAd interstitialAd;
        interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(context.getString(R.string.admob_interstitial_ads_id));
        AdRequest request = new AdRequest.Builder().build();
        interstitialAd.loadAd(request);


        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }
            }
        });
    }

}
